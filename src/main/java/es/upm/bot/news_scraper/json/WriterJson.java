package es.upm.bot.news_scraper.json;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import es.upm.bot.news_scraper.elements.Article;
import es.upm.bot.news_scraper.elements.Property;
import es.upm.bot.news_scraper.elements.ScrapingProperties;

public class WriterJson {


	private OutputStream os;
	private JsonGenerator generator;

	public WriterJson() {
		this.os = new ByteArrayOutputStream(5000);
		JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
		this.generator = factory.createGenerator(os);
	}

	public String articlesToJson(ArrayList<Article> articles) {
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

	public String scrapingPropertiesToJson(ArrayList<ScrapingProperties> scrapingPropertiesList) {
		generator.writeStartArray();
		for(ScrapingProperties sp : scrapingPropertiesList) {	
			generator.writeStartArray();
			for(Property p : sp.getProperties()) {
				generator
				.writeStartObject()
				.write("use", p.getUse())
				.write("type", p.getType())
				.write("attributeName", p.getAttributeName())
				.write("value", p.getValue())
				.writeEnd();
			}
			generator.writeEnd();
		}
		generator.writeEnd();
		generator.close();

		return os.toString();

	}

}
