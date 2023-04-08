package es.upm.bot.news_scraper.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upm.bot.news_scraper.elements.Article;
import es.upm.bot.news_scraper.elements.Topic;
import es.upm.bot.news_scraper.entity.Provider;
import es.upm.bot.news_scraper.entity.ProviderId;
import es.upm.bot.news_scraper.entity.Server;
import es.upm.bot.news_scraper.entity.User;
import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;
import es.upm.bot.news_scraper.exceptions.FirstParagraphNotFoundException;
import es.upm.bot.news_scraper.exceptions.ImageNotFoundException;
import es.upm.bot.news_scraper.exceptions.ProviderNotFoundException;
import es.upm.bot.news_scraper.exceptions.TopicsNotFoundException;
import es.upm.bot.news_scraper.exceptions.UrlNotAccessibleException;
import es.upm.bot.news_scraper.repositories.ProviderRepository;
import es.upm.bot.news_scraper.repositories.ServerRepository;
import es.upm.bot.news_scraper.repositories.UserRepository;

@Service
public class TechnicalScraper {

	private int NEWS_LIMIT_COMPLETE = 20;
	private int NEWS_LIMIT_PRIV = 5;

	@Autowired
	private ProviderRepository providerRepository;

	@Autowired
	private ServerRepository serverRepository;

	@Autowired
	private UserRepository userRepository;

	private Map<String, Queue<Article>> userNews;

	private final ReentrantLock mutex;

	public TechnicalScraper() {
		userNews = new HashMap<>();
		mutex = new ReentrantLock();
	}

