package es.upm.bot.news_scraper.scraper;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.core.JsonFactory;

import es.upm.bot.news_scraper.elements.Article;
import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;
import es.upm.bot.news_scraper.exceptions.TopicNotFoundException;



public class Scraper {
	private static String A = "https://www.elmundo.es/";
	private static String B = "https://www.antena3.com/noticias/";
	private static String C = "https://elpais.com/";
	private static String D = "https://www.eluniversal.com/";


	public static String webPage = B;

	public int NEWS_LIMIT = 5;
	private Document doc;
	
	public Scraper() {
		doc = generateDoc(webPage);
	}
	
	public void changeProvider(String provider) {
		webPage = provider;
		doc = generateDoc(webPage);
	}


	public String getArticles() throws ArticlesNotFoundException {
		String res = "";
		Elements articles = doc.getElementsByTag("article");
		ArrayList<Article> articleList = new ArrayList<>();
		if(articles.size() == 0)
			throw new ArticlesNotFoundException();
		int i = 0;
		for(Element e : articles) {
			if(i++ >= NEWS_LIMIT)
				break;
			Article a = new Article(e);
			System.out.println(a.toJson());
			System.out.println();
			res += a.toJson() + "\n";
			articleList.add(a);
		}
		System.out.println(articlesToJson(articleList));
		return res;
	}

	
	public String getArticlesList() throws ArticlesNotFoundException {
		String res = "";
		Elements articles = doc.getElementsByTag("article");
		ArrayList<Article> articleList = new ArrayList<>();
		if(articles.size() == 0)
			throw new ArticlesNotFoundException();
		int i = 0;
		for(Element e : articles) {
			if(i++ >= NEWS_LIMIT)
				break;
			Article a = new Article(e);
			System.out.println("SCRAPER: getArticlesList()");
			System.out.println(a.toJson());
			System.out.println();
			res += a.toJson() + "\n";
			articleList.add(a);
		}
		System.out.println(articlesToJson(articleList));
		return articlesToJson(articleList);
	}

	public void getArticlesFromTopicEntry(String t) throws TopicNotFoundException, ArticlesNotFoundException {
		Elements headers = doc.select("body header");
		Elements topics = headers.first().getElementsContainingText(t);
		if(topics.size() == 0)
			throw new TopicNotFoundException();
		for(Element e : topics) {
			if(e.text().length() < 25) {
				Element topic = e.getElementsByAttribute("href").first();
				System.out.println(topic.className());
				getTopicsFromTopicClass(topic.className());
				//getArticles(generateDoc(topic.attr("href")));
				break;
			}
		}
	}

	public ArrayList<String[]> getTopicsFromTopicClass(String clase) {
		ArrayList<String[]> topicList = new ArrayList<>();

		Elements topics = doc.getElementsByClass(clase);
		System.out.println(topics.size());

		for(Element e : topics) {

			String topic = e.text();
			String href = e.attr("href");

			if(!href.equals("")) {
				topicList.add(new String[]{topic,href});
				System.out.println(e.text());
				System.out.println(e.attr("href"));
			}
		}



		return topicList;
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
			return Scraper.webPage + url;	
		return url;
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



//	public void main(String[] args) throws Exception {
//		getArticles(generateDoc(webPage));
//		//getArticlesFromTopicEntry("Deportes",generateDoc(webPage));
//	}


}
