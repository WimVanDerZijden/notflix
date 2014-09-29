package vanderzijden.notflix.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Session {
	
	private User user;
	
	public String token;
	
	public Session() {}

	public Session(User user, String token) {
		this.user = user;
		this.token = token;
	}
	
	public User getUser() {
		return user;
	}
}
