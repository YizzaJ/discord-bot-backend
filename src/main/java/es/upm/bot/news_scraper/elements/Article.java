package es.upm.bot.news_scraper.elements;

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

import es.upm.bot.news_scraper.scraper.Scraper;

public class Article {

	private Element base;

	private String title;
	private String link;
	private String clase;
	private String content;
	private String image;
	private String favicon;
	private ArrayList<String> authors;
	private ArrayList<String> attributes;

	public Article(String title, String link, String clase, String image, String favicon) {
		this.title = title;
		this.link = link;
		this.clase = clase;
	}
	public Article(Element e) {
		this.base = e;
		this.title = e.getElementsByTag("header").text();
		this.link = urlCheck(e.getElementsByAttribute("href").first().attr("href"));
		this.clase = e.className();
		this.content = readArticle();
		this.image = urlCheck(getImage());
		this.favicon = urlCheck(getFavicon());
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
			return Scraper.webPage + url;	
		return url;
	}

	private String readArticle() {	
		String res = "";
		Elements parrafos = generateDoc(link).select("article p");
//		for(Element e : parrafos) {
//			res += e.text();
//		}

		return parrafos.first().text();
	}
	private String getImage() {
		Elements articles = generateDoc(link).getElementsByTag("article").first().getElementsByTag("img");
		return articles.first().attr("src");
	}
	
	private String getFavicon() {
		Elements articles = generateDoc(link).getElementsByAttributeValueContaining("href", "favicon.ico");
		return articles.first().attr("href");
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

	public String getAuthors() {
		String res = "";

		for(String a : authors)
			res += a + ", ";
		return res;
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

	public String toJson() {
		OutputStream os = new ByteArrayOutputStream(2000);
		JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
		JsonGenerator generator = factory.createGenerator(os);
		generator
		.writeStartObject()
		.write("title", title)
		.write("image", image)
		.write("content", content)
		.write("authors", "autores")
		.write("link", link)
		.write("favicon", favicon)
		.writeEnd();
		generator.close();
		return os.toString();
	}
	
}
