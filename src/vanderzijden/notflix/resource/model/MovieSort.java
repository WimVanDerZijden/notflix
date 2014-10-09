package vanderzijden.notflix.resource.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import vanderzijden.notflix.application.Log;
import vanderzijden.notflix.model.Movie;

public class MovieSort {
	
	public enum SortOrder {
		TitleDesc,
		TitleAsc,
		RatingAsc, 
		RatingDesc, 
		VotesAsc, 
		VotesDesc, 
		None
	}
	
	public static void sort(List<Movie> movies, SortOrder sortOrder) {
		switch(sortOrder) {
		case None:
			break;
		case TitleAsc:
			Collections.sort(movies, new TitleSort());
			break;
		case TitleDesc:
			Collections.sort(movies, Collections.reverseOrder(new TitleSort()));
			break;
		default:
			throw new RuntimeException("Unimplemented sort order");
		}
	}
	
	public static List<Movie> getSublist(List<Movie> movies, int begin, int limit) {
		if (movies.size() == 0)
			return movies;
		if (begin < 0 || limit < 1 || begin >= movies.size()) {
			Log.info(MovieSort.class, "Invalid begin and/or limit query param");
			throw new WebApplicationException(400);
		}
		if (begin + limit > movies.size()) {
			limit = movies.size() - begin;
		}
		return movies.subList(begin, begin + limit);
	}
	
	public static class TitleSort implements Comparator<Movie>{

		@Override
		public int compare(Movie movie, Movie movie2) {
			return movie.getTitle().compareTo(movie2.getTitle());
		}

	}
}
