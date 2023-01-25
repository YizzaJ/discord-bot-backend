package es.upm.bot.news_scraper.elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.upm.bot.news_scraper.scraper.scraper;

public class Article {
	
	private Element base;
	
	private String title;
	private String link;
	private String clase;
	private String content;
	private ArrayList<String> authors;
	private ArrayList<String> attributes;
	
	public Article(String title, String link, String clase) {
		this.title = title;
		this.link = link;
		this.clase = clase;
	}
	public Article(Element e) {
		base = e;
		this.title = e.getElementsByTag("header").text();
//		this.link = e.getElementsByClass("ue-c-cover-content__link").attr("href");
		this.link = e.getElementsByAttribute("href").first().attr("href");
		this.link = urlCheck(link) ? link : (scraper.webPage + link);
		
		this.clase = e.className();
		this.content = readArticle();
	}
	
	private Document parse() {
		
	        String html = "";
			try{
				html = Jsoup.connect(link).get().html();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Document doc = Jsoup.parse(html);
			return doc;
	}
	private String readArticle() {
		
		String res = "";
		Elements parrafos = parse().select("article p");
		int index = 0;
		for(Element e : parrafos) {
			res += e.text();
		}
		
		return res;
		
	}
	
	private boolean urlCheck(String url) {
		boolean res = true;		
		if(!url.startsWith("https://") && !url.startsWith("http://"))
		res = false;	
		return res;
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	public ArrayList<String> getAttributes() {
		return attributes;
	}
	
	public void addAttributes(String a) {
		attributes.add(a);
	}
	
	public void removeAttributes(String a) {
		attributes.remove(a);
	}

	public void setAttributes(ArrayList<String> attributes) {
		this.attributes = attributes;
	}

	public ArrayList<String> getAuthors() {
		return authors;
	}
	
	public void addAuthors(String a) {
		authors.add(a);
	}
	
	public void removeAuthors(String a) {
		authors.remove(a);
	}

	public void setAuthors(ArrayList<String> authors) {
		this.authors = authors;
	}

	@Override
	public String toString() {
		return "Article [ \n"
				+ "title=" + title + ", \n link=" + link + ", \n clase=" + clase + ", \n content=" + content + ", \n authors="
				+ authors + ", \n attributes=" + attributes + "]";
	}
	
	
	
	
	

}
