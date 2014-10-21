package vanderzijden.notflix.resource.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlRootElement;

import vanderzijden.notflix.application.Log;
import vanderzijden.notflix.model.Movie;
import vanderzijden.notflix.model.User;

@XmlRootElement
public class SearchResult {

	public SearchResult() {}
	
	private int size;
	
	private ArrayList<UserMovie> movies = new ArrayList<>();
	
	private ArrayList<Link> link = new ArrayList<Link>();
	
	public SearchResult(List<Movie> all_movies, User user, int page, int pageSize) {
		List<Movie> moviesPage = getSubList(all_movies, page, pageSize);
		for (Movie movie : moviesPage) {
			movies.add(new UserMovie(movie, user));
		}
		size = all_movies.size();
	}
	
	public static List<Movie> getSubList(List<Movie> movies, int page, int pageSize) {
		if (movies.size() == 0)
			return movies;
		// Catch illegal parameters...
		if (page < 0 || pageSize < 1 || pageSize > 100) {
			Log.info(MovieSort.class, "Invalid begin and/or limit query param");
			throw new WebApplicationException(400);
		}
		int begin = page * pageSize;
		int end = begin + pageSize;
		// ...but be forgiving when they are out of range
		if (begin > movies.size()) {
			begin = movies.size();
		}
		if (end > movies.size()) {
			end = movies.size();
		}
		return movies.subList(begin, end);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public ArrayList<UserMovie> getMovies() {
		return movies;
	}

	public void setMovies(ArrayList<UserMovie> movies) {
		this.movies = movies;
	}

	public void addLink(Link link) {
		if (link != null) {
			this.link.add(link);
		}
	}

	public ArrayList<Link> getLink() {
		return link;
	}

	public void setLink(ArrayList<Link> link) {
		this.link = link;
	}
	
	
	
}
