package vanderzijden.notflix.application;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class WebApp extends ResourceConfig {

	public WebApp() {
		System.out.println("Web App started");
		packages("vanderzijden.notflix.resource");
		// Custom Exception Listener for debugging:
		// 	register(ExceptionListener.class);
		// Make sure all classes are loaded
		this.getClasses();
	}

}
