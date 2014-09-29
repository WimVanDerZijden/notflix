package vanderzijden.notflix.application;

public class SearchString {

	private String value;
	
	public SearchString(String q) {
		value = q.toUpperCase();
	}

	@Override
	public String toString() {
		return value;
	}
}
