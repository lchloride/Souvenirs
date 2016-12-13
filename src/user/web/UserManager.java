package user.web;

import java.util.*;

import user.dao.UserDAO;

public class UserManager {
	private static UserManager user_manager = new UserManager();
	private Map<String, String> parameter = null;
	private static UserDAO dao = new UserDAO();

	final int REGISTER_DEFAULT_PARA = 2;//login_username,login_password
	final int LOGIN_DEFAULT_PARA = 2;//login_username, login_password
	
	public UserManager() {
		// TODO Auto-generated constructor stub
		// dao = new UserDAO();
	}

	public void setParameter(Map<String, String> parameter) {
		this.parameter = parameter;
	}

	public static UserManager getInstance() {
		return user_manager;
	}

	public Map<String, Object> register() {
		Map<String, Object> result = new HashMap<>();
		// User has already login the system, he/she cannot register
		if (checkLogin(parameter.get("login_username"), parameter.get("login_password"))) {
			result.put("DispatchURL", "register.jsp");
			result.put("Result", true);
			result.put("Msg", "Aleady login");
		} else {
			if (parameter.size() > REGISTER_DEFAULT_PARA) {
				// Check whether username is duplicated or not
				if (checkUsername(parameter.get("Text_username"))) {
					result.put("DispatchURL", "register.jsp");
					result.put("Result", false);
					result.put("Msg", "Duplicate Username");
				} else {
					// Key operation
					int rs = dao.register(parameter);
					if (rs == 0) {
						result.put("DispatchURL", "register.jsp"); // Insert
																	// successfully
						result.put("Result", true);
					} else {
						result.put("DispatchURL", "register.jsp");// Insert
																	// failed
						result.put("Result", false);
						result.put("Msg", "Database Error occured, please try again");
					}
				}
			} else { // The first time to display register page
				result.put("DispatchURL", "register.jsp");
			}
		}
		return result;
	}

	public Map<String, Object> login() {
		Map<String, Object> result = new HashMap<>();
		if (!parameter.get("Text_username").isEmpty() || !parameter.get("Text_password").isEmpty()) { 
			// Fill the login form and click submit, just check what the user wrote
			if (/*checkLogin(parameter.get("login_username"), parameter.get("login_password"))
					||*/ checkLogin(parameter.get("Text_username"), parameter.get("Text_password"))) {
				result.put("DispatchURL", "homepage");
				result.put("Redirect", true);
				result.put("login_username", parameter.get("Text_username"));
				result.put("login_password", parameter.get("Text_password"));
			} else {
				result.put("DispatchURL", "index.jsp");
				result.put("Msg", "Username(ID) and/or password is wrong");
				result.put("Firsttime", false);
			}
		} else {
			//the first time load index page, check whether username and password stored in session
			if (checkLogin(parameter.get("login_username"), parameter.get("login_password"))) {
				result.put("DispatchURL", "homepage");
				result.put("Redirect", true);
			} else {
				result.put("DispatchURL", "index.jsp");
				result.put("Firsttime", false);
			}
		}
		return result;
	}

	public static boolean checkLogin(String username, String password) {
		Map<String, String> para = new HashMap<>();
		para.put("Text_username", username);
		para.put("Text_password", password);
		List<Object> query_result = dao.getLogin(para);
		if ((long) query_result.get(0) == 1)
			return true;
		else
			return false;
	}

	public static boolean checkLogin(Object username, Object password) {
		Map<String, String> para = new HashMap<>();
		if (username == null || password == null)
			return false;
		else {
			para.put("Text_username", (String) username);
			para.put("Text_password", (String) password);
			List<Object> query_result = dao.getLogin(para);
			if ((long) query_result.get(0) == 1)
				return true;
			else
				return false;
		}
	}

	public static boolean checkUsername(String username) {
		Map<String, String> para = new HashMap<>();
		para.put("Text_username", username);
		long rs = (long) dao.checkUsername(para).get(0);
		System.out.println(rs);
		if (rs == 0)
			return false;
		else if (rs == 1)
			return true;
		else {
			System.out.println("Fatal Error: duplicate user!");
			return true;
		}
	}
}