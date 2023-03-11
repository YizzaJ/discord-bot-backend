package es.upm.bot.news_scraper.not.used;

import es.upm.bot.news_scraper.entity.Provider;
import es.upm.bot.news_scraper.entity.Server;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "User")
public class UserNO {

	@Id
    @Column(name = "username", nullable = false)
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serverID", referencedColumnName = "ID", nullable = false)
    private Server server;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider", referencedColumnName = "link", nullable = false)
    private Provider provider;

    
    
	public UserNO(String username, Server server, Provider provider) {
		this.username = username;
		this.server = server;
		this.provider = provider;
	}

	protected UserNO() {
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

    
    @Override
	public String toString() {
		return "User [username=" + username + ", server=" + server + ", provider=" + provider + "]";
	}


}
