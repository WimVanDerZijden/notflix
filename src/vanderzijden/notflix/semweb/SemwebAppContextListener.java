package vanderzijden.notflix.semweb;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.json.JSONArray;
import org.json.JSONObject;

import vanderzijden.notflix.application.Log;
import vanderzijden.notflix.model.Movie;
import vanderzijden.notflix.model.NotflixModel;
import vanderzijden.notflix.model.User;

/**
 * Loads the model on init as json. Saves the model on destroy as json.
 * 
 * Save location is the WEB-INF folder of the deployed web server.
 * 
 * @author Wim van der Zijden
 * 
 *         Mostly copied from WebTechnologie Opdracht 1: eenvoudige kamerverhuur
 *         applicatie
 *
 */
@WebListener
public class SemwebAppContextListener implements ServletContextListener {

	/**
	 * Set to true to reload the model (loosing all user data). As long as this
	 * property is true, all user data will be lost on a server restart
	 * 
	 */
	private static final boolean RELOAD_MODEL = false;

	private static final String MODEL_SAVE_FILE = "model_save_file.xml";

	private static final String MODEL_ONTOLOGY = "notflix_ontology.xml";
	/**
	 * Requested 729,513 movies via omdb-api. Added: 88,951 Not found: 142,183
	 * (main cause: foreign title movies that are in OMDB-API under English
	 * name) No poster: 497,700 Other: 166
	 */
	private static final String MOVIES_JSON = "movies-top5000.json"; // "movies-top3.json";

	private ServletContext ctx;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ctx = event.getServletContext();
		Scanner scanner = null;
		NotflixModel model = null;

		try {
			File file = new File(getModelPath(ctx));
			// Simply use as delimiter a string that will 'never' occur in the
			// source file
			scanner = new Scanner(file).useDelimiter("!@#endoffile!@#");
			String in = scanner.next();
			Log.info(this, "Loading model saved as rdf triples.");
			model = new RDFNotflixModel(getOntologyPath(ctx), getModelPath(ctx));
		} catch (FileNotFoundException e) {
			Log.warning(this, "Unable to load model from: " + getModelPath(ctx));
		} finally {
			if (scanner != null)
				scanner.close();
		}
		// Failed to load model: create new model // TODO load RDFNotflixModel
		// instead
		if (model == null || RELOAD_MODEL) {
			model = new RDFNotflixModel(getOntologyPath(ctx));
			loadTestData(model);
		}
		ctx.setAttribute("model", model);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext ctx = event.getServletContext();
		NotflixModel model = (NotflixModel) ctx.getAttribute("model");
		if (model != null)
			model.save(getModelPath(ctx));
	}

	private String getModelPath(ServletContext ctx) {
		return ctx.getRealPath("/WEB-INF") + "/" + MODEL_SAVE_FILE;
	}

	private static String getMoviesPath(ServletContext ctx) {
		return ctx.getRealPath("/WEB-INF") + "/" + MOVIES_JSON;
	}

	private static String getOntologyPath(ServletContext ctx) {
		return ctx.getRealPath("/WEB-INF") + "/" + MODEL_ONTOLOGY;
	}

	private void loadTestData(final NotflixModel model) {
		Scanner scanner = null;
		try {
			File file = new File(getMoviesPath(ctx));
			// Simply use as delimiter a string that will 'never' occur
			// in the source file
			scanner = new Scanner(file).useDelimiter("!@#endoffile!@#");
			String in = scanner.next();
			JSONArray jsonMovies = new JSONArray(in);
			Log.info(this, "First time loading " + jsonMovies.length()
					+ " movies from " + MOVIES_JSON);
			for (int n = 0; n < jsonMovies.length(); n++) {
				JSONObject jsonMovie = jsonMovies.getJSONObject(n);
				model.addMovie(Movie.parseFromOmdbApi(jsonMovie));
			}
			model.save(getModelPath(ctx));
		} catch (FileNotFoundException e) {
			Log.severe(SemwebAppContextListener.class, "File not found: "
					+ getMoviesPath(ctx));
			e.printStackTrace();
		} finally {
			if (scanner != null)
				scanner.close();
		}
		model.addUser(new User("wim", "Wim", "van der", "Zijden", "wim"));
		model.addUser(new User("pim", "Pim", null, "Teunissen", "pim"));
	}

}
