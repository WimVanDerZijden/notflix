package vanderzijden.notflix.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import vanderzijden.notflix.model.User;

@Path("user")
public class UserResource extends BaseResource {

	@POST
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response createUser(
			@FormParam("username") String username,
			@FormParam("firstName") String firstName,
			@FormParam("namePrepositions") String namePrepositions,
			@FormParam("lastName") String lastName,
			@FormParam("password") String password)
	{
		User user = getModel().createUser(username, firstName, namePrepositions, lastName, password);
		URI uri = UriBuilder.fromResource(UserResource.class).path(username).build();
		return Response.created(uri).entity(user).build();
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
	public List<User> getUsers()
	{
		return getModel().getUsers();
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("{username}")
	public User getUser(
			@PathParam("username") String username)
	{
		return getModel().getUser(username);
	}
	
}
