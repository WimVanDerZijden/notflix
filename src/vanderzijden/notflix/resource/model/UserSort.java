package vanderzijden.notflix.resource.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vanderzijden.notflix.model.User;

public class UserSort {
	
	public enum SortOrder {
		NameDesc,
		NameAsc,
		None
	}
	
	public static void sort(List<User> users, SortOrder sortOrder) {
		switch(sortOrder) {
		case None:
			break;
		case NameAsc:
			Collections.sort(users, new NameSort());
			break;
		case NameDesc:
			Collections.sort(users, Collections.reverseOrder(new NameSort()));
			break;
		default:
			throw new RuntimeException("Unimplemented sort order");
		}
	}
	
	private static class NameSort implements Comparator<User>{

		@Override
		public int compare(User user, User user2) {
			return user.getLastName().compareTo(user2.getLastName());
		}
	}
}
