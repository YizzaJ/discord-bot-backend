package es.upm.bot.news_scraper.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
	
	
	@PostMapping("/news/send-message")
	public void receiveMessage(@RequestBody String message) {
		System.out.println("MANDO MENSAJE CON POST DESDE NEWS SCRAPER: " + message);
	}
	
    @GetMapping("news")
    public ResponseEntity<String> getAll() {
        return new ResponseEntity<>("RESPUESTA A GET DE NEWS SCRAPER", HttpStatus.OK);
    }
}
