package vanderzijden.notflix.model;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import vanderzijden.notflix.application.Log;
import vanderzijden.notflix.application.Util;

public class Model {
	
	private final Map<String, Movie> movies = new HashMap<>();
	private final Map<String, User> users = new HashMap<>();
	private final Map<String, Session> sessions = new HashMap<>();
	 
	private static final SecureRandom random = new SecureRandom();

	// *** GET ***
	
	public List<User> getUsers() {
		List<User> result = new ArrayList<>();
		for (String username : users.keySet()) {
			result.add(users.get(username));
		}
		return result;
	}
	
	public User getUser(String username) {
		User user = users.get(username);
		if (user == null) {
			Log.info(this, "User not found: " + username);
			throw new WebApplicationException(404);
		}
		return user;
	}
	
	/**
	 * Get movie by imdb tt number.
	 * Throws a 404 Not found if not found.
	 * 
	 * @param imdb_tt
	 * @return
	 */
	public Movie getMovie(String imdb_tt) {
		Movie movie = movies.get(imdb_tt);
		if (movie == null) {
			Log.info(this, "Movie not found: " + imdb_tt);
			throw new WebApplicationException(404);
		}
		return movie;
	}
	
	/**
	 * Get the session by token.
	 * Throws a 401 Unauthorized if no session for this token
	 * 
	 * @param token
	 * @return
	 */
	public Session getSession(String token) {
		Session session = sessions.get(token);
		if (session == null) {
			Log.info(this, "No session");
			throw new WebApplicationException(401);
		}
		return session;
	}
	/* This is too complicated and unnecessary for now.

	/**
	 * Fuzzy search for movies by title.
	 * Case-insensitive.
	 * Uses Levenshtein distance to match similar strings
	 * 
	 * If q is an empty string, all movies are returned.
	 *  
	 * @param q
	 * @param limit
	 * @param sorted
	 * @return
	 */
	/*
	public List<UserMovie> searchMovies(String q, User user) {
		q = Util.deAccent(q.toLowerCase());
		List<UserMovie> result = new ArrayList<>();
		for (String imdb_tt : movies.keySet()) {
			Movie movie = movies.get(imdb_tt);
			String title = Util.deAccent(movie.getTitle().toLowerCase());
			int editDistance;
			if (title.equals(q)) {
				editDistance = -1;
			} else if (title.contains(q)) {
				editDistance = 0;
			} else {
				editDistance = Util.getLevenshteinDistance(q, title);
			}
			if (editDistance < q.length() / 2 + 1) {
				result.add(new UserMovie(movie, user, editDistance));
			}
		}
		Collections.sort(result);
		return result;
	}
	*/
	/**
	 * Search for movies by title.
	 * 
	 * Case-insensitive and converts diacritical characters,
	 * e.g. 'é' to 'e'
	 * 
	 * @param q
	 * @return
	 */
	public List<Movie> searchMovies(String q) {
		if (q == null || q.equals("")) {
			return searchAllMovies();
		}
		q = Util.deAccent(q.toLowerCase());
		List<Movie> result = new ArrayList<>();
		for (Movie movie : movies.values()) {
			String title = Util.deAccent(movie.getTitle().toLowerCase());
			if (title.contains(q)) {
				result.add(movie);
			}
		}
		return result;
	}

	/**
	 * Get all movies as an arraylist/
	 * 
	 * @return
	 */
	
	public List<Movie> searchAllMovies() {
		return new ArrayList<>(movies.values());
	}
	
	// *** CREATE ***
	public Session createSession(String username, String password) {
		User user = users.get(username);
		if (user == null || !user.checkPassword(password)) {
			Log.info(this, "Invalid username/password");
			throw new WebApplicationException(401);
		}
		String token;
		do {
			token = new BigInteger(130, random).toString(32);
		} while (sessions.containsKey(token));
		Session session = new Session(user, token);
		sessions.put(token, session);
		return session;
	}
	
	public User createUser(String username, String firstName, String namePrepositins, String lastName, String password)
	{
		if (users.containsKey(username)) {
			Log.info(this, "Could not create duplicate user");
			throw new WebApplicationException(400);
		}
		User user = new User(username, firstName, namePrepositins, lastName, password);
		addUser(user);
		return user;
	}
	

	// *** OTHER ***
	public void addMovie(Movie movie) {
		movies.put(movie.getImdbID(), movie);
	}
	
	public void addUser(User user) {
		users.put(user.getUsername(), user);
	}
	
	public void clearSessions() {
		sessions.clear();
	}

}
