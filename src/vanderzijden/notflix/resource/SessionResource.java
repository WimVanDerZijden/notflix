package vanderzijden.notflix.resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import vanderzijden.notflix.application.C;
import vanderzijden.notflix.model.Session;

@Path(C.path.SESSIONS)
public class SessionResource extends BaseResource {

	@POST
	@Path(C.path.LOGIN)
	@Produces(MediaType.APPLICATION_XML)
	public Session createSession(
			@FormParam(C.parameter.USERNAME) String username,
			@FormParam(C.parameter.PASSWORD) String password) {
		Session session = getModel().createSession(username, password);
		return session;
	}

}
