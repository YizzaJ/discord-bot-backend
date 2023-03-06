package es.upm.bot.news_scraper.service;

import org.springframework.beans.factory.annotation.Autowired;

import es.upm.bot.news_scraper.entity.Provider;
import es.upm.bot.news_scraper.repositories.ServerRepository;

public class ServerService {
	
	@Autowired
	private ServerRepository serverRepository;
	
	public Provider getProvider(Long serverID, Long name) {
		
		return null;
	}

}
