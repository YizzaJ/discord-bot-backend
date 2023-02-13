package es.upm.bot.news_scraper.exceptions;

public class FirstParagraphNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1696961473391734391L;

	public FirstParagraphNotFoundException() {
		super("No existen parrafos en la url proporcionada.");
		// TODO Auto-generated constructor stub
	}
}
