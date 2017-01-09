package user.web;

import java.util.*;

import org.apache.log4j.Logger;

import tool.VerifyCode;
import user.dao.UserDAO;

public class UserManager {
	private static UserManager user_manager = new UserManager();
	private static UserDAO dao = null;
	private static Logger logger = Logger.getLogger(UserManager.class);
	final int REGISTER_DEFAULT_PARA = 2;// login_username,login_password
	final int LOGIN_DEFAULT_PARA = 2;// login_username, login_password

	public UserManager() {
		// TODO Auto-generated constructor stub
		 
	}

	public static UserManager getInstance() {
		return user_manager;
	}

	/*
	 * 注册操作
	 * @param parameter 前端传来的参数表，包括用户名/密码
	 */
	public Map<String, Object> register(Map<String, String> parameter) {
		if (dao == null)
			dao = UserDAO.getInstance();
		Map<String, Object> result = new HashMap<>();
		// User has already login the system, he/she cannot register
		if (checkLogin(parameter.get("login_username"), parameter.get("login_password"))) {
			result.put("DispatchURL", "register.jsp");
			result.put("Result", true);
			result.put("Msg", "Already login");
			logger.info("Register failed: <" + parameter.get("login_username") + "> has already login.");
		} else {
			if (parameter.size() > REGISTER_DEFAULT_PARA) {//Large than REGISTER_DEFAULT_PARA means there are valid parameters for creating a new user
				// Check whether username is duplicated or not
				if (checkUsername(parameter.get("Text_username"))) {
					result.put("DispatchURL", "register.jsp");
					result.put("Result", false);
					result.put("Msg", "Duplicate Username");
					logger.info("Register failed: <" + parameter.get("Text_username") + "> existed.");
				} else {
					// Key operation of register
					int rs = dao.register(parameter);
					if (rs == 0) {
						//Register succeeded.
						result.put("DispatchURL", "register.jsp"); // Insert successfully
						result.put("Result", true);
						logger.info("Register succeeded:Username <" + parameter.get("Text_username") + ">");
					} else {
						//Register failed.
						result.put("DispatchURL", "register.jsp");// Insert failed
						result.put("Result", false);
						result.put("Msg", "Database Error occured, please try again");
						logger.info("Register failed: Database internal error");
					}
				}
			} else { // The first time to display register page
				result.put("DispatchURL", "register.jsp");
			}
		}
		return result;
	}

	/*
	 * 主页的登录操作
	 * @param parameter 前端传来的参数，包括用户名/密码/验证码
	 * @return 发回前端的参数
	 */
	public Map<String, Object> login(Map<String, String> parameter) {
		if (dao == null)
			dao = UserDAO.getInstance();
		Map<String, Object> result = new HashMap<>();
		String text_username = null;
		String text_password = null;
		//Check invalidation of username and password in parameters 
		if (parameter.get("Text_username") == null || parameter.get("Text_password") == null) {
			result.put("DispatchURL", "index.jsp");
			result.put("Redirect", true);
		} else if (!parameter.get("Text_username").isEmpty() || !parameter.get("Text_password").isEmpty()) {
			// Check whether username and password in parameters are empty
			//If not empty
			text_username = parameter.get("Text_username");
			text_password = parameter.get("Text_password");
			if (parameter.get("login_verifycode_name") == null) {
				// verify code name in session does not exist, load it again.
				// This is probably caused by expired session
				result.put("DispatchURL", "index.jsp");
				result.put("Msg", "Verify Code is expired!");
				result.put("Firsttime", false);
				result.put("VerifyCode", VerifyCode.getVerifyCode());
				logger.info("User Login Failed: Username <" + text_username + "> with expired verify code.");
			} else {
				// Check verify code input by user
				if (VerifyCode.checkVerifyCodeAns(parameter.get("login_verifycode_name"),
						parameter.get("Text_verifycode")))
					// Verify code is valid, then check username and password
					if (checkLogin(text_username, text_password)) {
						// Checking(login) succeeded
						result.put("DispatchURL", "homepage");
						result.put("Redirect", true);
						result.put("login_username", text_username);
						result.put("login_password", text_password);
						result.put("login_user_id", getUserIDByName(text_username));
						logger.info("User Login Succeeded: Username <" + text_username + ">");
					} else {
						//Login failed, username/password is wrong
						result.put("DispatchURL", "index.jsp");
						result.put("Msg", "Username(ID) or password is wrong");
						result.put("Firsttime", false);
						result.put("VerifyCode", VerifyCode.getVerifyCode());
						logger.info("User Login Failed: Username <" + text_username
								+ "> with wrong username(ID) or password.");
					}
				else {
					//verify code is wrong
					result.put("DispatchURL", "index.jsp");
					result.put("Msg", "Verify code is wrong");
					result.put("Firsttime", false);
					result.put("VerifyCode", VerifyCode.getVerifyCode());
					logger.info("User Login Failed: Username <" + text_username + "> with wrong verify code.");
				}
			}
		} else {
			// the first time load index page, check username and
			// password stored in session
			if (checkLogin(parameter.get("login_user_id"), parameter.get("login_username"),
					parameter.get("login_password"))) {
				//Checking succeed, then automatically login
				result.put("DispatchURL", "homepage");
				result.put("Redirect", true);
				logger.info("User Session Login: Username <" + parameter.get("login_username") + ">");
			} else {
				result.put("DispatchURL", "index.jsp");
				result.put("Firsttime", false);
				result.put("VerifyCode", VerifyCode.getVerifyCode());
				logger.info("User Session Login Failed: Username <" + parameter.get("login_username") + ">");
			}
		}

		return result;
	}

