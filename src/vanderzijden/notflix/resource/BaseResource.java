package vanderzijden.notflix.resource;

import javax.servlet.ServletContext;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import vanderzijden.notflix.application.C;
import vanderzijden.notflix.model.Model;
import vanderzijden.notflix.model.Session;
import vanderzijden.notflix.model.User;

public abstract class BaseResource {

	@Context
	private ServletContext ctx;
	
	@QueryParam("token")
	private String token;
	
	private Session session;
	
	protected Model getModel() {
		Model model = (Model) ctx.getAttribute(C.parameter.MODEL);
		if (model == null) {
			model = new Model();
			model.loadTestData();
			ctx.setAttribute(C.parameter.MODEL, model);
		}
		return model;
	}
	
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

}
