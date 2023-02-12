package es.upm.bot.news_scraper.json;

import java.io.StringReader;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import es.upm.bot.news_scraper.elements.Article;
import es.upm.bot.news_scraper.elements.Property;
import es.upm.bot.news_scraper.elements.ScrapingProperties;


public class ReaderJson {


	private JsonArray array;

	public ReaderJson(String body) {
		StringReader sr = new StringReader(body);
		JsonReader reader = Json.createReader(sr);
		this.array = reader.readArray();
	}

	public ArrayList<Article> getArticles(){
		ArrayList<Article> articleList = new ArrayList<>();
		for(JsonValue jo : array) {
			JsonObject obj = jo.asJsonObject();
			Article article = new Article(obj.getString("title"), obj.getString("link"), 
					obj.getString("clase"), obj.getString("content"), obj.getString("image"), obj.getString("favIcon"));	
			articleList.add(article);
		}
		return articleList;
	}

	public ArrayList<ScrapingProperties> getScraperProperties(){
		ArrayList<ScrapingProperties> scrapingPropertiesList = new ArrayList<>();

		for(JsonValue jo : array) {
			JsonArray arr = jo.asJsonArray();
			ArrayList<Property> propertyList = new ArrayList<>();
			for(JsonValue jo2 : arr) {
				JsonObject obj = jo2.asJsonObject();			
				Property property = new Property(obj.getString("use"), obj.getString("type"),
						obj.getString("attributeName"), obj.getString("value"));
				propertyList.add(property);
			}
			scrapingPropertiesList.add(new ScrapingProperties(propertyList));	
		}
		return scrapingPropertiesList;
	}

}
