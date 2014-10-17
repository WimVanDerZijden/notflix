package vanderzijden.notflix.resource.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vanderzijden.notflix.model.Movie;

public class MovieSort {
	
	public enum SortOrder {
		TitleDesc,
		TitleAsc,
		ImdbRatingAsc,
		ImdbRatingDesc,
		ImdbVotesAsc,
		ImdbVotesDesc,
		ReleasedAsc,
		ReleasedDesc,
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
		case ReleasedAsc:
			Collections.sort(movies, new ReleasedSort());
			break;
		case ReleasedDesc:
			Collections.sort(movies, Collections.reverseOrder(new ReleasedSort()));
			break;
		case ImdbRatingAsc:
			Collections.sort(movies, new ImdbRatingSort());
			break;
		case ImdbRatingDesc:
			Collections.sort(movies, Collections.reverseOrder(new ImdbRatingSort()));
			break;
		case ImdbVotesAsc:
			Collections.sort(movies, new ImdbVotesSort());
			break;
		case ImdbVotesDesc:
			Collections.sort(movies, Collections.reverseOrder(new ImdbVotesSort()));
			break;
		default:
			throw new RuntimeException("Unimplemented sort order");
		}
	}
	
	private static class TitleSort implements Comparator<Movie>{

		@Override
		public int compare(Movie movie, Movie movie2) {
			return movie.getPlainTitle().compareTo(movie2.getPlainTitle());
		}
	}
	
	private static class ReleasedSort implements Comparator<Movie> {
		
		@Override
		public int compare(Movie movie, Movie movie2) {
			return movie.getReleased().compareTo(movie2.getReleased());
		}
	}
	
	private static class ImdbRatingSort implements Comparator<Movie> {
		
		@Override
		public int compare(Movie movie, Movie movie2) {
			return movie.getImdbRating().compareTo(movie2.getImdbRating());
		}
	}
	
	private static class ImdbVotesSort implements Comparator<Movie> {
		@Override
		public int compare(Movie movie, Movie movie2) {
			return movie.getImdbVotes() - movie2.getImdbVotes();
		}
	}
}
