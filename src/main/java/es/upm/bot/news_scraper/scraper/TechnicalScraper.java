package es.upm.bot.news_scraper.scraper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
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

	private String webPage;

	private Document doc;
	private int NEWS_LIMIT = 5;
	private ScrapingProperties properties;

	private String articleType;
	private String firstParagraphType;
	private String topicType;

	private Map<String,ScrapingProperties> msp;

	public TechnicalScraper(String webPage, ScrapingProperties sp) {
		System.err.println("LLAMADA CONSTRUCTOR TECHNICALSCRAPER CON PARAMETROS");
		this.webPage = webPage;
		this.doc = generateDoc(webPage);
		this.properties = sp;

		this.articleType = sp.getArticle().getType();
		this.firstParagraphType = sp.getFirstParagraph().getType();
		this.topicType = sp.getTopic().getType();
		this.msp = new HashMap<>();

		msp.put(webPage, sp);
	}

	public TechnicalScraper() {
		System.err.println("LLAMADA CONSTRUCTOR TECHNICALSCRAPER SIN PARAMETROS");
		this.webPage = "https://www.elpais.com/";
		this.doc = generateDoc(webPage);

		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new Property("Article","Tag","","article"));
		properties.add(new Property("FirstParagraph","","",""));
		properties.add(new Property("Topic","Class","","ue-c-main-navigation__link ue-c-main-navigation__link-dropdown js-accessible-link"));

		ScrapingProperties sp = new ScrapingProperties("El pais", webPage, properties);


		this.properties = sp;

		this.articleType = sp.getArticle().getType();
		this.firstParagraphType = sp.getFirstParagraph().getType();
		this.topicType = sp.getTopic().getType();
	}

	private Document generateDoc(String url) {
		String html = "";
		try{
			html = Jsoup.connect(urlCheck(url)).get().html();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Jsoup.parse(html);
	}

	private String urlCheck(String url) {	
		if(!url.startsWith("https://") && !url.startsWith("http://"))
			return webPage + url;	
		return url;
	}

	public void changeProvider(String provider) throws ProviderNotFoundException {

		System.out.println("Intento cambiar al proveedor " + provider);
		System.out.println("Existen los siguientes : \n" + msp.keySet().toString());
		ScrapingProperties sp = msp.get(provider);

		if(sp == null)
			throw new ProviderNotFoundException();


		webPage = provider;
		doc = generateDoc(webPage);

		this.properties = sp;

		this.articleType = sp.getArticle().getType();
		this.firstParagraphType = sp.getFirstParagraph().getType();
		this.topicType = sp.getTopic().getType();


	}


	public String getArticles() throws ArticlesNotFoundException, ImageNotFoundException, FirstParagraphNotFoundException {
		ArrayList<Article> articleList = new ArrayList<>();
		Elements articles = null;
		System.out.println("ARTICLE TYPE FROM GET ARTICLES " + articleType);
		switch(articleType) {
		case "Tag":{
			System.out.println("ARTICLE BY TAG " + properties.getArticle().getValue());
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
			if(i++ >= NEWS_LIMIT)
				break;
			Article a;
			try {
				a = getArticleFromElement(e);
			} catch (ImageNotFoundException | FirstParagraphNotFoundException e1) {
				i--;
				continue;
			}
			articleList.add(a);
		}	

		return articlesToJson(articleList);
	}

	private String getFirstParagraph(String articleLink) throws FirstParagraphNotFoundException {	
		Elements parrafos = null;
		System.out.println("PARAGRAPTH TYPE FROM getFirstParagraph " + firstParagraphType + " LINK " + articleLink);
		switch(firstParagraphType) {
		case "Tag":{
			parrafos = doc.getElementsByTag(properties.getFirstParagraph().getValue())
					.first().getElementsByTag("p");
			break;
		}

		case "Class":{
			parrafos = generateDoc(articleLink).getElementsByClass(properties.getFirstParagraph().getValue());
			break;
		}

		case "Attribute":{
			parrafos = doc.getElementsByAttributeValueContaining(properties.getFirstParagraph().getAttributeName()
					,properties.getFirstParagraph().getValue()).first().getElementsByTag("p");
			break;
		}

		default:{
			parrafos = generateDoc(articleLink).select("article p");

		}
		}
		
		if(parrafos.size() == 0)
			throw new FirstParagraphNotFoundException();
		return parrafos.first().text();
	}

	public String getTopics() {
		ArrayList<Topic> topicList = new ArrayList<>();

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

	public String getArticlesFromTopic(String link) throws ArticlesNotFoundException{
		ArrayList<Article> articleList = new ArrayList<>();
		Elements articles = generateDoc(link).getElementsByTag("article");
		if(articles.size() == 0)
			throw new ArticlesNotFoundException();
		int i = 0;
		for(Element e : articles) {
			if(i++ >= NEWS_LIMIT)
				break;
			Article a;
			try {
				a = getArticleFromElement(e);
			} catch (ImageNotFoundException | FirstParagraphNotFoundException e1) {
				i--;
				continue;
			}
			articleList.add(a);
		}
		return articlesToJson(articleList);

	}



	private Article getArticleFromElement(Element e) throws ImageNotFoundException, FirstParagraphNotFoundException {
		String title = e.getElementsByTag("header").text();

		Element article = e.getElementsByAttribute("href").first();
		if(article == null)
			return null;
		String link = urlCheck(article.attr("href"));
		String clase = e.className();
		String content = getFirstParagraph(link);
		String image = urlCheck(searchImage(link));
		String favicon = urlCheck(searchFavicon(link));
		return new Article(title, link, clase, content, image, favicon);
	}


	private String searchImage(String articleLink) throws ImageNotFoundException {
		String image = "";
		try {
		Elements articles = generateDoc(articleLink).getElementsByTag("article").first().getElementsByTag("img");
		image = articles.first().attr("src");
		}
		catch(NullPointerException e) {
			image = "";
			throw new ImageNotFoundException();		
		}
		
		return image;
	}

	private String searchFavicon(String articleLink) {
		return "https://www.google.com/s2/favicons?domain="+ webPage+"&sz=128";
	}

	private String articlesToJson(ArrayList<Article> articles) {
		OutputStream os = new ByteArrayOutputStream(5000);
		JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
		JsonGenerator generator = factory.createGenerator(os);
		generator.writeStartArray();
		for(Article a : articles) {		
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
			System.out.println(websitePropertiesPar);
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

	public String getWebPage() {
		return webPage;
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



}
