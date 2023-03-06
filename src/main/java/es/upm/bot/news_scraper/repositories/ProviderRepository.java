package es.upm.bot.news_scraper.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.upm.bot.news_scraper.entity.Provider;

public interface ProviderRepository extends JpaRepository<Provider, String>{
	
 
    Optional<Provider> findByNombreAndServerID(String nombre, Long serverID);
    
    Optional<List<Provider>> findByServerID(Long serverID);
}
