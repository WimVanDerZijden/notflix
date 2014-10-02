package vanderzijden.notflix.model;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import vanderzijden.notflix.application.Log;

@XmlRootElement
public class Movie {

	/** A currently useless field, only here because it's in the assignment... */
	private int id;
	@XmlElement
	private String imdb_tt;
	@XmlElement
	private String title;
	@XmlElement
	private long released;
	@XmlElement
	private int length;
	@XmlElement
	private String director;
	@XmlElement
	private String description;
	
	private final Map<String,Rating> ratings = new HashMap<>();

	/**
	 * Get rating for this user.
	 * 
	 * Throws a "404 Not Found" if no rating was found.
	 * 
	 * @param user
	 * @return
	 */

	public Rating getRating(User user) {
		Rating rating = ratings.get(user.getUsername());
		if (rating == null) {
			Log.info(this, "Rating not found for user=" + user.getUsername() + ", imdb_tt=" + imdb_tt);
			throw new WebApplicationException(404);
		}
		return rating;
	}
	
	/**
	 * Create new rating for this user.
	 * 
	 * Throws a "400 Bad Request" if a rating already exists
	 * 
	 * @param user
	 * @param halfStars
	 * @return
	 */
	public Rating createRating(User user, int halfStars) {
		if (ratings.containsKey(user.getUsername())) {
			Log.info(this, "Rating already exists user=" + user.getUsername() + ", imdb_tt=" + imdb_tt);
			throw new WebApplicationException(400);
		}
		Rating rating = new Rating(user, halfStars);
		ratings.put(user.getUsername(), rating);
		return rating;
	}
	
	/**
	 * Update the rating for this user.
	 * 
	 * Throws a "404 Not found" if no rating was found.
	 * 
	 * @param user
	 * @param halfStars
	 * @return
	 */
	public Rating updateRating(User user, int halfStars) {
		Rating rating = getRating(user);
		rating.setHalfStars(halfStars);
		return rating;
	}
	
	public Rating deleteRating(User user) {
		Rating rating = ratings.remove(user.getUsername());
		if (rating == null) {
			Log.info(this, "Could not delete rating because it doesn't exist for user=" + user.getUsername() + ", movie=" + imdb_tt);
			throw new WebApplicationException(404);
		}
		return rating;
	}
	
	public Movie() {}

	public Movie(int id, String imdb_tt, String title, long released, int length, String director, String description) {
		this.id = id;
		this.imdb_tt = imdb_tt;
		this.title = title;
		this.released = released;
		this.length = length;
		this.director = director;
		this.description = description;
	}

	public String getImdb_tt() {
		return imdb_tt;
	}

	public String getTitle() {
		return title;
	}
	
	public String getDirector() {
		return director;
	}

}
