package es.upm.bot.news_scraper.exceptions;

public class ProviderNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1696961473391734391L;

	public ProviderNotFoundException() {
		super("El proveedor de noticias al que se intenta cambiar no existe.");
		// TODO Auto-generated constructor stub
	}
}
