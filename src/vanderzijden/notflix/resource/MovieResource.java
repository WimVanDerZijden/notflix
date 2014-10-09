package vanderzijden.notflix.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import vanderzijden.notflix.application.WebApp;
import vanderzijden.notflix.model.Movie;
import vanderzijden.notflix.model.User;
import vanderzijden.notflix.resource.model.MovieSort;
import vanderzijden.notflix.resource.model.MovieSort.SortOrder;
import vanderzijden.notflix.resource.model.UserMovie;

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
	public Response searchMovies(
			@DefaultValue("") @QueryParam("q") String q,
			@DefaultValue("0") @QueryParam("begin") int begin,
			@DefaultValue("10") @QueryParam("limit") int limit,
			@DefaultValue("None") @QueryParam("sort") SortOrder sort)
	{
		User user = getUserOpt();
		List<Movie> movies = getModel().searchMovies(q);
		MovieSort.sort(movies, sort);
		List<Movie> moviesSublist = MovieSort.getSublist(movies, begin, limit);
		URI next = UriBuilder.fromResource(MovieResource.class)
				.queryParam("q", q)
				.queryParam("begin", begin + limit)
				.queryParam("limit", limit)
				.queryParam("sort", sort)
				.build();
		return Response.ok(UserMovie.get(moviesSublist, user))
				.link(next, "next").build();
	}

}
