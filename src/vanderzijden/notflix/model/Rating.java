package vanderzijden.notflix.model;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlRootElement;

import vanderzijden.notflix.application.Log;

/**
 * Ratings are stored in a list on the Movie object.
 * Therefore, there is no need for a Movie attribute.
 * 
 * @author Wim van der Zijden
 *
 */
@XmlRootElement
public class Rating {

	/** A rating from 1 to 10, displayed as min. 0.5 and max. 5 stars */
	private int halfStars;
	/** The user that issued the rating */
	private User user;
	
	public Rating() {}

	public Rating(User user, int halfStars) {
		setHalfStars(halfStars);
		this.user = user;
	}

	public void setHalfStars(int halfStars) {
		if (halfStars < 1 || halfStars > 10) {
			Log.info(this, "Invalid value for halfStars");
			throw new WebApplicationException("Invalid value for halfStars",400);
		}
		this.halfStars = halfStars;
	}
	
	public int getHalfStars() {
		return halfStars;
	}

	protected User getUser() {
		return user;
	}

}
