package es.upm.bot.news_scraper.exceptions;

public class TopicsNotFoundException extends Exception {

	public TopicsNotFoundException() {
		super("No se pudieron encontrar los topics para este proveedor.");
	}

	public TopicsNotFoundException(String message) {
		super(message);
	}


}
