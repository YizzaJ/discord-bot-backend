package es.upm.bot.news_scraper.rest;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.upm.bot.news_scraper.elements.Property;
import es.upm.bot.news_scraper.elements.ScrapingProperties;
import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;
import es.upm.bot.news_scraper.scraper.Scraper;
import es.upm.bot.news_scraper.scraper.TechnicalScraper;



@RestController
@Scope("singleton")
public class MessageController {
	
	
	private TechnicalScraper ts;
	
	public MessageController() {
		String webPage =  ("https://www.elmundo.es/");
		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new Property("Article","Tag","article"));
		properties.add(new Property("FirstParagraph","",""));
		properties.add(new Property("Topic","Class","ue-c-main-navigation__link ue-c-main-navigation__link-dropdown js-accessible-link"));

		ScrapingProperties sp = new ScrapingProperties(properties);

		this.ts = new TechnicalScraper(webPage, sp);
	}
	
	private TechnicalScraper testMundo() {
		String webPage =  ("https://www.elmundo.es/");
		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new Property("Article","Tag","article"));
		properties.add(new Property("FirstParagraph","",""));
		properties.add(new Property("Topic","Class","ue-c-main-navigation__link ue-c-main-navigation__link-dropdown js-accessible-link"));

		ScrapingProperties sp = new ScrapingProperties(properties);
		TechnicalScraper ts= new TechnicalScraper(webPage, sp);
		
		System.out.println(ts.getArticles());
		ts.getTopics();
		return ts;
	}
	
	private TechnicalScraper testPais() {
		String webPage =  ("https://elpais.com/");
		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new Property("Article","Tag","article"));
		properties.add(new Property("FirstParagraph","Class","a_c clearfix"));
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
    
	@PostMapping("change")
	public void changeProvider(@RequestBody String message) {
		String newProvider = message.replace("[", "");
		newProvider = newProvider.replace("]", "");
		ts.changeProvider(newProvider);
		Scraper.webPage = newProvider;
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
	
	
	
	
	
	
//	Scraper scraper = new Scraper();
//	
//    @GetMapping("news")
//    public ResponseEntity<String> getAll() throws ArticlesNotFoundException {
//    	String articles = scraper.getArticleOlds();
//        return new ResponseEntity<>(articles, HttpStatus.OK);
//    }
//    
//    @GetMapping("newslist")
//    public ResponseEntity<String> getAllList() throws ArticlesNotFoundException {
//    	String articles = scraper.getArticleOldsList();
//        return new ResponseEntity<>(articles, HttpStatus.OK);
//    }
//    
//	@PostMapping("change")
//	public void changeProvider(@RequestBody String message) {
//		System.out.println("MANDO MENSAJE CON POST DESDE NEWS SCRAPER: " + message);
//		String newProvider = message.replace("[", "");
//		newProvider = newProvider.replace("]", "");
//		System.out.println("MANDO MENSAJE CON POST DESDE NEWS SCRAPER: " + newProvider);
//		scraper.changeProvider(newProvider);
//		Scraper.webPage = newProvider;
//	}
}
