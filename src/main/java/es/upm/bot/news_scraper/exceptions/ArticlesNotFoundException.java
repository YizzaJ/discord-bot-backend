package es.upm.bot.news_scraper.exceptions;

public class ArticlesNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1696961473391734391L;

	public ArticlesNotFoundException() {
		super("No existen articulos en la url proporcionada.");
		// TODO Auto-generated constructor stub
	}
}
