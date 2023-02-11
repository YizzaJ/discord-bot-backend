package es.upm.bot.news_scraper.scraper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import es.upm.bot.news_scraper.elements.Article;
import es.upm.bot.news_scraper.elements.Property;
import es.upm.bot.news_scraper.elements.ScrapingProperties;
import es.upm.bot.news_scraper.elements.Topic;
import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;

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

		ScrapingProperties sp = new ScrapingProperties(properties);
		
		
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

	public void changeProvider(String provider) {
		webPage = provider;
		doc = generateDoc(webPage);
	}


	public String getArticles() {
		ArrayList<Article> articleList = new ArrayList<>();
		Elements articles;

		switch(articleType) {
		case "Tag":{
			articles = doc.getElementsByTag(properties.getArticle().getValue());

			//			if(articles.size() == 0)
			//				throw new ArticlesNotFoundException();
			int i = 0;
			for(Element e : articles) {
				if(i++ >= NEWS_LIMIT)
					break;
				Article a = getArticleFromElement(e);

				if(a != null)
					articleList.add(a);
			}	
			break;
		}

		case "Class":{

			break;
		}

		case "Attribute":{

			break;
		}

		default:{
			articles = doc.getElementsByTag("article");
			//			if(articles.size() == 0)
			//				throw new ArticlesNotFoundException();
			int i = 0;
			for(Element e : articles) {
				if(i++ >= NEWS_LIMIT)
					break;
				Article a = getArticleFromElement(e);
				articleList.add(a);
			}
			return articlesToJson(articleList);
		}
		}

		return articlesToJson(articleList);
	}

	public String getTopics() {
		ArrayList<Topic> topicList = new ArrayList<>();

		Elements topics = null;

		switch(topicType) {
		case "Tag":{

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

	public String getArticlesFromTopic(String link) throws ArticlesNotFoundException {
		ArrayList<Article> articleList = new ArrayList<>();
		Elements articles = generateDoc(link).getElementsByTag("article");
		if(articles.size() == 0)
			throw new ArticlesNotFoundException();
		int i = 0;
		for(Element e : articles) {
			if(i++ >= NEWS_LIMIT)
				break;
			Article a = getArticleFromElement(e);
			articleList.add(a);
		}
		return articlesToJson(articleList);

	}



	private Article getArticleFromElement(Element e) {
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

	private String getFirstParagraph(String articleLink) {	
		Elements parrafos;

		switch(firstParagraphType) {
		case "Tag":{

			break;
		}

		case "Class":{
			String res = "";
			parrafos = generateDoc(articleLink).getElementsByClass(properties.getFirstParagraph().getValue())
					.first().getElementsByTag("p");
			return parrafos.first().text();
		}

		case "Attribute":{

			break;
		}

		default:{
			parrafos = generateDoc(articleLink).select("article p");
			return parrafos.first().text();
		}
		}
		return null;
	}
	private String searchImage(String articleLink) {
		Elements articles = generateDoc(articleLink).getElementsByTag("article").first().getElementsByTag("img");
		return articles.first().attr("src");
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

	public String getWebPage() {
		return webPage;
	}

	public Map<String, ScrapingProperties> getMsp() {
		return msp;
	}
	
	public void addScrapingProperty(String webPage, ScrapingProperties sp) {
		msp.put(webPage, sp);
	}



}
