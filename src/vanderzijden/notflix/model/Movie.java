package vanderzijden.notflix.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONObject;

import vanderzijden.notflix.application.Log;

@XmlRootElement
public class Movie implements Comparable<Movie> {

	private static int movieNextId = 1;
	
	/** A currently useless field, only here because it's in the assignment... */
	private int id;
	private String imdbID;
	private String title;
	private long released;
	private int runtime;
	private String director;
	private String writer;
	private String actors;
	private String plot;
	private String genre;
	private String country;
	private String language;
	private String awards;
	private URI poster;
	
	// Context specific variables */
	
	protected final Map<String,Rating> ratings = new HashMap<>();


	
	/**
	 * Parsing OmdbApi JSON to Movie. We don't use Jackson to marshall it,
	 * because we don't strive to be compliant with the
	 * OmdbApi. This is just meant as a one-time use to fill
	 * the model with test data. 
	 * 
	 * @param json
	 * @return
	 */
	public static Movie parseFromOmdbApi(JSONObject json) {
		Movie movie = new Movie();
		movie.id = movieNextId++;
		movie.setActors(json.getString("Actors"));
		movie.setAwards(json.getString("Awards"));
		movie.setCountry(json.getString("Country"));
		movie.setDirector(json.getString("Director"));
		movie.setGenre(json.getString("Genre"));
		movie.setImdbID(json.getString("imdbID"));
		movie.setLanguage(json.getString("Language"));
		movie.setPlot(json.getString("Plot"));
		movie.setTitle(json.getString("Title"));
		movie.setWriter(json.getString("Writer"));
		String attr = "";
		try {
			attr = json.getString("Poster");
			movie.setPoster(new URI(attr));
		} catch (URISyntaxException e) {
			Log.warning(Movie.class, "Could not parse URI: "+  attr);
		}
		try
		{
			attr = json.getString("Released");
			if (!attr.contains("N/A")) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy z", Locale.US);
				movie.setReleased(sdf.parse(attr + " UTC").getTime() / 1000);
			}
		}
		catch (ParseException e) {
			Log.warning(Movie.class, "Could not parse released: " + attr);
		}
		try {
			attr = json.getString("Runtime");
			if (!attr.contains("N/A")) {
				Pattern pattern = Pattern.compile("^\\d+");
				Matcher matcher = pattern.matcher(attr);
				matcher.find();
				movie.setRuntime(Integer.parseInt(matcher.group()));
			}
		} catch (NumberFormatException | IllegalStateException e) {
			Log.warning(Movie.class, "Could not parse runtime: " + attr);
			Log.warning(Movie.class, e.getMessage());
		}
		return movie;
	}
	
	public Movie() {}
	
	/**
	 * Create a copy
	 * 
	 * @param movie
	 */
	public Movie(Movie movie) {
		id = movie.id;
		imdbID = movie.imdbID;
		title = movie.title;
		released = movie.released;
		runtime = movie.runtime;
		director = movie.director;
		writer = movie.writer;
		actors = movie.actors;
		plot = movie.plot;
		genre = movie.genre;
		country = movie.country;
		language = movie.language;
		awards = movie.awards;
		poster = movie.poster;
	}
	
	@XmlElement
	public Double getAvgRating() {
		if (ratings.size() == 0)
			return null;
		double sum = 0;
		for (String username : ratings.keySet()) {
			sum += ratings.get(username).getHalfStars();
		}
		return sum / ratings.size();
	}
	
	@XmlElement
	public int getVotes()
	{
		return ratings.size();
	}
	
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
			Log.info(this, "Rating not found for user=" + user.getUsername() + ", imdb_tt=" + imdbID);
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
			Log.info(this, "Rating already exists user=" + user.getUsername() + ", imdb_tt=" + imdbID);
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
			Log.info(this, "Could not delete rating because it doesn't exist for user=" + user.getUsername() + ", movie=" + imdbID);
			throw new WebApplicationException(404);
		}
		return rating;
	}
	
	public String getImdbID() {
		return imdbID;
	}

	public void setImdbID(String imdbID) {
		this.imdbID = imdbID;
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

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getActors() {
		return actors;
	}

	public void setActors(String actors) {
		this.actors = actors;
	}

	public String getPlot() {
		return plot;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAwards() {
		return awards;
	}

	public void setAwards(String awards) {
		this.awards = awards;
	}

	public URI getPoster() {
		return poster;
	}

	public void setPoster(URI poster) {
		this.poster = poster;
	}

	@Override
	public int compareTo(Movie movie) {
		return getTitle().compareTo(movie.getTitle());
	}

	
}
