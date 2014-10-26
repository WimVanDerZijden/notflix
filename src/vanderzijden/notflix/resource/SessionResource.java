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
	@Path("login")	//	session/login
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Session createSession(
			@FormParam("username") String username,
			@FormParam("password") String password)
	{
		return getModel().createSession(username, password);
	}
	/*
	@GET
	@Path("validate")	//	session/validate
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Session validateSession()
	{
		return getSession();
	}
	*/
}
