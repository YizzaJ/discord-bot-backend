package es.upm.bot.news_scraper.elements;

public class Topic {
	
	private String name;
	private String Link;
	
	public Topic(String name, String link) {
		this.name = name;
		Link = link;
	}
	
	public String getName() {
		return name;
	}
	public String getLink() {
		return Link;
	}
	
	

}
