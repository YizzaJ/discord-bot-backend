package es.upm.bot.news_scraper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "User")
public class User {
	
    @EmbeddedId
    private UserId userId;

    @Column(name = "provider", nullable = false)
    private String provider;

    
	public User(String username, Long serverID, String provider) {
		this.userId = new UserId(serverID, username);
		this.provider = provider;
	}


	protected User() {
	}


	public String getUsername() {
		return userId.getUsername();
	}


	public void setUsername(String username) {
		this.userId.setUsername(username);
	}


	public String getProvider() {
		return provider;
	}


	public UserId getUserId() {
		return userId;
	}


	public void setUserId(UserId userId) {
		this.userId = userId;
	}


	public void setProvider(String provider) {
		this.provider = provider;
	}

}
