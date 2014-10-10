package vanderzijden.notflix.resource.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import vanderzijden.notflix.model.Movie;
import vanderzijden.notflix.model.User;

@XmlRootElement(name = "Movies")
public class UserMovies extends ArrayList<UserMovie>{

	private static final long serialVersionUID = 1L;

	public URI previous;
	
	public URI next;
	
	public UserMovies() {}
	
	public static UserMovies get(List<Movie> movies, User user, URI previous, URI next) {
		UserMovies result = new UserMovies();
		for (Movie movie : movies) {
			result.add(new UserMovie(movie, user));
		}
		if (previous != null) {
			result.previous = previous;
		}
		if (next != null) {
			result.next = next;
		}
		return result;
	}
}
