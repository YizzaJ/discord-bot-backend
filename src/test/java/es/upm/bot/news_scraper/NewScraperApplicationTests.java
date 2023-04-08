package es.upm.bot.news_scraper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.upm.bot.news_scraper.entity.ProviderId;
import es.upm.bot.news_scraper.exceptions.ArticlesNotFoundException;
import es.upm.bot.news_scraper.exceptions.FirstParagraphNotFoundException;
import es.upm.bot.news_scraper.exceptions.TopicsNotFoundException;
import es.upm.bot.news_scraper.exceptions.UrlNotAccessibleException;
import es.upm.bot.news_scraper.repositories.ProviderRepository;
import es.upm.bot.news_scraper.service.TechnicalScraper;

@SpringBootTest
class NewScraperApplicationTests{

	@Autowired
	TechnicalScraper ts;


	private static Long serverID = 1059854853721030719L;
	private static String username = "tests";
	private static String newProviderOk;
	private static String newProviderOkId;
	private static String newProviderBadLink;
	private static String newProviderBadArticle;
	private static String newProviderBadParagraph;
	private static String newProviderBadTopic;

	@BeforeAll
	static void loadProviders() {

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
		newProviderOkId = "https://www.antena3.com/noticias/";

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
	
	@BeforeAll
	static void load() {

	}

	@Nested
	@DisplayName("Provider checks tests.")
	class ProviderChecksTests {

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

	@Nested
	@DisplayName("TechnicalScraper tests.")
	class TechnicalScraperTests {
		
		@Test
		void checkUrlGoodUrlTest() throws Exception {
			String goodBaseUrl = "https://www.elmundo.es/";
			String goodUrl = "https://www.elmundo.es/espana.html";
			String result = ts.urlCheck(goodBaseUrl, goodUrl);
			
			Assertions.assertTrue(result.equals(goodUrl));
		}
		
		@Test
		void checkUrlBadUrlTest() throws Exception {
			String goodBaseUrl = "https://www.elmundo.es/";
			String badUrl = "espana.html";
			String result = ts.urlCheck(goodBaseUrl, badUrl);
			
			Assertions.assertTrue(result.equals(goodBaseUrl + badUrl));
		}
		
		@Test
		void checkUrlBadBaseUrlTest() throws Exception {
			String goodBaseUrl = "veryBadUrl/";
			String badUrl = "espana.html";
			
			Assertions.assertThrows(UrlNotAccessibleException.class, () -> {
				ts.urlCheck(goodBaseUrl, badUrl);
			});	
		}
			
	}

	@Nested
	@DisplayName("Database management tests.")
	class DatabaseManagementTests {
		
		@Autowired
		ProviderRepository pr;
		
		@Test
		void addProviderGoodProviderTest() throws Exception {

			ts.addProvider(newProviderOk, serverID);
			
			Assertions.assertTrue(pr.existsById(new ProviderId(serverID, newProviderOkId)));
		}
		
		@Test
		void removeProviderGoodProviderTest() throws Exception {
			ts.removeProvider(serverID, "Antena 3");
			
			Assertions.assertFalse(pr.existsById(new ProviderId(serverID, newProviderOkId)));
		}
		
		@Test
		void addProviderBadProviderTest() throws Exception {

			Assertions.assertThrows(ArticlesNotFoundException.class, () -> {
				ts.addProvider(newProviderBadArticle, serverID);
			});	
			
			Assertions.assertFalse(pr.existsById(new ProviderId(serverID, newProviderOkId)));
		}
		
		
	}
}
