package vanderzijden.notflix.application;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEvent.Type;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
/**
 * There is a jersey bug that causes some exception to be "eaten".
 * I.e., a server error occurs, but no stacktrace is show in the console.
 * 
 * This class displays such stacktraces.
 * 
 * Code modified from: http://stackoverflow.com/a/22336601/2947592
 * 
 * @author Wim van der Zijden
 *
 */

public class ExceptionListener implements ApplicationEventListener {

	@Override
	public void onEvent(ApplicationEvent event) {

	}

	@Override
	public RequestEventListener onRequest(RequestEvent requestEvent) {
		return new ExceptionRequestEventListener();
	}

	public static class ExceptionRequestEventListener implements RequestEventListener {

		@Override
		public void onEvent(RequestEvent event) {
			if (event.getType() == Type.ON_EXCEPTION) {
				System.out.println("Spotted exception");
				event.getException().printStackTrace();
			}
		}
	}
}