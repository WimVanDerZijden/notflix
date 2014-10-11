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
import vanderzijden.notflix.model.User;
import vanderzijden.notflix.resource.model.MovieSort;
import vanderzijden.notflix.resource.model.MovieSort.SortOrder;
import vanderzijden.notflix.resource.model.SearchResult;
import vanderzijden.notflix.resource.model.UserMovie;

@Path("movie")
public class MovieResource extends BaseResource {

	@GET
	@Path("{imdb_tt: tt\\d+}")	//	base_url/resources/movies/imdb_tt12345
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
	public Movie getMovie(
			@PathParam("imdb_tt") String imdb_tt)
	{
		User user = getUserOpt();
		return new UserMovie(getModel().getMovie(imdb_tt), user);
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
	public SearchResult searchMovies(
			@DefaultValue("") @QueryParam("q") String q,
			@DefaultValue("0") @QueryParam("page") int page,
			@DefaultValue("10") @QueryParam("pageSize") int pageSize,
			@DefaultValue("None") @QueryParam("sort") SortOrder sort)
	{
		User user = getUserOpt();
		List<Movie> movies = getModel().searchMovies(q);
		MovieSort.sort(movies, sort);
		return new SearchResult(movies, user, page, pageSize);
	}

}
