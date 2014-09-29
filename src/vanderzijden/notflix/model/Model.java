package vanderzijden.notflix.model;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
	
	private Map<String, Movie> movies = new HashMap<>();
	private Map<String, User> users = new HashMap<>();
	private Map<String, Session> sessions = new HashMap<>();
	 
	private int moviesNextId = 1;
	
	private static final SecureRandom random = new SecureRandom();

	// *** GET ***
	public Movie getMovie(String imdb_tt) {
		return movies.get(imdb_tt);
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
		if (user == null || !user.checkPassword(password))
			return null;
		String token;
		do {
			token = new BigInteger(130, random).toString(32);
		} while (sessions.containsKey(token));
		Session session = new Session(user, token);
		sessions.put(token, session);
		return session;
	}
	
	// *** OTHER ***
	public void addMovie(Movie movie) {
		movies.put(movie.getImdb_tt(), movie);
	}
	
	public void addUser(User user) {
		users.put(user.username, user);
	}
	
	public void loadTestData() {
		addMovie(new Movie(moviesNextId++, "tt0042876", "Rashômon", -568598400, 88, "Akira Kurosawa",
				"A heinous crime and its aftermath are recalled from differing points of view."));

		addMovie(new Movie(moviesNextId++, "tt0071853", "Monty Python and the Holy Grail", 217468800, 91, "Terry Gilliam & Terry Jones",
				"King Arthur and his knights embark on a low-budget search for the Grail, encountering many very silly obstacles."));
		//addMovie(new Movie(moviesNextId++, "", "", 0, 0, "",
		//		""));
		addUser(new User("wim", "Wim","van der", "Zijden","wim"));
		addUser(new User("pim", "Pim",null, "Teunissen","pim"));
		
	}

}
