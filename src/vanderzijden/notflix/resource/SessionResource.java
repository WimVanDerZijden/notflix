package vanderzijden.notflix.resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import vanderzijden.notflix.model.Session;

@Path("session")
public class SessionResource extends BaseResource {

	@POST
	@Path("login")	//	sessions/login
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Session createSession(
			@FormParam("username") String username,
			@FormParam("password") String password)
	{
		return getModel().createSession(username, password);
	}

}
