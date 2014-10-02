package vanderzijden.notflix.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import vanderzijden.notflix.model.Model;

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
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext ctx = event.getServletContext();
		Scanner scanner = null;
		try {
			File file = new File(getModelPath(ctx)); 
			// Simply use as delimiter a string that will 'never' occur in the source file
			scanner = new Scanner(file).useDelimiter("!@#endoffile!@#");
			String in = scanner.next();
			// Just for debugging. Remove for production release because it contains passwords as well.
			System.out.println("AppContextListener.contextInitialized: Loading model saved as json: " + in);
			Model model = new Gson().fromJson(in, Model.class);
			if (model == null) {
				model = new Model();
				model.loadTestData();
			}
			ctx.setAttribute("model", model);
		} catch (FileNotFoundException e) {
			Log.warning(this,"Unable to load model from: " + getModelPath(ctx));
		} finally {
			scanner.close();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext ctx = event.getServletContext();
		Gson gson = new Gson();
		String json = gson.toJson(ctx.getAttribute("model"));
		// Just for debugging. Remove for production release because it contains passwords as well.
		System.out.println("AppContextListener.contextDestroyed: Saving model as json: " + json);
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

}
