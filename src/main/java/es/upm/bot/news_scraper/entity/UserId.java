package es.upm.bot.news_scraper.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "serverID")
    private Long serverID;

    @Column(name = "username")
    private String username;

	public UserId(Long serverId, String username) {
		this.serverID = serverId;
		this.username = username;
	}
	
	protected UserId() {

	}

	public Long getServerID() {
		return serverID;
	}

	public void setServerID(Long serverID) {
		this.serverID = serverID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	

    
}
