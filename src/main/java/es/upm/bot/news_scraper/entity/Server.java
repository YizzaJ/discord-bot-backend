package es.upm.bot.news_scraper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Server")
public class Server {
 
    @Id
    @Column(name = "ID")
    private Long id;
 
    @Column(name = "servername", nullable = false)
    private String serverName;
    
   
	public Server(Long id, String servername) {
		this.id = id;
		this.serverName = servername;
	}


	protected Server() {
	}

    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getServername() {
		return serverName;
	}

	public void setServername(String servername) {
		this.serverName = servername;
	}
    
    
}
