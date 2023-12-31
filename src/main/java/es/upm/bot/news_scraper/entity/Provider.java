package es.upm.bot.news_scraper.entity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Provider")
public class Provider {
	
    @EmbeddedId
    private ProviderId providerId;
	
    @Column(name = "nombre", nullable = false)
    private String nombre;
    

    @Column(name = "uso_articulo", nullable = false)
    private String usoArticulo;

    @Column(name = "tipo_articulo", nullable = false)
    private String tipoArticulo;

    @Column(name = "attributeName_articulo")
    private String attributeNameArticulo;

    @Column(name = "valor_articulo", nullable = false)
    private String valorArticulo;

    @Column(name = "uso_parrafo", nullable = false)
    private String usoParrafo;

    @Column(name = "tipo_parrafo", nullable = false)
    private String tipoParrafo;

    @Column(name = "attributeName_parrafo")
    private String attributeNameParrafo;

    @Column(name = "valor_parrafo", nullable = false)
    private String valorParrafo;

    @Column(name = "uso_topic", nullable = false)
    private String usoTopic;

    @Column(name = "tipo_topic", nullable = false)
    private String tipoTopic;

    @Column(name = "attributeName_topic")
    private String attributeNameTopic;

    @Column(name = "valor_topic", nullable = false)
    private String valorTopic;

	public Provider(String link, String nombre, Long serverID, String usoArticulo, String tipoArticulo,
			String attributeNameArticulo, String valorArticulo, String usoParrafo, String tipoParrafo,
			String attributeNameParrafo, String valorParrafo, String usoTopic, String tipoTopic,
			String attributeNameTopic, String valorTopic) {
		providerId = new ProviderId(serverID, link);

		this.nombre = nombre;
		this.usoArticulo = usoArticulo;
		this.tipoArticulo = tipoArticulo;
		this.attributeNameArticulo = attributeNameArticulo;
		this.valorArticulo = valorArticulo;
		this.usoParrafo = usoParrafo;
		this.tipoParrafo = tipoParrafo;
		this.attributeNameParrafo = attributeNameParrafo;
		this.valorParrafo = valorParrafo;
		this.usoTopic = usoTopic;
		this.tipoTopic = tipoTopic;
		this.attributeNameTopic = attributeNameTopic;
		this.valorTopic = valorTopic;
	}
	
	protected Provider() {
		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getUsoArticulo() {
		return usoArticulo;
	}

	public void setUsoArticulo(String usoArticulo) {
		this.usoArticulo = usoArticulo;
	}

	public String getTipoArticulo() {
		return tipoArticulo;
	}

	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
	}

	public String getAttributeNameArticulo() {
		return attributeNameArticulo;
	}

	public void setAttributeNameArticulo(String attributeNameArticulo) {
		this.attributeNameArticulo = attributeNameArticulo;
	}

	public String getValorArticulo() {
		return valorArticulo;
	}

	public void setValorArticulo(String valorArticulo) {
		this.valorArticulo = valorArticulo;
	}

	public String getUsoParrafo() {
		return usoParrafo;
	}

	public void setUsoParrafo(String usoParrafo) {
		this.usoParrafo = usoParrafo;
	}

	public String getTipoParrafo() {
		return tipoParrafo;
	}

	public void setTipoParrafo(String tipoParrafo) {
		this.tipoParrafo = tipoParrafo;
	}

	public String getAttributeNameParrafo() {
		return attributeNameParrafo;
	}

	public void setAttributeNameParrafo(String attributeNameParrafo) {
		this.attributeNameParrafo = attributeNameParrafo;
	}

	public String getValorParrafo() {
		return valorParrafo;
	}

	public void setValorParrafo(String valorParrafo) {
		this.valorParrafo = valorParrafo;
	}

	public String getUsoTopic() {
		return usoTopic;
	}

	public void setUsoTopic(String usoTopic) {
		this.usoTopic = usoTopic;
	}

	public String getTipoTopic() {
		return tipoTopic;
	}

	public void setTipoTopic(String tipoTopic) {
		this.tipoTopic = tipoTopic;
	}

	public String getAttributeNameTopic() {
		return attributeNameTopic;
	}

	public void setAttributeNameTopic(String attributeNameTopic) {
		this.attributeNameTopic = attributeNameTopic;
	}

	public String getValorTopic() {
		return valorTopic;
	}

	public void setValorTopic(String valorTopic) {
		this.valorTopic = valorTopic;
	}

	public ProviderId getProviderId() {
		return providerId;
	}

	public void setProviderId(ProviderId providerId) {
		this.providerId = providerId;
	}

	@Override
	public String toString() {
		return "link=" + providerId.getLink() + ", nombre=" + nombre + ", serverID=" + providerId.getServerID() + ", usoArticulo="
				+ usoArticulo + ", tipoArticulo=" + tipoArticulo + ", attributeNameArticulo=" + attributeNameArticulo
				+ ", valorArticulo=" + valorArticulo + ", usoParrafo=" + usoParrafo + ", tipoParrafo=" + tipoParrafo
				+ ", attributeNameParrafo=" + attributeNameParrafo + ", valorParrafo=" + valorParrafo + ", usoTopic="
				+ usoTopic + ", tipoTopic=" + tipoTopic + ", attributeNameTopic=" + attributeNameTopic + ", valorTopic="
				+ valorTopic;
	}
	
    public String toJson() {
	
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("webSite", providerId.getLink())
                .add("webSiteName", this.nombre)
                .add("serverID", providerId.getServerID())
                .add("usoArticulo", this.usoArticulo)
                .add("tipoArticulo", this.tipoArticulo)
                .add("attributeNameArticulo", this.attributeNameArticulo)
                .add("valorArticulo", this.valorArticulo)
                .add("usoParrafo", this.usoParrafo)
                .add("tipoParrafo", this.tipoParrafo)
                .add("attributeNameParrafo", this.attributeNameParrafo)
                .add("valorParrafo", this.valorParrafo)
                .add("usoTopic", this.usoTopic)
                .add("tipoTopic", this.tipoTopic)
                .add("attributeNameTopic", this.attributeNameTopic)
                .add("valorTopic", this.valorTopic);
        JsonObject jsonObj = builder.build();
        return jsonObj.toString();
    }
	
	


}
