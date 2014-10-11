package vanderzijden.notflix.resource.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import vanderzijden.notflix.model.Movie;
import vanderzijden.notflix.model.Rating;
import vanderzijden.notflix.model.User;

/**
 * A movie with context-specific attributes.
 * 
 * Currently only the user requesting the resource,
 * so that his rating can be added.
 *
 * @author Wim van der Zijden
 *
 */

@XmlRootElement(name = "movie")
public class UserMovie extends Movie {
	
	private User user;

	public UserMovie() {
		super();
	}
	
	/**
	 * Create from Movie
	 * 
	 * @param movie
	 */
	public UserMovie(Movie movie, User user) {
		super(movie);
		this.user = user;
	}
	
	/**
	 * Get rating for this user.
	 * 
	 * @param user
	 * @return
	 */
	@XmlElement
	public Rating getRating() {
		if (user != null) {
			return ratings.get(user.getUsername());
		}
		return null;
	}

}
