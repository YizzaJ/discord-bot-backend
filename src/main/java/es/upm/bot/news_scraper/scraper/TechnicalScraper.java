package es.upm.bot.news_scraper.scraper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.stereotype.Service;

import es.upm.bot.news_scraper.elements.Article;
import es.upm.bot.news_scraper.elements.Property;
import es.upm.bot.news_scraper.elements.ScrapingProperties;
import es.upm.bot.news_scraper.elements.Topic;
import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;
import es.upm.bot.news_scraper.exceptions.FirstParagraphNotFoundException;
import es.upm.bot.news_scraper.exceptions.ImageNotFoundException;
import es.upm.bot.news_scraper.exceptions.ProviderNotFoundException;

@Service
public class TechnicalScraper {

	private String defaultWebPage;
	private int NEWS_LIMIT_COMPLETA = 50;
	private int NEWS_LIMIT_PRIV = 5;

	private Map<String, ScrapingProperties> msp;
	private Map<String, String> userP;
	private Map<String, List<Article>> userNews;

	public TechnicalScraper(String defaultWebPage, ScrapingProperties sp) {
		System.err.println("LLAMADA CONSTRUCTOR TECHNICALSCRAPER CON PARAMETROS");
		this.defaultWebPage = defaultWebPage;
		this.msp = new HashMap<>();
		this.userP = new HashMap<>();
		this.userNews = new HashMap<>();
		msp.put(defaultWebPage, sp);
	}

