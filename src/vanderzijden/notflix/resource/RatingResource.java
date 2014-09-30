package vanderzijden.notflix.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import vanderzijden.notflix.model.Movie;
import vanderzijden.notflix.model.Rating;
import vanderzijden.notflix.model.User;

@Path("ratings/{imdb_tt: tt\\d+}")
public class RatingResource extends BaseResource {

	@POST
	@Path("create")
	public Rating createRating(
			@PathParam("imdb_tt") String imdb_tt,
			@QueryParam("halfStars") int halfStars) {
		User user = getUser();
		Movie movie = getModel().getMovie(imdb_tt);
		return movie.createRating(user, halfStars);
	}

}
