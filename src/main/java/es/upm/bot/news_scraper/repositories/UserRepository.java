package es.upm.bot.news_scraper.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.upm.bot.news_scraper.entity.User;

public interface UserRepository extends JpaRepository<User, String>{
	
	Optional<User> findByUsernameAndServerID(String username, Long serverID);
	
	

}
