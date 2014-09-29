package vanderzijden.notflix.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {

	public String username;
	public String firstName;
	public String namePrepositions;
	public String lastName;

	private String password;

	public User() {	}

	public User(String username, String firstName, String namePrepositions, String lastName, String password) {
		super();
		this.username = username;
		this.firstName = firstName;
		this.namePrepositions = namePrepositions;
		this.lastName = lastName;
		this.password = password;
	}

	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}
}