	public TechnicalScraper() {
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

	public void changeProvider(String userID, String provider) throws ProviderNotFoundException {

		ScrapingProperties sp = msp.get(provider);

		if(sp == null)
			throw new ProviderNotFoundException();

		userP.put(userID, provider);
	}


	public String getArticles(String userID) throws ArticlesNotFoundException, ImageNotFoundException, FirstParagraphNotFoundException {


		String webPage = userP.get(userID);
		Document doc;

		if(webPage != null) {
			doc = generateDoc(webPage, webPage);
		}
		else {
			doc = generateDoc(defaultWebPage, defaultWebPage);
			webPage = defaultWebPage;
		}
		
		ScrapingProperties properties = msp.get(webPage);

		
		String articleType = msp.get(webPage).getArticle().getType();
		System.out.println("articleType " + articleType);
		String firstParagraphType= msp.get(webPage).getFirstParagraph().getType();


		ArrayList<Article> articleList = new ArrayList<>();
		Elements articles = null;
		switch(articleType) {
		case "Tag":{
			articles = doc.getElementsByTag(properties.getArticle().getValue());
			break;
		}

		case "Class":{
			articles = doc.getElementsByClass(properties.getArticle().getValue());
			break;
		}

		case "Attribute":{
			articles = doc.getElementsByAttributeValueContaining(properties.getArticle().getAttributeName()
					,properties.getArticle().getValue());
			break;
		}

		default:{
			articles = doc.getElementsByTag("article");
		}
		}
		if(articles.size() == 0)
			throw new ArticlesNotFoundException();
		int i = 0;
		for(Element e : articles) {
			if(i++ >= NEWS_LIMIT_COMPLETA)
				break;
			Article a;
			try {
				a = getArticleFromElement(e, webPage, firstParagraphType, doc);
			} catch (ImageNotFoundException | FirstParagraphNotFoundException e1) {
				i--;
				continue;
			}
			articleList.add(a);
		}	
		
		userNews.put(userID, articleList.subList(NEWS_LIMIT_PRIV, articleList.size()));

		return articlesToJson(articleList.subList(0, NEWS_LIMIT_PRIV));
	}
	
	public String getNextArticles(String userID) {
		List<Article> articles = userNews.get(userID);
		String nextArticles = articlesToJson(articles.subList(0, NEWS_LIMIT_PRIV));
		userNews.put(userID, articles.subList(NEWS_LIMIT_PRIV, articles.size()));
		return nextArticles;
	}

	private String getFirstParagraph(String articleLink, String webPage, String firstParagraphType, Document doc) throws FirstParagraphNotFoundException {	
		Elements parrafos = null;
		ScrapingProperties properties = msp.get(webPage);
		switch(firstParagraphType) {
		case "Tag":{
			parrafos = doc.getElementsByTag(properties.getFirstParagraph().getValue())
					.first().getElementsByTag("p");
			break;
		}

		case "Class":{
			parrafos = generateDoc(webPage, articleLink).getElementsByClass(properties.getFirstParagraph().getValue());
			break;
		}

		case "Attribute":{
			parrafos = doc.getElementsByAttributeValueContaining(properties.getFirstParagraph().getAttributeName()
					,properties.getFirstParagraph().getValue()).first().getElementsByTag("p");
			break;
		}

		default:{
			parrafos = generateDoc(webPage, articleLink).select("article p");

		}
		}

		if(parrafos.size() == 0)
			throw new FirstParagraphNotFoundException();
		return parrafos.first().text();
	}

	public String getTopics(String userID) {
		ArrayList<Topic> topicList = new ArrayList<>();

		String webPage = userP.get(userID);
		Document doc;

		if(webPage != null) {
			doc = generateDoc(webPage, webPage);
		}
		else {
			doc = generateDoc(defaultWebPage, defaultWebPage);
			webPage = defaultWebPage;
		}
		ScrapingProperties properties = msp.get(webPage);
		String topicType= msp.get(webPage).getTopic().getType();


		Elements topics = null;

		switch(topicType) {
		case "Tag":{
			topics = doc.getElementsByTag(properties.getTopic().getValue());
			break;
		}

		case "Class":{
			topics = doc.getElementsByClass(properties.getTopic().getValue());
			break;
		}

		case "Attribute":{
			topics = doc.getElementsByAttributeValueContaining(properties.getTopic().getAttributeName()
					,properties.getTopic().getValue());
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

	public String getArticlesFromTopic(String userID, String link) throws ArticlesNotFoundException{

		String webPage = userP.get(userID);
		Document doc;

		if(webPage != null) {
			doc = generateDoc(webPage, webPage);
		}
		else {
			doc = generateDoc(defaultWebPage, defaultWebPage);
			webPage = defaultWebPage;
		}

		String firstParagraphType= msp.get(webPage).getFirstParagraph().getType();

		ArrayList<Article> articleList = new ArrayList<>();

		Elements articles = generateDoc(webPage, link).getElementsByTag("article");
		if(articles.size() == 0)
			throw new ArticlesNotFoundException();
		int i = 0;
		for(Element e : articles) {
			if(i++ >= NEWS_LIMIT_COMPLETA)
				break;
			Article a;
			try {
				a = getArticleFromElement(e, webPage, firstParagraphType, doc);
			} catch (ImageNotFoundException | FirstParagraphNotFoundException e1) {
				i--;
				continue;
			}
			articleList.add(a);
		}
		
		userNews.put(userID, articleList.subList(NEWS_LIMIT_PRIV, articleList.size()));

		return articlesToJson(articleList.subList(0, NEWS_LIMIT_PRIV));

	}



	private Article getArticleFromElement(Element e, String webPage, String firstParagraphType, Document doc) throws ImageNotFoundException, FirstParagraphNotFoundException {
		String title = e.getElementsByTag("header").text();

		Element article = e.getElementsByAttribute("href").first();
		if(article == null)
			return null;
		String link = urlCheck(webPage, article.attr("href"));
		String clase = e.className();
		String content = getFirstParagraph(link, webPage, firstParagraphType, doc);
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

	private String articlesToJson(List<Article> list) {
		OutputStream os = new ByteArrayOutputStream(5000);
		JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
		JsonGenerator generator = factory.createGenerator(os);
		generator.writeStartArray();
		for(Article a : list) {		
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

	private String providersToJson(Map<String, ScrapingProperties> providers) {
		OutputStream os = new ByteArrayOutputStream(5000);
		JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
		JsonGenerator generator = factory.createGenerator(os);
		generator.writeStartArray();
		for(String webSite : providers.keySet()) {		
			generator
			.writeStartObject()
			.write("webSite", webSite)
			.write("name", msp.get(webSite).getName())
			.writeEnd();
		}
		generator.writeEnd();
		generator.close();

		return os.toString();

	}

	public Map<String, ScrapingProperties> providersFromJson(String body){	
		StringReader sr = new StringReader(body);
		JsonReader reader = Json.createReader(sr);
		JsonArray array = reader.readArray();

		Map<String, ScrapingProperties> scrapingPropertiesList = new HashMap<>();
		for(JsonValue jo : array) {
			JsonArray websitePropertiesPar = jo.asJsonArray();
			boolean webSiteExtracted = false;
			boolean webSiteNameExtracted = false;
			String webSite = "";
			String webSiteName = "";
			ArrayList<Property> propertyList = new ArrayList<>();
			for(JsonValue par : websitePropertiesPar) {
				if(!webSiteExtracted) {
					webSite = par.asJsonObject().getString("webSite");
					webSiteExtracted = true;
				}
				else if(!webSiteNameExtracted) {
					webSiteName = par.asJsonObject().getString("webSiteName");
					webSiteNameExtracted = true;
				}
				else {
					JsonArray arr = par.asJsonArray();

					for(JsonValue jo2 : arr) {
						JsonObject obj = jo2.asJsonObject();	
						Property property = new Property(obj.getString("use"), obj.getString("type"),
								obj.getString("attributeName"), obj.getString("value"));
						propertyList.add(property);
					}
					scrapingPropertiesList.put(webSite,new ScrapingProperties(webSiteName, webSite, propertyList));	
				}
			}
		}

		return scrapingPropertiesList;
	}


	public Map<String, ScrapingProperties> getMsp() {
		return msp;
	}

	public void addScrapingProperty(String webPage, ScrapingProperties sp) {
		msp.put(webPage, sp);
	}

	public void addProviders(Map<String, ScrapingProperties> providers) {
		msp.putAll(providers);
	}

	public void addProviders(String providers) {
		msp.putAll(providersFromJson(providers));
	}

	public String getProviders() {
		return providersToJson(msp);
	}

	public int getNEWS_LIMIT_COMPLETA() {
		return NEWS_LIMIT_COMPLETA;
	}

	public void setMax(int max) {
		this.NEWS_LIMIT_PRIV = max;
	}







}
