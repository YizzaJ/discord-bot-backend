package es.upm.bot.news_scraper.json;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

public class WriterJson {


	private OutputStream os;
	private JsonGenerator generator;

	public WriterJson() {
		this.os = new ByteArrayOutputStream(5000);
		JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
		this.generator = factory.createGenerator(os);
	}

	public String articlesToJson(ArrayList<String[]> articles) {
		generator.writeStartArray();
		for(String[] a : articles) {		
			generator
			.writeStartObject()
			.write("title", a[0])
			.write("image", a[1])
			.write("content", a[2])
			.write("link", a[3])
			.write("favicon", a[4])
			.writeEnd();
		}
		generator.writeEnd();
		generator.close();

		return os.toString();
	}

	public String scrapingPropertiesToJson(ArrayList<ArrayList<String[]>> scrapingPropertiesList) {
		generator.writeStartArray();
		for(ArrayList<String[]> sp : scrapingPropertiesList) {
			generator.writeStartArray();
			generator
			.writeStartObject()
			.write("webSite", sp.remove(0)[0])
			.writeEnd();
			for(String[] p : sp) {	
				generator.writeStartArray();
				generator
				.writeStartObject()
				.write("webSite", p[0])
				.write("use", p[1])
				.write("type", p[2])
				.write("attributeName", p[3])
				.write("value", p[4])
				.writeEnd();
				generator.writeEnd();
			}
			generator.writeEnd();
		}
		generator.writeEnd();
		generator.close();

		return os.toString();

	}

}
