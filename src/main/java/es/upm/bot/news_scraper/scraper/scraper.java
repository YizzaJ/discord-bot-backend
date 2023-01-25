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
		
//		int index = 0;
//		for(Element e : articles) {
//			if(!e.className().contains("content--is-opinion-in-block")) {
//			System.out.println(index + " " + "Clase"  + " " + e.className());
//			System.out.println(index++ + " " + e.text());
//			System.out.println();
//			
//			}
//		}
	}
	
	public static void getParrafos(Document doc) {
		Elements parrafos = doc.getElementsByTag("p");
		int index = 0;
		for(Element e : parrafos) {
			System.out.println(index++ + " " + e.text());
			System.out.println();
		}
	}
	
	
	public static void main(String[] args) {
		  

	        String html = "";
			try{
				html = Jsoup.connect(webPage).get().html();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Document doc = Jsoup.parse(html);
			getArticles(doc);
			

	}
	
	
}
