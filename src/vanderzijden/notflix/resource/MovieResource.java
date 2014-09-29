package vanderzijden.notflix.resource;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import vanderzijden.notflix.application.C;
import vanderzijden.notflix.model.Movie;

@Path(C.path.MOVIES)
public class MovieResource extends BaseResource {

	@GET
	@Path(C.path.IMDB_TT)
	@Produces(MediaType.APPLICATION_JSON) 
	public Movie getMovie(
			@PathParam(C.parameter.IMDB_TT) String imdb_tt) {
		return getModel().getMovie(imdb_tt);
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML})
	public List<Movie> searchMovies(
			@DefaultValue("") @QueryParam(C.parameter.Q) String q) {
		return getModel().searchMovies(q);
	}

}
