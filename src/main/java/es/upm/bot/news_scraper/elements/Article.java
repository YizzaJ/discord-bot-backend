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


public class Article {

	private String title;
	private String link;
	private String clase;
	private String content;
	private String image;
	private String favicon;

	
	public Article(String title, String link, String clase, String content, String image, String favicon) {
		this.title = title;
		this.link = link;
		this.clase = clase;
		this.content = content;
		this.image = image;
		this.favicon = favicon;
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

	public String getImage() {
		return image;
	}
	public String getFavicon() {
		return favicon;
	}


	@Override
	public String toString() {
		return "Article [title=" + title + ", link=" + link + ", clase=" + clase + ", content=" + content + ", image="
				+ image + ", favicon=" + favicon + "]";
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
