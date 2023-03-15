package es.upm.bot.news_scraper.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.upm.bot.news_scraper.entity.Server;
import es.upm.bot.news_scraper.entity.User;
import es.upm.bot.news_scraper.entity.UserId;

public interface UserRepository extends JpaRepository<User, UserId>{
	
	Optional<User> findByUserIdUsernameAndUserIdServerID(String username, Long serverID);
	
	Optional<List<User>> findByUserIdUsername(String username);

}
