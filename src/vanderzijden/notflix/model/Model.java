package vanderzijden.notflix.model;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import vanderzijden.notflix.application.Log;

public class Model {
	
	private final Map<String, Movie> movies = new HashMap<>();
	private final Map<String, User> users = new HashMap<>();
	private final Map<String, Session> sessions = new HashMap<>();
	 
	private int moviesNextId = 1;
	
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
	/**
	 * Search for q in titles and directors.
	 * Case-insensitive.
	 * 
	 * If q is an empty string, all movies are returned 
	 * 
	 * @param q
	 * @return
	 */
	
	public List<Movie> searchMovies(String q) {
		q = q.toUpperCase();
		List<Movie> result = new ArrayList<>();
		for (String imdb_tt : movies.keySet()) {
			Movie movie = movies.get(imdb_tt);
			if (movie.getTitle().toUpperCase().contains(q) ||
					movie.getDirector().toUpperCase().contains(q)) {
				result.add(movie);
			}
		}
		return result;
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
		movies.put(movie.getImdb_tt(), movie);
	}
	
	public void addUser(User user) {
		users.put(user.getUsername(), user);
	}
	
	public void loadTestData() {
		System.out.println("Loading test data..");
		addMovie(new Movie(moviesNextId++, "tt0042876", "Rashômon", -568598400, 88, "Akira Kurosawa",
				"A heinous crime and its aftermath are recalled from differing points of view."));

		addMovie(new Movie(moviesNextId++, "tt0071853", "Monty Python and the Holy Grail", 217468800, 91, "Terry Gilliam & Terry Jones",
				"King Arthur and his knights embark on a low-budget search for the Grail, encountering many very silly obstacles."));
		//addMovie(new Movie(moviesNextId++, "", "", 0, 0, "",
		//		""));
		addUser(new User("wim", "Wim","van der", "Zijden","wim"));
		addUser(new User("pim", "Pim",null, "Teunissen","pim"));
		
		sessions.put("abc", new Session(users.get("wim"), "abc"));
		
	}

}
