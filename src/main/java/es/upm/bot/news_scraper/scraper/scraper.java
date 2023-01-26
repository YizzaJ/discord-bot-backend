package es.upm.bot.news_scraper.scraper;



import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.upm.bot.news_scraper.elements.Article;



public class scraper {
	private static String A = "https://www.elmundo.es/";
	private static String B = "https://www.antena3.com/noticias/";
	private static String C = "https://elpais.com/";
	private static String D = "https://www.eluniversal.com/";


	public static String webPage = D;

	public static int NEWS_LIMIT = 5;

	
	public static void getArticles(Document doc) {

		Elements articles = doc.getElementsByTag("article");

		int i = 0;
		for(Element e : articles) {
			if(i++ >= NEWS_LIMIT)
				break;
			Article a = new Article(e);
			System.out.println(a.toString());
			System.out.println();
		}
	}


	public static void getArticlesTopic(String topic, Document doc) {
		Elements headers = doc.select("body header");
		for(Element e : headers.first().getElementsContainingText(topic)) {
			if(e.text().length() < 25) {
				getArticles(generateDoc(e.getElementsByAttribute("href").first().attr("href")));
				break;
			}
		}
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


	public static void main(String[] args) {
		getArticles(generateDoc(webPage));
		//getArticlesTopic("Deporte",generateDoc(webPage));
	}


}
