package vanderzijden.notflix.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Person {

	
	private String name;
	private String dbpUri;
	private String wikiUri;
	private String thumbNail;
	private long dateOfBirth;
	private String abstract2;
	
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
	public String getThumbNail()
	{
		return thumbNail;
	}
	public void setThumbNail(String thumbNail)
	{
		this.thumbNail = thumbNail;
	}
	public long getDateOfBirth()
	{
		return dateOfBirth;
	}
	public void setDateOfBirth(long dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}
	public String getAbstract2()
	{
		return abstract2;
	}
	public void setAbstract2(String abstract2)
	{
		this.abstract2 = abstract2;
	}
	
	
}
