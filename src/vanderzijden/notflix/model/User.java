package vanderzijden.notflix.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {

	@XmlElement
	private String username;
	@XmlElement
	private String firstName;
	@XmlElement
	private String namePrepositions;
	@XmlElement
	private String lastName;

	private String password;

	public User() {	}

	public User(String username, String firstName, String namePrepositions, String lastName, String password) {
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
