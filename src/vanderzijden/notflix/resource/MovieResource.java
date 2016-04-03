package vanderzijden.notflix.resource;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import vanderzijden.notflix.model.Movie;
import vanderzijden.notflix.model.User;
import vanderzijden.notflix.resource.model.MovieSort;
import vanderzijden.notflix.resource.model.MovieSort.SortOrder;
import vanderzijden.notflix.resource.model.SearchResult;
import vanderzijden.notflix.resource.model.UserMovie;

@Path("movie")
public class MovieResource extends BaseResource {

	@Context
	UriInfo ui;
	
	@GET
	@Path("{imdb_tt: tt\\d+}")	//	base_url/resources/movies/imdb_tt12345
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
	public Movie getMovie(
			@PathParam("imdb_tt") String imdb_tt,
			@DefaultValue("en") @QueryParam("lang") String lang)
	{
		User user = getUserOpt();
		return new UserMovie(getModel().getMovie(imdb_tt, lang), user);
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
		SearchResult searchResult = new SearchResult(movies, user, page, pageSize);
		searchResult.addLink(getLink("prev", q, sort.toString(), page - 1, pageSize, searchResult.getSize()));
		searchResult.addLink(getLink("next", q, sort.toString(), page + 1, pageSize, searchResult.getSize()));
		return searchResult;
	}

	private Link getLink(String rel, String q, String sort, Integer page, Integer pageSize, int size) {
		// No link if page is out of scope
		if (page < 0 || page > (size - 1) / pageSize) {
			return null;
		}
		return Link.fromUri(ui.getAbsolutePath())
				.param("q",q)
				.param("sort", sort)
				.param("page", "" + page)
				.param("pageSize", "" + pageSize)
				.rel(rel)
				.build();
	}
	
}
