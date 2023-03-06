package es.upm.bot.news_scraper.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.upm.bot.news_scraper.elements.Article;
import es.upm.bot.news_scraper.elements.Topic;
import es.upm.bot.news_scraper.entity.Provider;
import es.upm.bot.news_scraper.entity.Server;
import es.upm.bot.news_scraper.entity.User;
import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;
import es.upm.bot.news_scraper.exceptions.FirstParagraphNotFoundException;
import es.upm.bot.news_scraper.exceptions.ImageNotFoundException;
import es.upm.bot.news_scraper.exceptions.ProviderNotFoundException;
import es.upm.bot.news_scraper.repositories.ProviderRepository;
import es.upm.bot.news_scraper.repositories.ServerRepository;
import es.upm.bot.news_scraper.repositories.UserRepository;

@Service
public class TechnicalScraper {

	private String defaultWebPage;
	private int NEWS_LIMIT_COMPLETA = 50;
	private int NEWS_LIMIT_PRIV = 5;

	@Autowired
	private ProviderRepository providerRepository;

	@Autowired
	private ServerRepository serverRepository;

	@Autowired
	private UserRepository userRepository;

	private Map<String, Queue<Article>> userNews;

	public TechnicalScraper() {
		userNews = new HashMap<>();
	}

	private Document generateDoc(String webPage, String url) {
		String html = "";
		try{
			html = Jsoup.connect(urlCheck(webPage, url)).get().html();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Jsoup.parse(html);
	}

	private String urlCheck(String webPage, String url) {	
		if(!url.startsWith("https://") && !url.startsWith("http://"))
			return webPage + url;	
		return url;
	}

	public void changeProvider(String username, Long serverID, String provider) throws ProviderNotFoundException{

		User user = searchUser(username, serverID);
		System.err.println("Cambio provedor de " + username + "de " + user.getProvider() + " a " + provider);
		Optional<Provider> prov = providerRepository.findById(provider);
		if (prov.isPresent()) {
			user.setProvider(provider);
			System.err.println("Nuevo provedor de " + username + " es " + user.getProvider());
			userRepository.save(user);
		}
		else
			throw new ProviderNotFoundException();

	}


	public String getArticles(String username, Long serverID) throws ArticlesNotFoundException, ImageNotFoundException, FirstParagraphNotFoundException {

		Document doc;

		User user = searchUser(username, serverID);


		final String webPage = user.getProvider();
		doc = generateDoc(webPage, webPage);


		Provider provider = providerRepository.findById(webPage).get();


		String articleType = provider.getTipoArticulo();
		System.out.println("articleType " + articleType);
		String firstParagraphType = provider.getTipoParrafo();

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
				Article a;
				try {
					a = getArticleFromElement(e, webPage, provider, doc);
				} catch (ImageNotFoundException | FirstParagraphNotFoundException e1) {
					i--;
					continue;
				}
				if(a != null) {
					userNews.get(username).add(a);
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

	public String getNextArticles(String username) {
		Queue<Article> articles = new LinkedList<>();
		for(int i = 0 ; i < NEWS_LIMIT_PRIV; i ++) {
			if(userNews.get(username).isEmpty()) {
				break;
			}
			articles.add(userNews.get(username).poll());

		}
		String res = articlesToJson(articles);
		System.out.println("LES ENVIO LOS PRIMEROS " + res);
		return res;
	}

	private String getFirstParagraph(String articleLink, Provider provider, Document doc) throws FirstParagraphNotFoundException {	
		Elements parrafos = null;

		switch(provider.getTipoParrafo()) {
		case "Tag":{
			parrafos = doc.getElementsByTag(provider.getValorParrafo())
					.first().getElementsByTag("p");
			break;
		}

		case "Class":{
			parrafos = generateDoc(provider.getLink(), articleLink).getElementsByClass(provider.getValorParrafo());
			break;
		}

		case "Attribute":{
			parrafos = doc.getElementsByAttributeValueContaining(provider.getAttributeNameParrafo()
					,provider.getValorParrafo()).first().getElementsByTag("p");
			break;
		}

		default:{
			parrafos = generateDoc(provider.getLink(), articleLink).select("article p");

		}
		}

		if(parrafos.size() == 0)
			throw new FirstParagraphNotFoundException();
		return parrafos.first().text();
	}

	public String getTopics(String username, Long serverID) {
		ArrayList<Topic> topicList = new ArrayList<>();

		User user = searchUser(username, serverID);
		String webPage = user.getProvider();
		Document doc = generateDoc(webPage, webPage);

		Provider provider = providerRepository.findById(webPage).get();
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

	public String getArticlesFromTopic(String username, Long serverID, String topicLink) throws ArticlesNotFoundException{

		final String webPage = topicLink;
		Document doc = generateDoc(webPage, webPage);

		User user = searchUser(username, serverID);
		userNews.get(username).clear();


		Provider provider = providerRepository.findById(user.getProvider()).get();


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
				Article a;
				try {
					a = getArticleFromElement(e, webPage, provider, doc);
				} catch (ImageNotFoundException | FirstParagraphNotFoundException e1) {
					i--;
					continue;
				}
				if(a != null) {
					userNews.get(username).add(a);
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



	private Article getArticleFromElement(Element e, String webPage, Provider provider, Document doc) throws ImageNotFoundException, FirstParagraphNotFoundException {
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


	private String searchImage(String articleLink, String webPage) throws ImageNotFoundException {
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

	private User searchUser(String username, Long serverID){
		Optional<User> user = userRepository.findByUsernameAndServerID(username, serverID);

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
		for(Provider provider : providerRepository.findByServerID(serverID).get()) {	
			generator
			.writeStartObject()
			.write("webSite", provider.getLink())
			.write("name", provider.getNombre())
			.writeEnd();
		}
		generator.writeEnd();
		generator.close();

		return os.toString();

	}

	//	public void providersFromJson(String body){	
	//		StringReader sr = new StringReader(body);
	//		JsonReader reader = Json.createReader(sr);
	//		JsonArray array = reader.readArray();
	//
	//		for(JsonValue jo : array) {
	//			JsonArray websitePropertiesPar = jo.asJsonArray();
	//			boolean webSiteExtracted = false;
	//			boolean webSiteNameExtracted = false;
	//			String webSite = "";
	//			String webSiteName = "";
	//			ArrayList<Property> propertyList = new ArrayList<>();
	//			for(JsonValue par : websitePropertiesPar) {
	//				if(!webSiteExtracted) {
	//					webSite = par.asJsonObject().getString("webSite");
	//					webSiteExtracted = true;
	//				}
	//				else if(!webSiteNameExtracted) {
	//					webSiteName = par.asJsonObject().getString("webSiteName");
	//					webSiteNameExtracted = true;
	//				}
	//				else {
	//					JsonArray arr = par.asJsonArray();
	//
	//					for(JsonValue jo2 : arr) {
	//						JsonObject obj = jo2.asJsonObject();	
	//						Property property = new Property(obj.getString("use"), obj.getString("type"),
	//								obj.getString("attributeName"), obj.getString("value"));
	//						propertyList.add(property);
	//					}
	//					scrapingPropertiesList.put(webSite,new ScrapingProperties(webSiteName, webSite, propertyList));	
	//				}
	//			}
	//		}
	//
	//	}

	public void providersFromJson(String body){	
		StringReader sr = new StringReader(body);
		JsonReader reader = Json.createReader(sr);
		JsonArray array = reader.readArray();

		for(JsonValue jo : array) {
			JsonObject obj = jo.asJsonObject();

			new Provider(obj.getString("webSite"), obj.getString("webSiteName"), 1111111111111L, 
					obj.getString("usoArticulo"), obj.getString("tipoArticulo"), obj.getString("attributeNameArticulo"), obj.getString("valorArticulo"), 
					obj.getString("usoParrafo"), obj.getString("tipoParrafo"), obj.getString("attributeNameParrafo"), obj.getString("valorParrafo"), 
					obj.getString("usoTopic"), obj.getString("tipoTopic"), obj.getString("attributeNameTopic"), obj.getString("valorTopic")); 

		}

	}


	//	public Map<String, ScrapingProperties> getMsp() {
	//		return msp;
	//	}
	//
	//	public void addScrapingProperty(String webPage, ScrapingProperties sp) {
	//		msp.put(webPage, sp);
	//	}
	//
	//	public void addProviders(Map<String, ScrapingProperties> providers) {
	//		msp.putAll(providers);
	//	}
	//
	//	public void addProviders(String providers) {
	//		msp.putAll(providersFromJson(providers));
	//	}

	public String getProviders(Long serverID) {
		return providersToJson(serverID);
	}

	public int getNEWS_LIMIT_COMPLETA() {
		return NEWS_LIMIT_COMPLETA;
	}

	public void setMax(int max) {
		this.NEWS_LIMIT_PRIV = max;
	}

	public void createServer(String message) {
		String[] split = message.split(",");
		Long serverID = Long.parseLong(split[0]);
		String serverName = split[1];


		Optional<Server> server = serverRepository.findById(serverID);
		if(server.isEmpty()) {
			System.out.println("Nuevo server " + serverID + " " + serverName);
			serverRepository.save(new Server(serverID, serverName));
		}
		System.out.println("Estamos en server " + serverID + " " + serverName);
	}







}
