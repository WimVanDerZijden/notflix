package vanderzijden.notflix.model;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import vanderzijden.notflix.application.Log;

@XmlRootElement
public class User {

	/** Required */
	@XmlElement
	private String username;
	/** Required */
	@XmlElement
	private String firstName;
	/** Optional */
	@XmlElement
	private String namePrepositions;
	/** Required */
	@XmlElement
	private String lastName;

	/** Required */
	private String password;

	public User() {	}

	public User(String username, String firstName, String namePrepositions, String lastName, String password) {
		if (username == null || firstName == null || lastName == null || password == null ||
				username.length() == 0 || firstName.length() == 0 || lastName.length() == 0 || password.length() == 0) {
			Log.info(this, "Missing one or more mandatory parameters");
			throw new WebApplicationException(400);
		}
		this.username = username;
		this.firstName = firstName;
		this.namePrepositions = namePrepositions;
		this.lastName = lastName;
		this.password = password;
	}

	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}

	public String getUsername() {
		return username;
	}

}
