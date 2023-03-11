package es.upm.bot.news_scraper.not.used;

import java.util.List;

import es.upm.bot.news_scraper.entity.Provider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Server")
public class ServerNO {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "servername")
    private String servername;

    @OneToMany(mappedBy = "server")
    private List<Provider> providers;

    @OneToMany(mappedBy = "server")
    private List<UserNO> users;
    
   
    
	public ServerNO(Long id, String servername, List<Provider> providers, List<UserNO> users) {
		this.id = id;
		this.servername = servername;
		this.providers = providers;
		this.users = users;
	}



	protected ServerNO() {
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getServername() {
		return servername;
	}



	public void setServername(String servername) {
		this.servername = servername;
	}



	public List<Provider> getProviders() {
		return providers;
	}



	public void setProviders(List<Provider> providers) {
		this.providers = providers;
	}



	public List<UserNO> getUsers() {
		return users;
	}



	public void setUsers(List<UserNO> users) {
		this.users = users;
	}



	@Override
	public String toString() {
		return "Server [id=" + id + ", servername=" + servername + ", providers=" + providers + ", users=" + users
				+ "]";
	}
    
	
    
}
