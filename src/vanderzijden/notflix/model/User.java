package vanderzijden.notflix.model;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlRootElement;

import vanderzijden.notflix.application.Log;

@XmlRootElement
public class User {

	/** Required */
	private String username;
	/** Required */
	private String firstName;
	/** Optional */
	private String namePrepositions;
	/** Required */
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

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getNamePrepositions() {
		return namePrepositions;
	}

	public void setNamePrepositions(String namePrepositions) {
		this.namePrepositions = namePrepositions;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
