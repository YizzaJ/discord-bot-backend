package es.upm.bot.news_scraper.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.upm.bot.news_scraper.entity.Provider;
import es.upm.bot.news_scraper.entity.ProviderId;

public interface ProviderRepository extends JpaRepository<Provider, ProviderId>{
	
 
    Optional<Provider> findByProviderId_ServerIDAndNombre(Long serverID, String nombre);
    
    Optional<List<Provider>> findByProviderId_ServerID(Long serverID);

	void deleteByProviderId_ServerIDAndProviderId_Link(Long serverID, String link);
}
