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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.upm.bot.news_scraper.elements.Property;
import es.upm.bot.news_scraper.elements.ScrapingProperties;
import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;
import es.upm.bot.news_scraper.exceptions.FirstParagraphNotFoundException;
import es.upm.bot.news_scraper.exceptions.ImageNotFoundException;
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

		ScrapingProperties sp = new ScrapingProperties("El mundo", webPage, properties);

		this.ts = new TechnicalScraper(webPage, sp);
		
		Map<String, ScrapingProperties> scrapingPropertiesList = new HashMap<>();
		
		webPage =  ("https://elpais.com/");
		properties = new ArrayList<>();
		properties.add(new Property("Article","Tag","","article"));
		properties.add(new Property("FirstParagraph","","",""));
		properties.add(new Property("Topic","","",""));
		sp = new ScrapingProperties("El pais", webPage, properties);
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

	@GetMapping("{userID}/news")
	public ResponseEntity<String> getAll(@PathVariable String userID) throws ArticlesNotFoundException, ImageNotFoundException, FirstParagraphNotFoundException {
		String articles = ts.getArticles(userID);
		return new ResponseEntity<>(articles, HttpStatus.OK);
	}

	@GetMapping("/{userID}/newslist")
	public ResponseEntity<String> getAllList(@PathVariable String userID) throws ArticlesNotFoundException, ImageNotFoundException, FirstParagraphNotFoundException {
		String articles = ts.getArticles(userID);
		return new ResponseEntity<>(articles, HttpStatus.OK);
	}

	@PostMapping("add")
	public void addProvider(@RequestBody String message) {
		System.err.println(message);
		ts.addProviders(message);
	}

	@PostMapping("{userID}/change")
	public void changeProvider(@RequestBody String message, @PathVariable String userID) throws ProviderNotFoundException {
		String newProvider = message.replace("[", "");
		newProvider = newProvider.replace("]", "");
		ts.changeProvider(userID, newProvider);
	}
	
	@GetMapping("{userID}/providerlist")
	public ResponseEntity<String> getProviders(@PathVariable String userID) throws ArticlesNotFoundException, ImageNotFoundException, FirstParagraphNotFoundException {
		String providers = ts.getProviders();
		System.out.println("PROVIDERS " + providers);
		return new ResponseEntity<>(providers, HttpStatus.OK);
	}

	@GetMapping("{userID}/topiclist")
	public ResponseEntity<String> getTopicList(@PathVariable String userID) throws ArticlesNotFoundException {
		String topics = ts.getTopics(userID);
		System.out.println("TOPICS " + topics);
		return new ResponseEntity<>(topics, HttpStatus.OK);
	}

	@PostMapping("{userID}/topic")
	public ResponseEntity<String> topic(@RequestBody String message, @PathVariable String userID) throws ArticlesNotFoundException {
		String topic = message.replace("[", "");
		topic = topic.replace("]", "");

		String topics = ts.getArticlesFromTopic(userID, topic);
		System.err.println("TOPICSARTICLES " + topics);
		return new ResponseEntity<>(topics, HttpStatus.OK);
	}
	
	@PostMapping("max")
	public ResponseEntity<String> changeMax(@RequestBody String message) throws ArticlesNotFoundException {
		String newMax = message.replace("[", "");
		newMax = newMax.replace("]", "");

		ts.setNEWS_LIMIT(Integer.parseInt(message));
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
}
