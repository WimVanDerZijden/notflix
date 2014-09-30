package vanderzijden.notflix.model;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Movie {

	private int id;
	private String imdb_tt;
	private String title;
	private long released;
	private int length;
	private String director;
	private String description;
	
	private final Map<String,Rating> ratings = new HashMap<>();

	public Rating createRating(User user, int halfStars) {
		if (ratings.containsKey(user.username))
			throw new WebApplicationException(400);
		Rating rating = new Rating(user, halfStars);
		ratings.put(user.username, rating);
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
	
	protected int getId() {
		return id;
	}

	public String getImdb_tt() {
		return imdb_tt;
	}

	public void setImdb_tt(String imdb_tt) {
		this.imdb_tt = imdb_tt;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getReleased() {
		return released;
	}

	public void setReleased(long released) {
		this.released = released;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
