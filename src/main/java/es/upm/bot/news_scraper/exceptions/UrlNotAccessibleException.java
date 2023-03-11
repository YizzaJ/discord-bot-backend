package es.upm.bot.news_scraper.exceptions;

public class UrlNotAccessibleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1451347119974003486L;

	public UrlNotAccessibleException() {
		super("El link proporcionado no es accesible.");
	}

	public UrlNotAccessibleException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}


}
