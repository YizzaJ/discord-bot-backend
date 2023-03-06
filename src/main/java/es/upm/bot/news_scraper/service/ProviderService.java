package es.upm.bot.news_scraper.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.upm.bot.news_scraper.entity.Provider;
import es.upm.bot.news_scraper.repositories.ProviderRepository;

@Service
public class ProviderService {
	
	@Autowired
	private ProviderRepository providerRepository;
	
	public Provider getP(String name, Long serverID) {
		Optional<Provider> provider = providerRepository.findByNombreAndServerID(name, serverID);
		return provider.get();
	}

}
