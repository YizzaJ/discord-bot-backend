package es.upm.bot.news_scraper.exceptions;

public class TopicNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1696961473391734391L;

	public TopicNotFoundException() {
		super("La categoria buscada no existe.");
		// TODO Auto-generated constructor stub
	}
}
