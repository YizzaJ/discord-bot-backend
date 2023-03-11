package es.upm.bot.news_scraper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;
import es.upm.bot.news_scraper.exceptions.FirstParagraphNotFoundException;
import es.upm.bot.news_scraper.exceptions.ImageNotFoundException;
import es.upm.bot.news_scraper.exceptions.ProviderNotFoundException;
import es.upm.bot.news_scraper.service.ProviderService;
import es.upm.bot.news_scraper.service.TechnicalScraper;

@RestController
@Scope("singleton")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8888"})
public class MessageController {

	@Autowired
	private TechnicalScraper ts;

	@Autowired
	ProviderService providerService;

	public MessageController() {

	}

	@PostMapping("create/")
	public void getNewGuild(@RequestBody String message) throws ArticlesNotFoundException, ImageNotFoundException, FirstParagraphNotFoundException {
		String newServ = message.replace("[", "");
		newServ = newServ.replace("]", "");
		ts.createServer(newServ);
	}


	@GetMapping("/{serverID}/{username}/news-list")
	public ResponseEntity<String> getAllList(@PathVariable Long serverID, @PathVariable String username) throws ArticlesNotFoundException, ImageNotFoundException, FirstParagraphNotFoundException {
		String articles = ts.getArticles(username, serverID);
		return new ResponseEntity<>(articles, HttpStatus.OK);
	}

	@GetMapping("/{serverID}/{username}/next-news")
	public ResponseEntity<String> getNextList(@PathVariable Long serverID, @PathVariable String username) throws ArticlesNotFoundException, ImageNotFoundException, FirstParagraphNotFoundException {
		String articles = ts.getNextArticles(username);
		return new ResponseEntity<>(articles, HttpStatus.OK);
	}

	@PostMapping("add-provider/{serverID}")
	public void addProvider(@PathVariable Long serverID, @RequestBody String message) {
		System.err.println(message);
		ts.providersFromJson(message, serverID);
	}
	
	@PostMapping("modify-provider/{serverID}")
	public void modifyProvider(@PathVariable Long serverID, @RequestBody String message) {
		System.err.println(message);
		ts.providersFromJson(message, serverID);
	}
	
	@PostMapping("remove-provider/{serverID}/{provider}")
	public void removeProvider(@PathVariable Long serverID, @PathVariable String provider) {
		ts.removeProvider(serverID, provider);
	}

	@PostMapping("{serverID}/{username}/change-provider")
	public void changeProvider(@RequestBody String message, @PathVariable Long serverID, @PathVariable String username) throws ProviderNotFoundException {
		String newProvider = message.replace("[", "");
		newProvider = newProvider.replace("]", "");
		ts.changeProvider(username, serverID, newProvider);
	}
	
	@GetMapping("{serverID}/providers")
	public ResponseEntity<String> getProvidersWs(@PathVariable Long serverID) throws ArticlesNotFoundException, ImageNotFoundException, FirstParagraphNotFoundException {
		System.err.println("Recibo get SERVERID "  + serverID);
		System.err.println("DOY PROVIDERS "  + ts.getProvidersWs(serverID));
		
		return new ResponseEntity<>(ts.getProvidersWs(serverID), HttpStatus.OK);
	}

	@GetMapping("{serverID}/{username}/provider-list")
	public ResponseEntity<String> getProviders(@PathVariable Long serverID, @PathVariable String username) throws ArticlesNotFoundException, ImageNotFoundException, FirstParagraphNotFoundException {
		String providers = ts.getProviders(serverID);
		System.out.println("PROVIDERS " + providers);
		return new ResponseEntity<>(providers, HttpStatus.OK);
	}

	@GetMapping("{serverID}/{username}/topic-list")
	public ResponseEntity<String> getTopicList(@PathVariable Long serverID, @PathVariable String username) throws ArticlesNotFoundException {
		String topics = ts.getTopics(username, serverID);
		System.out.println("TOPICS " + topics);
		return new ResponseEntity<>(topics, HttpStatus.OK);
	}

	@PostMapping("{serverID}/{username}/change-topic")
	public ResponseEntity<String> topic(@RequestBody String message, @PathVariable Long serverID, @PathVariable String username) throws ArticlesNotFoundException {
		String topic = message.replace("[", "");
		topic = topic.replace("]", "");

		String topics = ts.getArticlesFromTopic(username, serverID, topic);
		return new ResponseEntity<>(topics, HttpStatus.OK);
	}

	@PostMapping("max-private")
	public ResponseEntity<String> changeMax(@RequestBody String message) throws ArticlesNotFoundException {
		String newMax = message.replace("[", "");
		newMax = newMax.replace("]", "");
		ts.setMax(Integer.parseInt(newMax));
		return new ResponseEntity<>(null, HttpStatus.OK);
	}

}
