package es.upm.bot.news_scraper.rest;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.upm.bot.news_scraper.elements.Property;
import es.upm.bot.news_scraper.elements.ScrapingProperties;
import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;
import es.upm.bot.news_scraper.exceptions.ProviderNotFoundException;
import es.upm.bot.news_scraper.scraper.Scraper;
import es.upm.bot.news_scraper.scraper.TechnicalScraper;



@RestController
@Scope("singleton")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8888"})
public class MessageController {


	private TechnicalScraper ts;

	public MessageController() {
		System.err.println("LLAMADA CONSTRUCTOR MESSAGECONTROLLER CON PARAMETROS");
		String webPage =  ("https://www.elmundo.es/");
		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new Property("Article","Tag","","article"));
		properties.add(new Property("FirstParagraph","","",""));
		properties.add(new Property("Topic","Class","","ue-c-main-navigation__link ue-c-main-navigation__link-dropdown js-accessible-link"));

		ScrapingProperties sp = new ScrapingProperties(properties);

		this.ts = new TechnicalScraper(webPage, sp);
		
		Map<String, ScrapingProperties> scrapingPropertiesList = new HashMap<>();
		
		webPage =  ("https://elpais.com/");
		properties = new ArrayList<>();
		properties.add(new Property("Article","Tag","","article"));
		properties.add(new Property("FirstParagraph","","",""));
		properties.add(new Property("Topic","","",""));
		sp = new ScrapingProperties(properties);
		scrapingPropertiesList.put(webPage, sp);
		
//		webPage =  ("https://www.antena3.com/noticias/");
//		properties = new ArrayList<>();
//		properties.add(new Property("Article","Tag","","article"));
//		properties.add(new Property("FirstParagraph","","",""));
//		properties.add(new Property("Topic","","",""));
//		sp = new ScrapingProperties(properties);
//		scrapingPropertiesList.put(webPage, sp);
		
		ts.addProviders(scrapingPropertiesList);
		
	}

	private TechnicalScraper testMundo() {
		String webPage =  ("https://www.elmundo.es/");
		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new Property("Article","Tag","","article"));
		properties.add(new Property("FirstParagraph","","",""));
		properties.add(new Property("Topic","Class","","ue-c-main-navigation__link ue-c-main-navigation__link-dropdown js-accessible-link"));

		ScrapingProperties sp = new ScrapingProperties(properties);
		TechnicalScraper ts= new TechnicalScraper(webPage, sp);

		System.out.println(ts.getArticles());
		ts.getTopics();
		return ts;
	}

	private TechnicalScraper testPais() {
		String webPage =  ("https://elpais.com/");
		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new Property("Article","Tag","","article"));
		properties.add(new Property("FirstParagraph","Class","","a_c clearfix"));
		properties.add(new Property("Topic","Attribute","cmp-ltrk","portada_menu"));

		ScrapingProperties sp = new ScrapingProperties(properties);

		TechnicalScraper ts= new TechnicalScraper(webPage, sp);

		System.out.println(ts.getArticles());
		ts.getTopics();
		return ts;
	}


	@GetMapping("news")
	public ResponseEntity<String> getAll() throws ArticlesNotFoundException {
		String articles = ts.getArticles();
		return new ResponseEntity<>(articles, HttpStatus.OK);
	}

	@GetMapping("newslist")
	public ResponseEntity<String> getAllList() throws ArticlesNotFoundException {
		String articles = ts.getArticles();
		return new ResponseEntity<>(articles, HttpStatus.OK);
	}

	@PostMapping("add")
	public void addProvider(@RequestBody String message) {
		System.err.println(message);
		ts.addProviders(providersToJson(message));
	}

	@PostMapping("change")
	public void changeProvider(@RequestBody String message) throws ProviderNotFoundException {
		String newProvider = message.replace("[", "");
		newProvider = newProvider.replace("]", "");
		ts.changeProvider(newProvider);
	}

	@GetMapping("topiclist")
	public ResponseEntity<String> getTopicList() throws ArticlesNotFoundException {
		String topics = ts.getTopics();
		System.out.println("TOPICS " + topics);
		return new ResponseEntity<>(topics, HttpStatus.OK);
	}

	@PostMapping("topic")
	public ResponseEntity<String> topic(@RequestBody String message) throws ArticlesNotFoundException {
		String topic = message.replace("[", "");
		topic = topic.replace("]", "");

		String topics = ts.getArticlesFromTopic(topic);
		System.err.println("TOPICSARTICLES " + topics);
		return new ResponseEntity<>(topics, HttpStatus.OK);
	}
	

	public Map<String, ScrapingProperties> providersToJson(String body){	
		StringReader sr = new StringReader(body);
		JsonReader reader = Json.createReader(sr);
		JsonArray array = reader.readArray();

		Map<String, ScrapingProperties> scrapingPropertiesList = new HashMap<>();
		for(JsonValue jo : array) {
			JsonArray websitePropertiesPar = jo.asJsonArray();
			System.out.println(websitePropertiesPar);
			boolean webSiteExtracted = false;
			String webSite = "";
			ArrayList<Property> propertyList = new ArrayList<>();
			for(JsonValue par : websitePropertiesPar) {
				if(!webSiteExtracted) {
					webSite = par.asJsonObject().getString("webSite");
					webSiteExtracted = true;
				}
				else {
					JsonArray arr = par.asJsonArray();
					
					for(JsonValue jo2 : arr) {
						JsonObject obj = jo2.asJsonObject();	
						Property property = new Property(obj.getString("use"), obj.getString("type"),
								obj.getString("attributeName"), obj.getString("value"));
						propertyList.add(property);
					}
					scrapingPropertiesList.put(webSite,new ScrapingProperties(propertyList));	
				}
			}
		}

		return scrapingPropertiesList;
	}

}
