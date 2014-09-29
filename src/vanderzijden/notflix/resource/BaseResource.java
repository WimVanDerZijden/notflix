package vanderzijden.notflix.resource;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import vanderzijden.notflix.application.C;
import vanderzijden.notflix.model.Model;

public abstract class BaseResource {

	@Context
	ServletContext ctx;
	
	protected Model getModel() {
		Model model = (Model) ctx.getAttribute(C.parameter.MODEL);
		if (model == null) {
			model = new Model();
			model.loadTestData();
		}
		return model;
	}

}
