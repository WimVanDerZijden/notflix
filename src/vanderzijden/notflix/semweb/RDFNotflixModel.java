package vanderzijden.notflix.semweb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import vanderzijden.notflix.application.Log;
import vanderzijden.notflix.application.Util;
import vanderzijden.notflix.model.Movie;
import vanderzijden.notflix.model.NotflixModel;
import vanderzijden.notflix.model.Person;
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
			FileOutputStream fo = new FileOutputStream(new File(filename));
			model.write(fo);
			fo.close();
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
		aFilm.addProperty(plot, movie.getPlot());
		aFilm.addProperty(poster, movie.getPoster().toASCIIString());
		aFilm.addProperty(imdbVotes, "" + movie.getImdbVotes());
		aFilm.addProperty(imdbRating, "" + movie.getImdbRating());
		aFilm.addProperty(released, "" + movie.getReleased());
		aFilm.addProperty(runtime, "" + movie.getRuntime());
		aFilm.addProperty(director, movie.getDirector());
		aFilm.addProperty(writer, movie.getWriter());
		aFilm.addProperty(actors, movie.getActors());
		//linkDBpedia(aFilm, movie.getTitle());
	}
	
	private void linkDBpedia(Resource resource, String label, int runtime)
	{
		// Match dbpedia on title=label(eng) and runtime=runtime
		ParameterizedSparqlString queryString = new ParameterizedSparqlString(
				"SELECT ?o " + 
				"WHERE  " + 
				"{ " + 
				"     ?o <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Film> . " + 
				"     ?o <http://www.w3.org/2000/01/rdf-schema#label> ?title . " + 
				"     ?o <http://dbpedia.org/ontology/Work/runtime> ?runtime . " + 
				"     FILTER (LANG(?title) =  'en' && REGEX(?title,?x) && <http://www.w3.org/2001/XMLSchema#integer>(?runtime) = ?y) . " + 
				"} " + 
				"");
		queryString.setLiteral("x", label);
		queryString.setLiteral("y", runtime);
		System.out.println(queryString);
		
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DBPEDIA_EP, queryString.asQuery());
		Resource remote = null;
		try
		{
			ResultSet results = qexec.execSelect();
			if (results.hasNext())
			{
				QuerySolution qs = results.next();
				remote = qs.getResource("?o");
				resource.addProperty(OWL.sameAs, remote);
			}
		}
		finally
		{
			qexec.close();
		}
		if (remote != null)
			enhanceWithDBpedia(resource, remote);
	}
	
	private void enhanceWithDBpedia(Resource resource, Resource remote)
	{
		ParameterizedSparqlString queryString = new ParameterizedSparqlString(
				"SELECT ?s ?p ?o " + 
				"WHERE " + 
				"{ " + 
				"  {  " + 
				"    ?s ?p ?o . " + 
				"    FILTER (?s = ?x) " + 
				"  } " + 
				"  UNION " + 
				"  { " + 
				"     ?s ?p ?o . " + 
				"     ?x ?props ?s . " + 
				"     FILTER (?props = <http://dbpedia.org/ontology/director> || " + 
				"             ?props = <http://dbpedia.org/ontology/starring> || " + 
				"             ?props = <http://dbpedia.org/ontology/writer>) . " + 
				"  } " + 
				"}");
		queryString.setParam("x", remote);
		System.out.println(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DBPEDIA_EP, queryString.asQuery());
		try
		{
			ResultSet results = qexec.execSelect();
			while (results.hasNext())
			{
				QuerySolution qs = results.next();
				Resource subject = qs.getResource("?s"); 
				Resource property = qs.getResource("?p");
				RDFNode object = qs.get("?o");
				model.add(subject, ontology.createProperty(property.getURI()), object);
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
	public Movie getMovie(String imdb_tt, String lang)
	{
		Resource resource = model.createResource(DATA_NS + imdb_tt);
		Movie movie = movieFromResource(resource);
		if (movie.getDbpUri() == null)
		{
			linkDBpedia(resource, movie.getTitle(), movie.getRuntime());
			movie.setDbpUri(getUriString(resource, OWL.sameAs));
		}
		Resource remote = model.createResource(movie.getDbpUri());
		movie.setAbstract2(getString(remote, ontology.createProperty(DBPEDIA.ABSTRACT), lang));
		movie.setTitle(getString(remote, RDFS.label, lang));
		movie.setDirectors(getPersonsForMovie(remote, ontology.createProperty("http://dbpedia.org/ontology/director"), lang));
		movie.setActors2(getPersonsForMovie(remote, ontology.createProperty("http://dbpedia.org/ontology/starring"), lang));
		movie.setWriters(getPersonsForMovie(remote, ontology.createProperty("http://dbpedia.org/ontology/writer"), lang));
		return movie;
	}
	
	public List<Person> getPersonsForMovie(Resource movie, Property property, String lang)
	{
		List<Person> directors = new ArrayList<>();
		NodeIterator iter = model.listObjectsOfProperty(movie, property);
		while (iter.hasNext())
		{
			RDFNode director = iter.next();
			directors.add(personFromResource(director.asResource(), lang));
		}
		return directors;
	}
	
	public Person personFromResource(Resource resource, String lang)
	{
		Person person = new Person();
		person.setDbpUri(resource.getURI());
		person.setName(getString(resource, RDFS.label, lang));
		person.setWikiUri(getUriString(resource, FOAF.primaryTopic));
		person.setThumbNail(getUriString(resource, ontology.createProperty("http://dbpedia.org/ontology/thumbnail")));
		person.setAbstract2(getString(resource, ontology.createProperty(DBPEDIA.ABSTRACT), lang));
		person.setDateOfBirth(getDate(resource, ontology.createProperty("http://dbpedia.org/ontology/birthDate")));
		return person;
	}

	public Movie movieFromResource(Resource resource)
	{
		Movie movie = new Movie();
		String imdbId = getString(resource, this.imdbID);
		if (imdbId == null)
			throw new WebApplicationException(404);
		movie.setImdbID(imdbId);
		movie.setTitle(getString(resource, title));
		movie.setPlot(getString(resource, plot));
		movie.setPoster(URI.create(getString(resource, poster)));
		movie.setImdbVotes(getInt(resource, imdbVotes));
		movie.setImdbRating(getDouble(resource, imdbRating));
		movie.setReleased(getLong(resource, released));
		movie.setRuntime(getInt(resource, runtime));
		movie.setDirector(getString(resource, director));
		movie.setWriter(getString(resource, writer));
		movie.setActors(getString(resource, actors));
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
