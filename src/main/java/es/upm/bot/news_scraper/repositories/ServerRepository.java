package es.upm.bot.news_scraper.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import es.upm.bot.news_scraper.entity.Server;

public interface ServerRepository extends JpaRepository<Server, Long>{


}
