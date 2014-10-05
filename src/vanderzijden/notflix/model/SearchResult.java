package vanderzijden.notflix.model;

public class SearchResult<T> {
	
	/** Levenshtein Distance */
	private int distance;
	private T value;
	
	public int getDistance() {
		return distance;
	}
	
	public T getValue() {
		return value;
	}
	
	public SearchResult(int distance, T value) {
		this.distance = distance;
		this.value = value;
	}
	
}
