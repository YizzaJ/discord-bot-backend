package es.upm.bot.news_scraper.elements;

import java.util.ArrayList;

public class ScrapingProperties{

	private String name;
	private String webSite;
	private Property article;
	private Property firstParagraph;
	private Property topic;
	private ArrayList<Property> properties;

	public ScrapingProperties(String name, String webSite, ArrayList<Property> properties) {
		this.properties = properties;
		this.name = name;
		this.webSite = webSite;
		for(Property p : properties) {
			switch(p.getUse()) {
			case "Article": 
				this.article = p; 
				break;

			case "FirstParagraph": 
				this.firstParagraph = p; 
				break;

			case "Topic": 
				this.topic = p;
				break;
			}

		}
	}

	public Property getArticle() {
		return article;
	}

	public Property getFirstParagraph() {
		return firstParagraph;
	}

	public Property getTopic() {
		return topic;
	}

	public ArrayList<Property> getProperties() {
		return properties;
	}

	public String getName() {
		return name;
	}

	public String getWebSite() {
		return webSite;
	}








}
