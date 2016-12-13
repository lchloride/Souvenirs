package user.dao;

import java.util.*;

import tool.DB;

public class UserDAO {
	public List<Object> getLogin(Map<String, String> para) {
		String sql = "select count(*) from user where username = '";
		sql  += para.get("Text_username");
		sql += "' and password = '";
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
}
