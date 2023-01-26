package es.upm.bot.news_scraper.elements;

import java.io.IOException;
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
		this.base = e;
		this.title = e.getElementsByTag("header").text();
		this.link = e.getElementsByAttribute("href").first().attr("href");
		this.link = urlCheck(link);
		this.clase = e.className();
		this.content = readArticle();
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
	
	private String readArticle() {	
		String res = "";
		Elements parrafos = generateDoc(link).select("article p");
		for(Element e : parrafos) {
			res += e.text();
		}
		
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
