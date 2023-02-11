package es.upm.bot.news_scraper.elements;

import java.util.ArrayList;

public class ScrapingProperties{

	private Property article;
	private Property firstParagraph;
	private Property topic;
	private ArrayList<Property> properties;

	public ScrapingProperties(Property article, Property topic) {
		this.article = article;
		this.topic = topic;
	}

	public ScrapingProperties(ArrayList<Property> properties) {
		this.properties = properties;
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








}
