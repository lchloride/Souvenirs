package user.dao;

import java.util.*;

import org.apache.log4j.Logger;

import tool.DB;

public class UserDAO {
	private Logger logger = Logger.getLogger(UserDAO.class);
	
	public List<Object> getLogin(Map<String, String> para) {
		String sql = "select count(*) from user where (username = '";
		sql  += para.get("Text_username");
		if (para.containsKey("Text_user_id")) {
			sql += "' and user_id = '";
			sql += para.get("Text_user_id");
		}
		sql += "') and password = '";
		sql += para.get("Text_password");
		sql += "'";
		System.out.println(sql);
		return DB.execSQLQuery(sql).get(0);
	}

	public int register(Map<String, String> para) {
		String sql = "call AddUserWithoutAvatar('"+para.get("Text_username")+"', '"+para.get("Text_password")+"')";
		System.out.println(sql);
		return (int) DB.execSQLQuery(sql).get(0).get(0);
	}
	
	public List<Object> checkUsername(Map<String, String> para) {
		String sql = "select count(*) from user where username = '"+para.get("Text_username")+"'";
		return DB.execSQLQuery(sql).get(0);
	}
	
	public List<Object> getUserIDByName(String username) throws Exception {
		String sql = "select user_id from user where username = '"+username+"'";
		List<List<Object>> result = DB.execSQLQuery(sql);
		if (result.size() > 1) {
			logger.error("Duplicate Users:Username <"+username+">");
			throw new Exception("Duplicate Users");
		} else {
			return result.get(0);
		}
	}
}
