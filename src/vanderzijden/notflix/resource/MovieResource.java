package vanderzijden.notflix.resource;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import vanderzijden.notflix.model.Movie;
import vanderzijden.notflix.model.SearchMovie;
import vanderzijden.notflix.model.User;

@Path("movie")
public class MovieResource extends BaseResource {

	@GET
	@Path("{imdb_tt: tt\\d+}")	//	base_url/resources/movies/imdb_tt12345
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
	public Movie getMovie(
			@PathParam("imdb_tt") String imdb_tt)
	{
		return getModel().getMovie(imdb_tt);
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
	public List<SearchMovie> searchMovies(
			@DefaultValue("") @QueryParam("q") String q,
			@DefaultValue("10") @QueryParam("limit") int limit)
	{
		User user = getUserOpt();
		return getModel().searchMovies(q, limit, user);
	}

}
