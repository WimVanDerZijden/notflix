package vanderzijden.notflix.application;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("resources")
public class WebApp extends ResourceConfig {

	public WebApp() {
		Log.info(this,"Web App started");
		packages("vanderzijden.notflix.resource");
		// Make sure all classes are loaded
		this.getClasses();		
		// For custom Exception Listener for debugging uncomment next line
		//register(ExceptionListener.class);


	}

}
