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

@Path("movies")
public class MovieResource extends BaseResource {

	@GET
	@Path("{imdb_tt: tt\\d+}")	//	movies/imdb_tt12345
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON}) 
	public Movie getMovie(
			@PathParam("imdb_tt") String imdb_tt) {
		return getModel().getMovie(imdb_tt);
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public List<Movie> searchMovies(
			@DefaultValue("") @QueryParam("q") String q) {
		return getModel().searchMovies(q);
	}

}
