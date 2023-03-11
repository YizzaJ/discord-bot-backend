package es.upm.bot.news_scraper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.upm.bot.news_scraper.exceptions.*;
import es.upm.bot.news_scraper.service.TechnicalScraper;

@SpringBootTest
class NewScraperApplicationTests{
	
	@Autowired
	TechnicalScraper ts;

	
	private static Long serverID;
	private static String newProviderOk;
	private static String newProviderBadLink;
	private static String newProviderBadArticle;
	private static String newProviderBadParagraph;
	private static String newProviderBadTopic;
	@BeforeAll
	static void load() {
		serverID = 1059854853721030719L;
		newProviderOk = "{\n"
				+ "  \"webSite\": \"https://www.antena3.com/noticias/\",\n"
				+ "  \"webSiteName\": \"Antena 3\",\n"
				+ "  \"usoArticulo\": \"Article\",\n"
				+ "  \"tipoArticulo\": \"Tag\",\n"
				+ "  \"attributeNameArticulo\": \"\",\n"
				+ "  \"valorArticulo\": \"article\",\n"
				+ "  \"usoParrafo\": \"FirstParagraph\",\n"
				+ "  \"tipoParrafo\": \"Class\",\n"
				+ "  \"attributeNameParrafo\": \"\",\n"
				+ "  \"valorParrafo\": \"article-main__description\",\n"
				+ "  \"usoTopic\": \"Topic\",\n"
				+ "  \"tipoTopic\": \"Class\",\n"
				+ "  \"attributeNameTopic\": \"\",\n"
				+ "  \"valorTopic\": \"menu-main__link menu-main__link--level1\"\n"
				+ "}";
		
		newProviderBadLink = "{\n"
				+ "  \"webSite\": \"https://www.estoNoEsUnLinkEsUnTestMalo.com/noticias/\",\n"
				+ "  \"webSiteName\": \"Antena 3\",\n"
				+ "  \"usoArticulo\": \"Article\",\n"
				+ "  \"tipoArticulo\": \"Tag\",\n"
				+ "  \"attributeNameArticulo\": \"\",\n"
				+ "  \"valorArticulo\": \"article\",\n"
				+ "  \"usoParrafo\": \"FirstParagraph\",\n"
				+ "  \"tipoParrafo\": \"Class\",\n"
				+ "  \"attributeNameParrafo\": \"\",\n"
				+ "  \"valorParrafo\": \"article-main__description\",\n"
				+ "  \"usoTopic\": \"Topic\",\n"
				+ "  \"tipoTopic\": \"Class\",\n"
				+ "  \"attributeNameTopic\": \"\",\n"
				+ "  \"valorTopic\": \"menu-main__link menu-main__link--level1\"\n"
				+ "}";
		
		newProviderBadArticle = "{\n"
				+ "  \"webSite\": \"https://www.antena3.com/noticias/\",\n"
				+ "  \"webSiteName\": \"Antena 3\",\n"
				+ "  \"usoArticulo\": \"Article\",\n"
				+ "  \"tipoArticulo\": \"Tag\",\n"
				+ "  \"attributeNameArticulo\": \"\",\n"
				+ "  \"valorArticulo\": \"estoNoEsUnArticulo\",\n"
				+ "  \"usoParrafo\": \"FirstParagraph\",\n"
				+ "  \"tipoParrafo\": \"Class\",\n"
				+ "  \"attributeNameParrafo\": \"\",\n"
				+ "  \"valorParrafo\": \"article-main__description\",\n"
				+ "  \"usoTopic\": \"Topic\",\n"
				+ "  \"tipoTopic\": \"Class\",\n"
				+ "  \"attributeNameTopic\": \"\",\n"
				+ "  \"valorTopic\": \"menu-main__link menu-main__link--level1\"\n"
				+ "}";
		
		newProviderBadParagraph = "{\n"
				+ "  \"webSite\": \"https://www.antena3.com/noticias/\",\n"
				+ "  \"webSiteName\": \"Antena 3\",\n"
				+ "  \"usoArticulo\": \"Article\",\n"
				+ "  \"tipoArticulo\": \"Tag\",\n"
				+ "  \"attributeNameArticulo\": \"\",\n"
				+ "  \"valorArticulo\": \"article\",\n"
				+ "  \"usoParrafo\": \"FirstParagraph\",\n"
				+ "  \"tipoParrafo\": \"Class\",\n"
				+ "  \"attributeNameParrafo\": \"\",\n"
				+ "  \"valorParrafo\": \"estoNoEsUnTipoParaElPrimerParrafo\",\n"
				+ "  \"usoTopic\": \"Topic\",\n"
				+ "  \"tipoTopic\": \"Class\",\n"
				+ "  \"attributeNameTopic\": \"\",\n"
				+ "  \"valorTopic\": \"menu-main__link menu-main__link--level1\"\n"
				+ "}";
				
		newProviderBadTopic = "{\n"
				+ "  \"webSite\": \"https://www.antena3.com/noticias/\",\n"
				+ "  \"webSiteName\": \"Antena 3\",\n"
				+ "  \"usoArticulo\": \"Article\",\n"
				+ "  \"tipoArticulo\": \"Tag\",\n"
				+ "  \"attributeNameArticulo\": \"\",\n"
				+ "  \"valorArticulo\": \"article\",\n"
				+ "  \"usoParrafo\": \"FirstParagraph\",\n"
				+ "  \"tipoParrafo\": \"Class\",\n"
				+ "  \"attributeNameParrafo\": \"\",\n"
				+ "  \"valorParrafo\": \"article-main__description\",\n"
				+ "  \"usoTopic\": \"Topic\",\n"
				+ "  \"tipoTopic\": \"Class\",\n"
				+ "  \"attributeNameTopic\": \"\",\n"
				+ "  \"valorTopic\": \"estoNoEsUnTipoParaElTopiccc\"\n"
				+ "}";
	}
	
	@Test
	void checkProviderTestOK() throws Exception {
		ts.checkNewProvider(newProviderOk, serverID);
	}
	
	@Test
	void checkProviderTestBadLink(){
		Assertions.assertThrows(UrlNotAccessibleException.class, () -> {
			ts.checkNewProvider(newProviderBadLink, serverID);
        });
	}
	
	@Test
	void checkProviderTestBadArticle(){
		Assertions.assertThrows(ArticlesNotFoundException.class, () -> {
			ts.checkNewProvider(newProviderBadArticle, serverID);
        });
	}
	
	@Test
	void checkProviderTestBadParagraph(){
		Assertions.assertThrows(FirstParagraphNotFoundException.class, () -> {
			ts.checkNewProvider(newProviderBadParagraph, serverID);
        });
	}
	
	@Test
	void checkProviderTestBadTopic(){
		Assertions.assertThrows(TopicsNotFoundException.class, () -> {
			ts.checkNewProvider(newProviderBadTopic, serverID);
        });
	}
	
	


}
