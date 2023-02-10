package es.upm.bot.news_scraper.elements;

public class Property {
	
	private String use;
	private String type; //tag,class,attribute.....
	private String attributeName; // if type is an Attribute
	private String value; //name of the type. if type is Attribute this is de content of the attribute
	
	public Property(String use, String type, String value) {
		this.use = use;
		this.type = type;
		this.value = value;
	}
	
	public Property(String use, String attr, String attributeName, String value) {
		this.use = use;
		this.type = attr;
		this.attributeName = attributeName;
		this.value = value;
	}

	public String getUse() {
		return use;
	}

	public String getType() {
		return type;
	}
	
	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getValue() {
		return value;
	}
	
	
	
	

}
