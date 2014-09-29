package vanderzijden.notflix.application;

/**
 * Constants organized in a similar way
 * as Android organizes id's in the R-class
 * 
 * @author Wim van der Zijden
 *
 */

public class C {

	public static class parameter {
		public static final String MODEL = "model";
		public static final String IMDB_TT = "imdb_tt"; 
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
		public static final String Q = "q";
	}
	
	public static class path {
		public static final String IMDB_TT = "{imdb_tt: tt\\d+}";
		public static final String MOVIES = "movies";
		public static final String SESSIONS = "sessions";
		public static final String LOGIN = "login";
	}
}
