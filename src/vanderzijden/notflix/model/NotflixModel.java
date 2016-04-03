package vanderzijden.notflix.model;

import java.util.List;

public interface NotflixModel
{

	User getUser(String username);
	
	Movie getMovie(String imdb_tt, String lang);
	
	Session getSession(String token);
	
	List<Movie> searchMovies(String q);
	
	List<Movie> searchAllMovies();
	
	List<User> searchAllUsers();
	
	List<User> searchUsers(String q);
	
	Session createSession(String username, String password);
	
	User createUser(String username, String firstName, String namePrepositins, String lastName, String password);
	
	void addMovie(Movie movie);
	
	void addUser(User user);
	
	void clearSessions();
	
	void save(String filename);
}
