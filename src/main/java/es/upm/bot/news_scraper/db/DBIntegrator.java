package es.upm.bot.news_scraper.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import es.upm.bot.news_scraper.elements.Property;
import es.upm.bot.news_scraper.elements.ScrapingProperties;

public class DBIntegrator {
	private static Connection conn = null;
	public static Connection connect() {

		Properties connectionProps = new Properties();
		connectionProps.put("user", "admin");
		connectionProps.put("password", "1901Jjpc*");

		try {
			conn = DriverManager.getConnection(
					"jdbc:" + "mysql" + "://" +
							"localhost" +
							":" + 3306 + "/newsbotdb",
							connectionProps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Connected to database");
		return conn;
	}

	public static Map<String,ScrapingProperties> getProviders() {
		Map<String,ScrapingProperties> msp = new HashMap<>();
		ResultSet rs = null;

		try {
			Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			String selectSql = 
					"SELECT nombre, link, uso, tipo, attributeName, valor "
							+ "FROM Provider "
							+ "JOIN Property ON Property.provider = Provider.link;";
			rs = statement.executeQuery(selectSql);
			System.out.println("GENERAMOS");
			System.out.println(rs.next());
			while (rs.next()) {
				System.out.println("DENTRO");
				String webPage =  (rs.getString("link"));

				ArrayList<Property> properties = new ArrayList<>();
				properties.add(new Property(rs.getString("uso"), 
						rs.getString("tipo"), rs.getString("attributeName"), rs.getString("valor")));
				rs.next();
				properties.add(new Property(rs.getString("uso"), 
						rs.getString("tipo"), rs.getString("attributeName"), rs.getString("valor")));
				rs.next();
				properties.add(new Property(rs.getString("uso"), 
						rs.getString("tipo"), rs.getString("attributeName"), rs.getString("valor")));

				System.out.println(webPage);
				System.out.println(properties);
				msp.put(webPage, new ScrapingProperties(rs.getString("nombre"), webPage, properties));


			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return msp;
	}

	public static void test() {
		Map<String,ScrapingProperties> msp = new HashMap<>();


		try {
			Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			String selectSql = "SELECT * FROM Provider";
			ResultSet rs = statement.executeQuery(selectSql);
			System.out.println(rs.next());
			if(rs.next()) {
				System.out.println("Obtained Resultset object is not empty its contents are:");
				rs.beforeFirst();
				while(rs.next()) {
					System.out.println(rs.getString("nombre"));
				}

			}else {
				System.out.println("Obtained ResultSet object is empty");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}





	public static void main (String[] args) {
		connect();
		System.out.println("Connection: " + conn);
		test();
	}

}
