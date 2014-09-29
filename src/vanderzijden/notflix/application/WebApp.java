package vanderzijden.notflix.application;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class WebApp extends ResourceConfig {

	public WebApp() {
		System.out.println("test");
		packages("vanderzijden.notflix.resource");
	}

}
