package vanderzijden.notflix.resource;

import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import vanderzijden.notflix.model.Movie;
import vanderzijden.notflix.model.Rating;
import vanderzijden.notflix.model.User;

@Path("rating")
public class RatingResource extends BaseResource {

	@Path("{imdb_tt: tt\\d+}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
	@POST
	public Response createRating(
			@PathParam("imdb_tt") String imdb_tt,
			@FormParam("halfStars") int halfStars)
	{
		User user = getUser();
		Movie movie = getModel().getMovie(imdb_tt);
		Rating rating = movie.createRating(user, halfStars);
		// Include location of created resource
		URI uri = UriBuilder.fromResource(RatingResource.class).path(imdb_tt).build();
		return Response.created(uri).entity(rating).build();
	}

	@Path("{imdb_tt: tt\\d+}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
	@PUT
	public Rating updateRating(
			@PathParam("imdb_tt") String imdb_tt,
			@FormParam("halfStars") int halfStars)
	{
		User user = getUser();
		Movie movie = getModel().getMovie(imdb_tt);
		return movie.updateRating(user, halfStars);
	}
	
	@Path("{imdb_tt: tt\\d+}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
	@GET
	public Rating getRating(
			@PathParam("imdb_tt") String imdb_tt)
	{
		User user = getUser();
		Movie movie = getModel().getMovie(imdb_tt);
		return movie.getRating(user);
	}
	
	@Path("{imdb_tt: tt\\d+}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
	@DELETE
	public Rating deleteRating(
			@PathParam("imdb_tt") String imdb_tt)
	{
		User user = getUser();
		Movie movie = getModel().getMovie(imdb_tt);
		return movie.deleteRating(user);
	}
}
