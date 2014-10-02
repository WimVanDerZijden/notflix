package vanderzijden.notflix.resource;

import javax.servlet.ServletContext;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import vanderzijden.notflix.model.Model;
import vanderzijden.notflix.model.Session;
import vanderzijden.notflix.model.User;

/**
 * Base Resource to take care of default stuff 
 * that most resources need to do.
 * 
 * - Get model from context
 * - Validating token
 * 
 * @author Wim van der Zijden
 *
 */
public abstract class BaseResource {

	@Context
	private ServletContext ctx;
	
	@QueryParam("token")
	private String token;
	
	private Session session;
	
	/**
	 * Get Model from context.
	 * Load if necessary.
	 * 
	 * @return
	 */
	protected Model getModel() {
		Model model = (Model) ctx.getAttribute("model");
		if (model == null) {
			model = new Model();
			model.loadTestData();
			ctx.setAttribute("model", model);
		}
		return model;
	}
	
	/**
	 * Get the session.
	 * Throws a 401 Unauthorized if no session
	 * 
	 * @return
	 */
	protected Session getSession() {
		if (session == null) {
			session = getModel().getSession(token);
		}
		return session;
	}
	
	/**
	 * Get the authorized user.
	 * Throws a 401 Unauthorized if no authorized user
	 * 
	 * @return
	 */
	protected User getUser() {
		return getSession().getUser();
	}
	
	/**
	 * Get the user, but only if a token was provided.
	 * 
	 * Returns null if no token was provided.
	 * Throws a 401 Unauthorized if a false token was provided
	 * 
	 * @return
	 */
	
	protected User getUserOpt() {
		if (token != null) {
			return getSession().getUser();
		}
		return null;
	}
}
