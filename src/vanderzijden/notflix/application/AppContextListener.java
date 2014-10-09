package vanderzijden.notflix.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.json.JSONArray;
import org.json.JSONObject;

import vanderzijden.notflix.model.Model;
import vanderzijden.notflix.model.Movie;
import vanderzijden.notflix.model.User;

import com.google.gson.Gson;

/**
 * Loads the model on init as json.
 * Saves the model on destroy as json.
 * 
 * Save location is the WEB-INF folder of the deployed web server.
 * 
 * @author Wim van der Zijden
 * 
 * Mostly copied from WebTechnologie Opdracht 1: eenvoudige kamerverhuur applicatie
 *
 */
@WebListener
public class AppContextListener implements ServletContextListener {

	private static final String MODEL_SAVE_FILE = "model_save_file.json";
	private static final String TOP_250_JSON_FILE = "top250.json";
	
	private ServletContext ctx;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ctx = event.getServletContext();
		Scanner scanner = null;
		Model model = null;
		try {
			File file = new File(getModelPath(ctx)); 
			// Simply use as delimiter a string that will 'never' occur in the source file
			scanner = new Scanner(file).useDelimiter("!@#endoffile!@#");
			String in = scanner.next();
			Log.info(this,"Loading model saved as json.");
			model = new Gson().fromJson(in, Model.class);
		} catch (FileNotFoundException e) {
			Log.warning(this,"Unable to load model from: " + getModelPath(ctx));
		} finally {
			if (scanner != null)
				scanner.close();
		}
		// Failed to load model: create new model
		if (model == null) {
			model = new Model();
			loadTestData(model);
		}
		ctx.setAttribute("model", model);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext ctx = event.getServletContext();
		Gson gson = new Gson();
		Model model = (Model) ctx.getAttribute("model");
		model.clearSessions();
		String json = gson.toJson(model);
		Log.info(this, "Saving model as json.");
		try {
			PrintWriter pw = new PrintWriter(getModelPath(ctx));
			pw.print(json);
			pw.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not save model to: " + getModelPath(ctx));
			e.printStackTrace();
		}
	}
	
	private String getModelPath(ServletContext ctx)
	{
		return ctx.getRealPath("/WEB-INF") + "/" + MODEL_SAVE_FILE;
	}
	
	private static String getMoviesPath(ServletContext ctx)
	{
		return ctx.getRealPath("/WEB-INF") + "/" + TOP_250_JSON_FILE;
	}
	
	private void loadTestData(Model model) {
		Scanner scanner = null;
		try {
			File file = new File(getMoviesPath(ctx));
			// Simply use as delimiter a string that will 'never' occur in the source file
			scanner = new Scanner(file).useDelimiter("!@#endoffile!@#");
			String in = scanner.next();
			JSONArray jsonMovies = new JSONArray(in);
			Log.info(this, "First time loading " + jsonMovies.length() + " movies from " + TOP_250_JSON_FILE);
			for (int n = 0; n < jsonMovies.length(); n++) {
				JSONObject jsonMovie = jsonMovies.getJSONObject(n);
				model.addMovie(Movie.parseFromOmdbApi(jsonMovie));
			}
		} catch (FileNotFoundException e) {
			Log.severe(AppContextListener.class, "File not found: " + getMoviesPath(ctx));
			e.printStackTrace();
		} finally {
			if (scanner != null)
				scanner.close();
		}
		model.addUser(new User("wim", "Wim","van der", "Zijden","wim"));
		model.addUser(new User("pim", "Pim",null, "Teunissen","pim"));
	}

}
