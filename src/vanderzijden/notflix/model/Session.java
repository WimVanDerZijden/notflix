package vanderzijden.notflix.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Session {
	
	private User user;
	
	@XmlElement
	private String token;
	
	public Session() {}

	public Session(User user, String token) {
		this.user = user;
		this.token = token;
	}
	
	public User getUser() {
		return user;
	}
}
