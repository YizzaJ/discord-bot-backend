package es.upm.bot.news_scraper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;
import es.upm.bot.news_scraper.exceptions.FirstParagraphNotFoundException;
import es.upm.bot.news_scraper.exceptions.ProviderNotFoundException;
import es.upm.bot.news_scraper.exceptions.TopicsNotFoundException;
import es.upm.bot.news_scraper.exceptions.UrlNotAccessibleException;
import es.upm.bot.news_scraper.service.TechnicalScraper;

@RestController
@Scope("singleton")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8888", "http://192.168.1.82:4200"})
public class MessageController {

	@Autowired
	private TechnicalScraper ts;


	public MessageController() {

	}

	@PostMapping("guilds/{serverID}/")
	public void getNewGuild(@PathVariable Long serverID,@RequestBody String message){
		String newServ = message.replace("[", "");
		newServ = newServ.replace("]", "");
		ts.createServer(serverID, newServ);
	}


	@GetMapping("news/{serverID}/{username}/")
	public ResponseEntity<String> getAllList(@PathVariable Long serverID, @PathVariable String username){
		String articles = "";
		try {
			articles = ts.getArticles(username, serverID);
		} catch (ArticlesNotFoundException | UrlNotAccessibleException e) {
			
			e.printStackTrace();
		}
		return new ResponseEntity<>(articles, HttpStatus.OK);
	}

	@GetMapping("news/{serverID}/{username}/next/")
	public ResponseEntity<String> getNextList(@PathVariable Long serverID, @PathVariable String username){
		String articles = ts.getNextArticles(username);
		return new ResponseEntity<>(articles, HttpStatus.OK);
	}

	@PostMapping("providers/{serverID}/")
	public ResponseEntity<String> addProvider(@PathVariable Long serverID, @RequestBody String message) {
		try {
			ts.addProvider(message, serverID);
		} catch (UrlNotAccessibleException | ArticlesNotFoundException | FirstParagraphNotFoundException
				| TopicsNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	@PutMapping("providers/{serverID}/")
	public ResponseEntity<String> modifyProvider(@PathVariable Long serverID, @RequestBody String message) {
		try {
			ts.addProvider(message, serverID);
		} catch (UrlNotAccessibleException | ArticlesNotFoundException | FirstParagraphNotFoundException
				| TopicsNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	@PostMapping("providers/remove/{serverID}/")
	public void removeProvider(@PathVariable Long serverID, @RequestBody String message) {
		String provider = message.replace("[", "");
		provider = provider.replace("]", "");
		ts.removeProvider(serverID, provider);
	}
	
	@GetMapping("providers/{serverID}/complete/")
	public ResponseEntity<String> getProvidersWs(@PathVariable Long serverID){	
		return new ResponseEntity<>(ts.getProvidersWs(serverID), HttpStatus.OK);
	}
	
	@GetMapping("servers/{username}")
	public ResponseEntity<String> getServersWs(@PathVariable String username){
		return new ResponseEntity<>(ts.getServersWs(username), HttpStatus.OK);
	}

	@PostMapping("providers/{serverID}/{username}/")
	public void changeProvider(@RequestBody String message, @PathVariable Long serverID, @PathVariable String username){
		String newProvider = message.replace("[", "");
		newProvider = newProvider.replace("]", "");
		try {
			ts.changeProvider(username, serverID, newProvider);
		} catch (ProviderNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	

	@GetMapping("providers/{serverID}/")
	public ResponseEntity<String> getProviders(@PathVariable Long serverID){
		String providers = ts.getProviders(serverID);
		return new ResponseEntity<>(providers, HttpStatus.OK);
	}

	@GetMapping("providers/{serverID}/{username}/topics/")
	public ResponseEntity<String> getTopicList(@PathVariable Long serverID, @PathVariable String username){
		String topics = "";
		try {
			topics = ts.getTopics(username, serverID);
		} catch (UrlNotAccessibleException e) {
			
			e.printStackTrace();
		}
		return new ResponseEntity<>(topics, HttpStatus.OK);
	}

	@PostMapping("providers/{serverID}/{username}/topics/")
	public ResponseEntity<String> topic(@RequestBody String message, @PathVariable Long serverID, @PathVariable String username){
		String topic = message.replace("[", "");
		topic = topic.replace("]", "");

		String topics = "";
		try {
			topics = ts.getArticlesFromTopic(username, serverID, topic);
		} catch (ArticlesNotFoundException | UrlNotAccessibleException e) {
			
			e.printStackTrace();
		}
		return new ResponseEntity<>(topics, HttpStatus.OK);
	}

	@PostMapping("max-private/")
	public ResponseEntity<String> changeMax(@RequestBody String message) throws ArticlesNotFoundException {
		String newMax = message.replace("[", "");
		newMax = newMax.replace("]", "");
		ts.setMax(Integer.parseInt(newMax));
		return new ResponseEntity<>(null, HttpStatus.OK);
	}

}
