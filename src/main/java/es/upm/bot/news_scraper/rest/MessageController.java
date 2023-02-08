package es.upm.bot.news_scraper.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;
import es.upm.bot.news_scraper.scraper.Scraper;

@RestController
public class MessageController {
	
	Scraper scraper = new Scraper();
	
	@PostMapping("/news/send-message")
	public void receiveMessage(@RequestBody String message) {
		System.out.println("MANDO MENSAJE CON POST DESDE NEWS SCRAPER: " + message);
	}
	
    @GetMapping("news")
    public ResponseEntity<String> getAll() throws ArticlesNotFoundException {
    	String articles = scraper.getArticles();
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }
    
    @GetMapping("newslist")
    public ResponseEntity<String> getAllList() throws ArticlesNotFoundException {
    	String articles = scraper.getArticlesList();
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }
    
	@PostMapping("change")
	public void changeProvider(@RequestBody String message) {
		System.out.println("MANDO MENSAJE CON POST DESDE NEWS SCRAPER: " + message);
	}
}
