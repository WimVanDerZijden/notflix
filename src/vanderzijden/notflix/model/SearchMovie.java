package vanderzijden.notflix.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A movie with context-specific attributes.
 * 
 * Namely, the user requesting the resource,
 * and the editDistance indicating how strong the match is
 * (lower is better)
 *
 * @author Wim van der Zijden
 *
 */
@XmlRootElement
public class SearchMovie extends Movie {
	
	private User user;
	/** Levenshtein distance */
	private int editDistance;
	
	public SearchMovie() {
		super();
	}
	
	/**
	 * Create from Movie
	 * 
	 * @param movie
	 */
	public SearchMovie(Movie movie, User user, int editDistance) {
		super(movie);
		this.user = user;
		this.editDistance = editDistance;
	}
	
	public int getEditDistance() {
		return editDistance;
	}

	public void setEditDistance(int editDistance) {
		this.editDistance = editDistance;
	}
	
	
	/**
	 * Get rating for this user.
	 * 
	 * Throws a "404 Not Found" if no rating was found.
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

	@Override
	public int compareTo(Movie movie) {
		if (movie instanceof SearchMovie) {
			SearchMovie smovie = (SearchMovie) movie;
			if (smovie.editDistance != editDistance) {
				return editDistance - smovie.editDistance;
			}
		}
		return super.compareTo(movie);
	}
}
