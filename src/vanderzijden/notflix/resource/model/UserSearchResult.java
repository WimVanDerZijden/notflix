package vanderzijden.notflix.resource.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import vanderzijden.notflix.application.Log;
import vanderzijden.notflix.model.User;

public class UserSearchResult {

	private int size;
	
	private ArrayList<User> users = new ArrayList<>();
	
	public UserSearchResult(List<User> all_users, int page, int pageSize) {
		List<User> usersPage = getSubList(all_users, page, pageSize);
		for (User user : usersPage) {
			users.add(user);
		}
		size = all_users.size();
	}
	
	public static List<User> getSubList(List<User> users, int page, int pageSize) {
		if (users.size() == 0)
			return users;
		// Catch illegal parameters...
		if (page < 0 || pageSize < 1 || pageSize > 100) {
			Log.info(UserSearchResult.class, "Invalid begin and/or limit query param");
			throw new WebApplicationException(400);
		}
		int begin = page * pageSize;
		int end = begin + pageSize;
		// ...but be forgiving when they are out of range
		if (begin > users.size()) {
			begin = users.size();
		}
		if (end > users.size()) {
			end = users.size();
		}
		return users.subList(begin, end);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setMovies(ArrayList<User> users) {
		this.users = users;
	}
	
}
