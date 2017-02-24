/**
 * 
 */
package user.dao;

import java.sql.Timestamp;
import java.util.List;

import tool.Store;
import user.User;

/**
 *
 */
public class UserImplStore implements Store<User> {

	@Override
	public User format(List<Object> list) throws Exception {
		// TODO Auto-generated method stub
		User user = new User();
		try {
			if (list.size() != User.getMemberCount())
				throw new Exception("Cannot transform result set from database to User object!");
			else {
				user.setUserId((String)list.get(0));
				user.setUsername((String)list.get(1));
				user.setAvatar((String)list.get(2));
				user.setCreateTimestamp((Timestamp)list.get(3));
				user.setReloadTimesMax((int)list.get(4));
				user.setLoadTimeout((int)list.get(5));
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		return user;
	}

}
