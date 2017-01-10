package user.dao;

import java.util.*;

import org.apache.log4j.Logger;

import tool.DB;

public class UserDAO {
	private Logger logger = Logger.getLogger(UserDAO.class);
	private static UserDAO user_dao = new UserDAO();
	
	public static UserDAO getInstance() {
		return user_dao;
	}
	
	public List<Object> getLogin(Map<String, String> para) {
		List<String>parameter = new ArrayList<>();
		String sql = "select count(*) from user where (username = ?";
		parameter.add(para.get("Text_username"));
		if (para.containsKey("Text_user_id")) {
			sql += " and user_id = ?";
			parameter.add(para.get("Text_user_id"));
		}
		sql += ") and password = ?";
		parameter.add(para.get("Text_password"));
		return DB.execSQLQuery(sql, parameter).get(0);
	}

	public int register(Map<String, String> para) {
		String sql = "call AddUserWithoutAvatar(?, ?)";
		List<String> parameter = Arrays.asList(para.get("Text_username"), para.get("Text_password"));
		return (int) DB.execSQLQuery(sql, parameter).get(0).get(0);
	}
	
	public List<Object> checkUsername(Map<String, String> para) {
		String sql = "select count(*) from user where username = ?";
		List<String> parameter = Arrays.asList(para.get("Text_username"));
		return DB.execSQLQuery(sql, parameter).get(0);
	}
	
	public List<Object> getUserIDByName(String username) throws Exception {
		String sql = "select user_id from user where username = ?";
		List<String> parameter = Arrays.asList(username);
		List<List<Object>> result = DB.execSQLQuery(sql, parameter);
		if (result.size() > 1) {
			logger.error("Duplicate Users:Username <"+username+">");
			throw new Exception("Duplicate Users");
		} else {
			return result.get(0);
		}
	}
}
