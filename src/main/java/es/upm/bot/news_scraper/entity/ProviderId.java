package es.upm.bot.news_scraper.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProviderId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "serverID")
    private Long serverID;

    @Column(name = "link")
    private String link;

	public ProviderId(Long serverId, String link) {
		this.serverID = serverId;
		this.link = link;
	}
	
	protected ProviderId() {

	}

	public Long getServerID() {
		return serverID;
	}

	public void setServerID(Long serverID) {
		this.serverID = serverID;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	

    
}
