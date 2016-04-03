package vanderzijden.notflix.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Director {

	
	private String name;
	private String dbpUri;
	private String wikiUri;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDbpUri() {
		return dbpUri;
	}
	public void setDbpUri(String dbpUri) {
		this.dbpUri = dbpUri;
	}
	public String getWikiUri() {
		return wikiUri;
	}
	public void setWikiUri(String wikiUri) {
		this.wikiUri = wikiUri;
	}
	
	
}
