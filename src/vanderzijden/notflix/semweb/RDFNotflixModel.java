package vanderzijden.notflix.semweb;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

import vanderzijden.notflix.application.Log;
import vanderzijden.notflix.application.Util;
import vanderzijden.notflix.model.Movie;
import vanderzijden.notflix.model.NotflixModel;
import vanderzijden.notflix.model.Session;
import vanderzijden.notflix.model.User;

public class RDFNotflixModel extends RDFModel implements NotflixModel
{

	public RDFNotflixModel(String ontologyLocation, String modelLocation)
	{
		super(ontologyLocation, modelLocation);
	}

	public RDFNotflixModel(String ontologyLocation)
	{
		super(ontologyLocation);
	}

	public void save(String filename)
	{
		try
		{
			FileWriter fw = new FileWriter(filename);
			model.write(fw);
			fw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void addMovie(Movie movie)
	{
		Resource aFilm = model.createResource(DATA_NS + movie.getImdbID());
		aFilm.addProperty(RDF.type, movieType);
		aFilm.addProperty(imdbID, movie.getImdbID());
		aFilm.addProperty(title, movie.getTitle());
		linkDBpedia(aFilm, movie.getTitle());
	}

	private void linkDBpedia(Resource resource, String label)
	{
		String queryString =
				"SELECT ?o " +
						"WHERE " +
						"{ " +
						"   ?o <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Film> . " +
						"   ?o <http://www.w3.org/2000/01/rdf-schema#label> '" + label + "'@en . " +
						"}";
		System.out.println(queryString);
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DBPEDIA_EP, query);
		try
		{
			ResultSet results = qexec.execSelect();
			if (results.hasNext())
			{
				QuerySolution qs = results.next();
				Resource remote = qs.getResource("?o");
				resource.addProperty(OWL.sameAs, remote);
			}
		}
		finally
		{
			qexec.close();
		}
	}

	// *** GET ***

	@Override
	public User getUser(String username)
	{
		User user = users.get(username);
		if (user == null)
		{
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
	@Override
	public Movie getMovie(String imdb_tt)
	{
		return movieFromResource(model.createResource(DATA_NS + imdb_tt));
	}

	public Movie movieFromResource(Resource resource)
	{
		Movie movie = new Movie();
		String imdbId = getString(resource, this.imdbID);
		if (imdbId == null)
			throw new WebApplicationException(404);
		movie.setImdbID(imdbId);
		movie.setTitle(getString(resource, title));
		movie.setDbpUri(getUriString(resource, OWL.sameAs));
		return movie;
	}

	/**
	 * Get the session by token.
	 * Throws a 401 Unauthorized if no session for this token
	 * 
	 * @param token
	 * @return
	 */
	@Override
	public Session getSession(String token)
	{
		Session session = sessions.get(token);
		if (session == null)
		{
			Log.info(this, "No session");
			throw new WebApplicationException(401);
		}
		return session;
	}

	/**
	 * Search for movies by title.
	 * 
	 * Case-insensitive and converts diacritical characters,
	 * e.g. '�' to 'e'
	 * 
	 * @param q
	 * @return
	 */
	@Override
	public List<Movie> searchMovies(String q)
	{
		List<Movie> movies = searchAllMovies();
		if (q == null || q.equals(""))
		{
			return movies;
		}
		q = Util.toPlainText(q);
		List<Movie> result = new ArrayList<>();
		for (Movie movie : movies)
		{
			if (movie.getPlainTitle().contains(q))
			{
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
	@Override
	public List<Movie> searchAllMovies()
	{
		List<Movie> movies = new ArrayList<Movie>();
		ResIterator iter = model.listResourcesWithProperty(RDF.type, movieType);
		while (iter.hasNext())
		{
			Resource resource = iter.next();
			Movie movie = movieFromResource(resource);
			if (movie != null)
				movies.add(movie);
		}
		return movies;
	}

	@Override
	public List<User> searchAllUsers()
	{
		return new ArrayList<>(users.values());
	}

	@Override
	public List<User> searchUsers(String q)
	{
		if (q == null || q.equals(""))
		{
			return searchAllUsers();
		}
		q = Util.toPlainText(q);
		List<User> result = new ArrayList<>();
		for (User user : users.values())
		{
			if (Util.toPlainText(user.getUsername()).contains(q) ||
					Util.toPlainText(user.getFirstName()).contains(q) ||
					Util.toPlainText(user.getLastName()).contains(q) ||
					Util.toPlainText(user.getNamePrepositions()).contains(q))
			{
				result.add(user);
			}
		}
		return result;
	}

	// *** CREATE ***
	@Override
	public Session createSession(String username, String password)
	{
		User user = users.get(username);
		if (user == null || !user.checkPassword(password))
		{
			Log.info(this, "Invalid username/password");
			throw new WebApplicationException(401);
		}
		String token;
		do
		{
			token = new BigInteger(130, random).toString(32);
		}
		while (sessions.containsKey(token));
		Session session = new Session(user, token);
		sessions.put(token, session);
		return session;
	}

	@Override
	public User createUser(String username, String firstName, String namePrepositins, String lastName, String password)
	{
		if (users.containsKey(username))
		{
			Log.info(this, "Could not create duplicate user");
			throw new WebApplicationException(400);
		}
		User user = new User(username, firstName, namePrepositins, lastName, password);
		addUser(user);
		return user;
	}

	@Override
	public void addUser(User user)
	{
		users.put(user.getUsername(), user);
	}

	@Override
	public void clearSessions()
	{
		sessions.clear();
	}

	private final Map<String, Movie> movies = new HashMap<>();
	private final Map<String, User> users = new HashMap<>();
	private final Map<String, Session> sessions = new HashMap<>();

	private static final SecureRandom random = new SecureRandom();

}