	private Document generateDoc(String webPage, String url) throws UrlNotAccessibleException {
		String html = "";
		try{
			html = Jsoup.connect(urlCheck(webPage, url)).get().html();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return Jsoup.parse(html);
	}

	public String urlCheck(String base, String url) throws UrlNotAccessibleException {	
		String finalUrl = url;
		if(!url.startsWith("https://") && !url.startsWith("http://"))
			finalUrl =  base + url;	

		isUrlAccessible(finalUrl);


		return finalUrl;
	}

	public static boolean isUrlAccessible(String url) throws UrlNotAccessibleException {
		HttpClient httpClient = HttpClient.newHttpClient();

		HttpResponse<String> response = null;
		try {
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | IllegalArgumentException | InterruptedException e) {

			throw new UrlNotAccessibleException();
		}
		return response.statusCode() < 400;

	}

	@Transactional
	public void changeProvider(String username, Long serverID, String provider) throws ProviderNotFoundException{

		User user = searchUser(username, serverID);
		System.err.println("Cambio provedor de " + username + "de " + user.getProvider() + " a " + provider);
		Optional<Provider> prov = providerRepository.findById(new ProviderId(serverID, provider));
		if (prov.isPresent()) {
			user.setProvider(provider);
			mutex.lock();
			try {
				userNews.get(username).clear();
			} finally {
				mutex.unlock();
			}
			System.err.println("Nuevo provedor de " + username + " es " + user.getProvider());
			userRepository.save(user);
		}
		else
			throw new ProviderNotFoundException();

	}


	public String getArticles(String username, Long serverID) throws ArticlesNotFoundException, UrlNotAccessibleException {
		User user = searchUser(username, serverID);

		final String webPage = user.getProvider();

		Document doc = generateDoc(webPage, webPage);

		Provider provider = providerRepository.findById(new ProviderId(serverID, webPage)).get();

		String articleType = provider.getTipoArticulo();
		System.out.println("articleType " + articleType);

		mutex.lock();
		try {
			userNews.get(username).clear();
		} finally {
			mutex.unlock();
		}


		Elements articles = null;
		switch(articleType) {
		case "Tag":{
			articles = doc.getElementsByTag(provider.getValorArticulo());
			break;
		}

		case "Class":{
			articles = doc.getElementsByClass(provider.getValorArticulo());
			break;
		}

		case "Attribute":{
			articles = doc.getElementsByAttributeValueContaining(provider.getAttributeNameArticulo()
					,provider.getValorArticulo());
			break;
		}

		default:{
			articles = doc.getElementsByTag("article");
		}
		}
		if(articles == null)
			throw new ArticlesNotFoundException();


		CountDownLatch latch = new CountDownLatch(1);

		final Elements articlesFinal = articles;
		new Thread(() -> {
			int i = 0;
			for(Element e : articlesFinal) {
				if(i++ >= NEWS_LIMIT_PRIV) {
					latch.countDown();
				}
				
				if(i == NEWS_LIMIT_COMPLETE) {
					break;
				}

				Article a;
				try {
					a = getArticleFromElement(e, webPage, provider, doc);
				} catch (ImageNotFoundException | FirstParagraphNotFoundException | UrlNotAccessibleException e1) {
					i--;
					continue;
				}

				mutex.lock();
				try {
					if(a != null) {
						userNews.get(username).add(a);
					}
				} finally {
					mutex.unlock();
				}
			}	
			latch.countDown();
		}).start();;

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		return getNextArticles(username);
	}

	public String getNextArticles(String username) {
		Queue<Article> articles = new LinkedList<>();


		for(int i = 0 ; i < NEWS_LIMIT_PRIV; i ++) {
			if(userNews.get(username).isEmpty()) {
				break;
			}

			mutex.lock();
			try {
				articles.add(userNews.get(username).poll());
			} finally {
				mutex.unlock();
			}


		}
		String res = articlesToJson(articles);
		System.out.println("LES ENVIO LOS PRIMEROS " + res);
		return res;
	}

	private String getFirstParagraph(String articleLink, Provider provider, Document doc) throws FirstParagraphNotFoundException, UrlNotAccessibleException {	
		Elements parrafos = null;

		switch(provider.getTipoParrafo()) {
		case "Tag":{
			parrafos = doc.getElementsByTag(provider.getValorParrafo())
					.first().getElementsByTag("p");
			break;
		}

		case "Class":{
			parrafos = generateDoc(provider.getProviderId().getLink(), articleLink).getElementsByClass(provider.getValorParrafo());
			break;
		}

		case "Attribute":{
			parrafos = doc.getElementsByAttributeValueContaining(provider.getAttributeNameParrafo()
					,provider.getValorParrafo()).first().getElementsByTag("p");
			break;
		}

		default:{
			parrafos = generateDoc(provider.getProviderId().getLink(), articleLink).select("article p");

		}
		}

		if(parrafos.size() == 0)
			throw new FirstParagraphNotFoundException();
		return parrafos.first().text();
	}

	public String getTopics(String username, Long serverID) throws UrlNotAccessibleException {
		ArrayList<Topic> topicList = new ArrayList<>();

		User user = searchUser(username, serverID);
		String webPage = user.getProvider();
		Document doc = generateDoc(webPage, webPage);

		Provider provider = providerRepository.findById(new ProviderId(serverID, webPage)).get();
		String topicType= provider.getTipoTopic();


		Elements topics = null;

		switch(topicType) {
		case "Tag":{
			topics = doc.getElementsByTag(provider.getValorTopic());
			break;
		}

		case "Class":{
			topics = doc.getElementsByClass(provider.getValorTopic());
			break;
		}

		case "Attribute":{
			topics = doc.getElementsByAttributeValueContaining(provider.getAttributeNameTopic()
					,provider.getValorTopic());
			break;
		}
		}

		for(Element e : topics) {
			String topic = e.text();
			String href = e.attr("href");

			if(!href.equals("")) {
				topicList.add(new Topic(topic, href));
				System.out.println(e.text());
				System.out.println(e.attr("href"));
			}
		}

		return topicsToJson(topicList);
	}

	public String getArticlesFromTopic(String username, Long serverID, String topicLink) throws ArticlesNotFoundException, UrlNotAccessibleException{

		final String webPage = topicLink;
		Document doc = generateDoc(webPage, webPage);

		User user = searchUser(username, serverID);

		mutex.lock();
		try {
			userNews.get(username).clear();
		} finally {
			mutex.unlock();
		}



		Provider provider = providerRepository.findById(new ProviderId(serverID, user.getProvider())).get();


		Elements articles = doc.getElementsByTag("article");
		if(articles.size() == 0)
			throw new ArticlesNotFoundException();

		CountDownLatch latch = new CountDownLatch(1);

		final Elements articlesFinal = articles;
		new Thread(() -> {
			int i = 0;
			for(Element e : articlesFinal) {
				if(i++ >= NEWS_LIMIT_PRIV) {
					latch.countDown();
				}
				
				if(i == NEWS_LIMIT_COMPLETE) {
					break;
				}
				
				Article a;
				try {
					a = getArticleFromElement(e, webPage, provider, doc);
				} catch (ImageNotFoundException | FirstParagraphNotFoundException | UrlNotAccessibleException e1) {
					i--;
					continue;
				} 
				mutex.lock();
				try {
					if(a != null) {
						userNews.get(username).add(a);
					}
				} finally {
					mutex.unlock();
				}
			}
		}).start();;


		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		return getNextArticles(username);
	}



	private Article getArticleFromElement(Element e, String webPage, Provider provider, Document doc) throws ImageNotFoundException, FirstParagraphNotFoundException, UrlNotAccessibleException {
		String title = e.getElementsByTag("header").text();

		Element article = e.getElementsByAttribute("href").first();
		if(article == null)
			return null;
		String link = urlCheck(webPage, article.attr("href"));
		String clase = e.className();
		String content = getFirstParagraph(link, provider, doc);
		String image = urlCheck(webPage, searchImage(link, webPage));
		String favicon = urlCheck(webPage, searchFavicon(link, webPage));
		return new Article(title, link, clase, content, image, favicon);
	}


	private String searchImage(String articleLink, String webPage) throws ImageNotFoundException, UrlNotAccessibleException {
		String image = "";
		try {
			Elements articles = generateDoc(webPage, articleLink).getElementsByTag("article").first().getElementsByTag("img");
			image = articles.first().attr("src");
		}
		catch(NullPointerException e) {
			image = "";
			throw new ImageNotFoundException();		
		}

		return image;
	}

	private String searchFavicon(String articleLink, String webPage) {
		return "https://www.google.com/s2/favicons?domain="+ webPage +"&sz=128";
	}

	@Transactional
	private User searchUser(String username, Long serverID){
		Optional<User> user = userRepository.findByUserIdUsernameAndUserIdServerID(username, serverID);

		if(user.isPresent()) {
			userNews.put(username, new LinkedList<>());
			return user.get();
		}
		else {
			userNews.put(username, new LinkedList<>());
			User newUser = new User(username, serverID, "https://www.elmundo.es/");
			userRepository.save(newUser);
			return newUser;
		}
	}

	private String articlesToJson(Queue<Article> queue) {
		OutputStream os = new ByteArrayOutputStream(5000);
		JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
		JsonGenerator generator = factory.createGenerator(os);
		generator.writeStartArray();
		for(Article a : queue) {		
			generator
			.writeStartObject()
			.write("title", a.getTitle())
			.write("image", a.getImage())
			.write("content", a.getContent())
			.write("authors", "autores")
			.write("link", a.getLink())
			.write("favicon", a.getFavicon())
			.writeEnd();
		}
		generator.writeEnd();
		generator.close();

		return os.toString();

	}

	private String topicsToJson(ArrayList<Topic> topics) {
		OutputStream os = new ByteArrayOutputStream(5000);
		JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
		JsonGenerator generator = factory.createGenerator(os);
		generator.writeStartArray();
		for(Topic t : topics) {		
			generator
			.writeStartObject()
			.write("name", t.getName())
			.write("link", t.getLink())
			.writeEnd();
		}
		generator.writeEnd();
		generator.close();

		return os.toString();

	}

	private String providersToJson(Long serverID) {
		OutputStream os = new ByteArrayOutputStream(5000);
		JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
		JsonGenerator generator = factory.createGenerator(os);
		generator.writeStartArray();
		for(Provider provider : providerRepository.findByProviderId_ServerID(serverID).get()) {	
			generator
			.writeStartObject()
			.write("webSite", provider.getProviderId().getLink())
			.write("name", provider.getNombre())
			.writeEnd();
		}
		generator.writeEnd();
		generator.close();

		return os.toString();

	}

	public Provider providersFromJson(String body, Long serverID){	
		StringReader sr = new StringReader(body);
		JsonReader reader = Json.createReader(sr);
		JsonObject obj = reader.readObject();

		Provider provider = new Provider(obj.getString("webSite"), obj.getString("webSiteName"), serverID, 
				obj.getString("usoArticulo"), obj.getString("tipoArticulo"), obj.getString("attributeNameArticulo"), obj.getString("valorArticulo"), 
				obj.getString("usoParrafo"), obj.getString("tipoParrafo"), obj.getString("attributeNameParrafo"), obj.getString("valorParrafo"), 
				obj.getString("usoTopic"), obj.getString("tipoTopic"), obj.getString("attributeNameTopic"), obj.getString("valorTopic")); 
		return provider;

	}

	@Transactional
	public void addProvider(String body, Long serverID) throws ArticlesNotFoundException, TopicsNotFoundException, UrlNotAccessibleException, FirstParagraphNotFoundException {

		checkNewProvider(body, serverID);

		providerRepository.save(providersFromJson(body, serverID));
	}

	public String getProviders(Long serverID) {
		return providersToJson(serverID);
	}

	public void setMax(int max) {
		this.NEWS_LIMIT_PRIV = max;
	}

	@Transactional
	public void createServer(Long serverID, String serverName) {

		Optional<Server> server = serverRepository.findById(serverID);
		if(server.isEmpty()) {
			System.out.println("Nuevo server " + serverID + " " + serverName);
			serverRepository.save(new Server(serverID, serverName));
			loadProviders(serverID);
		}

		System.out.println("Estamos en server " + serverID + " " + serverName);
	}

	private void loadProviders(Long serverID) {
		Provider provider = new Provider("El mundo", "https://www.elmundo.es/", serverID, 
				"Article", "Tag", "", "article", 
				"FirstParagraph", "", "", "", 
				"Topic", "Class", "", "ue-c-main-navigation__link ue-c-main-navigation__link-dropdown js-accessible-link"); 

		providerRepository.save(provider);
	}

	public String getProvidersWs(Long serverID) {
		List<Provider> providers = providerRepository.findByProviderId_ServerID(serverID).get();
		StringBuilder sb = new StringBuilder("[");
		for (Provider prov : providers) {
			sb.append(prov.toJson()).append(",");
		}
		if (providers.size() > 0) {
			sb.setLength(sb.length() - 1);
		}
		sb.append("]");
		return sb.toString();
	}

	public String getServersWs(String username) {
		List<Server> servers = userRepository.findByUserIdUsername(username).get().stream()
				.map(user -> serverRepository.findById(user.getUserId().getServerID()).get()).toList();

		StringBuilder sb = new StringBuilder("[");
		for (Server server : servers) {
			sb.append(server.toJson()).append(",");
		}
		if (servers.size() > 0) {
			sb.setLength(sb.length() - 1);
		}
		sb.append("]");
		return sb.toString();
	}

	@Transactional
	public void removeProvider(Long serverID, String provider) {
		providerRepository.deleteByProviderId_ServerIDAndProviderId_Link(serverID, provider);

	}

	public void checkNewProvider(String body, Long serverID) throws  ArticlesNotFoundException, TopicsNotFoundException, UrlNotAccessibleException, FirstParagraphNotFoundException {


		Provider provider = providersFromJson(body, serverID);
		String webPage = provider.getProviderId().getLink();
		Document doc;
		try {
			doc = generateDoc(webPage, webPage);
		} catch (UrlNotAccessibleException e2) {
			throw new UrlNotAccessibleException("link");
		}

		String articleType = provider.getTipoArticulo();

		Elements articles = null;
		switch(articleType) {
		case "Tag":{
			articles = doc.getElementsByTag(provider.getValorArticulo());
			break;
		}

		case "Class":{
			articles = doc.getElementsByClass(provider.getValorArticulo());
			break;
		}

		case "Attribute":{
			articles = doc.getElementsByAttributeValueContaining(provider.getAttributeNameArticulo()
					,provider.getValorArticulo());
			break;
		}

		default:{
			articles = doc.getElementsByTag("article");
		}
		}
		if(articles == null)
			throw new ArticlesNotFoundException("article");

		int i = 0;
		int articleNumber = 0;
		for(Element e : articles) {
			if(i++ >= NEWS_LIMIT_PRIV) {
				break;
			}
			Article a = null;
			try {
				a = getArticleFromElement(e, webPage, provider, doc);
			} catch (ImageNotFoundException | UrlNotAccessibleException e1) {
				i--;
				continue;
			} catch (FirstParagraphNotFoundException e1) {
				throw new FirstParagraphNotFoundException("paragraph");
			}
			if(a != null) {
				articleNumber++;
			}
		}

		if(articleNumber == 0)
			throw new ArticlesNotFoundException("article");

		Elements topics = null;

		String topicType = provider.getTipoTopic();
		switch(topicType) {
		case "Tag":{
			topics = doc.getElementsByTag(provider.getValorTopic());
			break;
		}

		case "Class":{
			topics = doc.getElementsByClass(provider.getValorTopic());
			break;
		}

		case "Attribute":{
			topics = doc.getElementsByAttributeValueContaining(provider.getAttributeNameTopic()
					,provider.getValorTopic());
			break;
		}
		}

		if(topics == null)
			throw new TopicsNotFoundException("topic");

		int topicNumber = 0;
		for(Element e : topics) {
			String href = e.attr("href");

			if(!href.equals("")) {
				topicNumber++;
			}
		}

		if(topicNumber == 0)
			throw new TopicsNotFoundException("topic");

	}



}
