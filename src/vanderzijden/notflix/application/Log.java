package vanderzijden.notflix.application;

import java.util.logging.Logger;

public class Log {

	private static Logger log = Logger.getLogger("vanderzijden.notflix");
	
	public static void info(Object object, String msg) {
		log.info(object.getClass().getSimpleName() + ": " + msg);
	}
	
	public static void warning(Object object, String msg) {
		log.warning(object.getClass().getSimpleName() + ": " + msg);
	}

}
