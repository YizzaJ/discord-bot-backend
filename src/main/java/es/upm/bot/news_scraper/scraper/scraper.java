package es.upm.bot.news_scraper.scraper;



import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.upm.bot.news_scraper.elements.Article;
import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;
import es.upm.bot.news_scraper.exceptions.TopicNotFoundException;



public class scraper {
	private static String A = "https://www.elmundo.es/";
	private static String B = "https://www.antena3.com/noticias/";
	private static String C = "https://elpais.com/";
	private static String D = "https://www.eluniversal.com/";


	public static String webPage = A;

	public static int NEWS_LIMIT = 5;


	public static void getArticles(Document doc) throws ArticlesNotFoundException {

		Elements articles = doc.getElementsByTag("article");
		if(articles.size() == 0)
			throw new ArticlesNotFoundException();
		int i = 0;
		for(Element e : articles) {
			if(i++ >= NEWS_LIMIT)
				break;
			Article a = new Article(e);
			System.out.println(a.toString());
			System.out.println();
		}
	}


	public static void getArticlesFromTopicEntry(String t, Document doc) throws TopicNotFoundException, ArticlesNotFoundException {
		Elements headers = doc.select("body header");
		Elements topics = headers.first().getElementsContainingText(t);
		if(topics.size() == 0)
			throw new TopicNotFoundException();
		for(Element e : topics) {
			if(e.text().length() < 25) {
				Element topic = e.getElementsByAttribute("href").first();
				System.out.println(topic.className());
				getTopicsFromTopicClass(topic.className(), doc);
				//getArticles(generateDoc(topic.attr("href")));
				break;
			}
		}
	}

	public static ArrayList<String[]> getTopicsFromTopicClass(String clase, Document doc) {
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

	private static Document generateDoc(String url) {
		String html = "";
		try{
			html = Jsoup.connect(urlCheck(url)).get().html();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Jsoup.parse(html);
	}

	private static String urlCheck(String url) {	
		if(!url.startsWith("https://") && !url.startsWith("http://"))
			return scraper.webPage + url;	
		return url;
	}


	public static void main(String[] args) throws Exception {
		getArticles(generateDoc(webPage));
		//getArticlesFromTopicEntry("Deportes",generateDoc(webPage));
	}


}
