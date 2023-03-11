package es.upm.bot.news_scraper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "User")
public class User {
    
    @Id
    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "serverID", nullable = false)
    private Long serverID;
    
    @Column(name = "provider", nullable = false)
    private String provider;

    
	public User(String username, Long serverID, String provider) {
		this.username = username;
		this.serverID = serverID;
		this.provider = provider;
	}


	protected User() {
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getProvider() {
		return provider;
	}


	public void setProvider(String provider) {
		this.provider = provider;
	}


	public Long getServerID() {
		return serverID;
	}


	public void setServerID(Long serverID) {
		this.serverID = serverID;
	}


}
