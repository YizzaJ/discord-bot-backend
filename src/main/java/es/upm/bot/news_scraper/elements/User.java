package es.upm.bot.news_scraper.elements;

import java.util.LinkedList;
import java.util.Queue;

public class User {
	
	private String id;
	private String provider;
	private Queue<Article> userNews;
	
	public User(String id) {
		this.id = id;
		this.provider = "";
		this.userNews = new LinkedList<>();
	}
	
	public User(String id, String provider) {
		this.id = id;
		this.provider = provider;
		this.userNews = new LinkedList<>();
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public Queue<Article> getUserNews() {
		return userNews;
	}
	
	public void addUserNews(Article a) {
		userNews.add(a);
	}
	
	public Article pollUserNews() {
		return userNews.poll();
	}
	
	public void clearUserNews() {
		userNews.clear();
	}
	
	
	
	
	
	
	

}
