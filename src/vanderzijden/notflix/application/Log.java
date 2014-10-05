package vanderzijden.notflix.application;

import java.util.logging.Logger;

public class Log {

	private static Logger log = Logger.getLogger("vanderzijden.notflix");
	
	public static void info(Object object, String msg) {
		info(object.getClass(), msg);
	}

	public static void info(Class<?> clazz, String msg) {
		log.info(clazz.getSimpleName() + ": " + msg);
	}
	
	public static void warning(Object object, String msg) {
		warning(object.getClass(), msg);
	}
	
	public static void warning(Class<?> clazz, String msg) {
		log.warning(clazz.getSimpleName() + ": " + msg);
	}

	public static void severe(Object object, String msg) {
		severe(object.getClass(), msg);
	}
	
	public static void severe(Class<?> clazz, String msg) {
		log.severe(clazz.getSimpleName() + ": " + msg);
	}

	
}