	/*
	 * 检查用户登录信息是否正确<br>
	 * 本函数只使用username和password，使用在login页面的检查
	 */
	public static boolean checkLogin(String username, String password) {
		if (dao == null)
			dao = UserDAO.getInstance();
		Map<String, String> para = new HashMap<>();
		para.put("Text_username", username);
		para.put("Text_password", password);
		List<Object> query_result = dao.getLogin(para);
		if ((long) query_result.get(0) == 1)
			return true;
		else
			return false;
	}

	/*
	 * 检查用户登录信息是否正确<br>
	 * 本函数是上一个函数的重载，使用Object类型username、password，减少在调用函数中Object转String的操作
	 */
	public static boolean checkLogin(Object username, Object password) {
		if (dao == null)
			dao = UserDAO.getInstance();
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

	/*
	 * This method is designed for check session login info, not only username
	 * matches password, but also user_id matches username.
	 */
	public static boolean checkLogin(String user_id, String username, String password) {
		if (dao == null)
			dao = UserDAO.getInstance();
		Map<String, String> para = new HashMap<>();
		para.put("Text_user_id", user_id);
		para.put("Text_username", username);
		para.put("Text_password", password);
		List<Object> query_result = dao.getLogin(para);
		if ((long) query_result.get(0) == 1)
			return true;
		else
			return false;
	}

	/*
	 * This method is designed for check session login info, not only username
	 * matches password, but also user_id matches username.
	 */
	public static boolean checkLogin(Object user_id, Object username, Object password) {
		if (dao == null)
			dao = UserDAO.getInstance();
		Map<String, String> para = new HashMap<>();
		if (user_id == null || username == null || password == null)
			return false;
		else {
			para.put("Text_user_id", (String) user_id);
			para.put("Text_username", (String) username);
			para.put("Text_password", (String) password);
			List<Object> query_result = dao.getLogin(para);
			if ((long) query_result.get(0) == 1)
				return true;
			else
				return false;
		}
	}

	/*
	 * 检查用户名是否存在
	 * @param username 待检查的用户名
	 * @result 检查结果，布尔型
	 */
	public static boolean checkUsername(String username) {
		if (dao == null)
			dao = UserDAO.getInstance();
		Map<String, String> para = new HashMap<>();
		para.put("Text_username", username);
		long rs = (long) dao.checkUsername(para).get(0);
		if (rs == 0)
			return false;
		else if (rs == 1)
			return true;
		else {
			logger.error("Duplicate User: Username <" + username + ">");
			return true;
		}
	}

	/*
	 * 根据username获取user_id
	 * @param username 待查询的用户名
	 * @result user_id
	 */
	public static String getUserIDByName(String username) {
		if (dao == null)
			dao = UserDAO.getInstance();
		try {
			List<Object> query_result = dao.getUserIDByName(username);
			return (String) query_result.get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}
