package vanderzijden.notflix.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import vanderzijden.notflix.application.C;

@Path(C.MOVIES)
public class MovieResource extends BaseResource {

	@GET
	@Produces(MediaType.TEXT_PLAIN) 
	public String getMovie() {
		return "Hi";
	}

}
