package user.web;

import java.io.File;
import java.util.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import souvenirs.PersonalAlbum;
import souvenirs.dao.SouvenirsDAO;
import tool.ImageLoader;
import tool.PropertyOper;
import tool.VerifyCode;
import upload.dao.UploadDAO;
import user.User;
import user.dao.UserDAO;

/**
 * 用户管理业务类，完成与用户相关的操作：用户注册、用户登录、检查用户是否已经登录、用户修改信息、用户登出<br>
 * 检查用户登录的方法以静态方法的形式提供API
 */
public class UserManager {
	private static UserManager user_manager = new UserManager();
	private static UserDAO dao = null;
	private static Logger logger = Logger.getLogger(UserManager.class);
	final int REGISTER_DEFAULT_PARA = 2;// login_username,login_password
	final int LOGIN_DEFAULT_PARA = 2;// login_username, login_password
	private final static int DEFAULT_AFFECTED_ROW = 1;

	public UserManager() {
		// TODO Auto-generated constructor stub
		//checkValidDAO();
	}

	/**
	 * 单例模式获取对象的方法
	 * 
	 * @return UserManager类的对象
	 */
	public static UserManager getInstance() {
		return user_manager;
	}

	/**
	 * 维护DAO对象的可用性
	 */
	private static void checkValidDAO() {
		if (dao == null)
			dao = UserDAO.getInstance();
	}

	/**
	 * 注册操作
	 * 
	 * @param parameter
	 *            前端传来的参数表，key包括Text_username(用户名)，Text_password(密码)，
	 *            login_user_id(session存储的用户ID),
	 *            login_username(session存储的用户名)，login_password(session存储的用户密码)
	 * @return 发给前端的参数，包括待转向的页面地址(key=DispatchURL)、注册的结果(key=Result)、返回的结果消息(key=Msg)
	 */
	public Map<String, Object> register(Map<String, String> parameter) {
		checkValidDAO();
		Map<String, Object> result = new HashMap<>();
		try {
			// User has already login the system, he/she cannot register
			if (checkLogin(parameter.get("login_username"), parameter.get("login_password"))) {
				result.put("DispatchURL", "register.jsp");
				result.put("Result", true);
				result.put("Msg", "Already login");
				logger.info("Register failed: <" + parameter.get("login_username") + "> has already login.");
			} else {
				if (parameter.size() > REGISTER_DEFAULT_PARA) {
					// Large than REGISTER_DEFAULT_PARA means there are valid
					// parameters for creating a new user
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
							// Register succeeded.
							result.put("DispatchURL", "register.jsp");
							// Insert successfully
							result.put("Result", true);
							logger.info("Register succeeded:Username <" + parameter.get("Text_username") + ">");
						} else {
							// Register failed.
							result.put("DispatchURL", "register.jsp");
							// Insert failed
							result.put("Result", false);
							result.put("Msg", "Database Error occured, please try again");
							logger.info("Register failed: Database internal error");
						}
					}
				} else { // The first time to display register page
					result.put("DispatchURL", "register.jsp");
				}
			}
		} catch (Exception e) {
			// An error occurred when executing query
			result.put("DispatchURL", "index.jsp");
			result.put("Result", false);
			result.put("Msg", "Sorry! An internal error occurred.");
			logger.error("Internal error with login! Error:<"+e.getMessage()+">");
		}
		return result;
	}

	/**
	 * 主页的登录操作
	 * 
	 * @param parameter
	 *            前端传来的参数，key包括Text_username(用户名)，Text_password(密码)，
	 *            Text_verifycode(验证码)，login_user_id(session存储的用户ID),
	 *            login_username(session存储的用户名)，login_password(session存储的用户密码)，
	 *            login_verifycode_name(session存储的验证码)
	 * @return 发回前端的参数，包括待转向的页面地址(key=DispatchURL)、redirect状态(redirect/foward,
	 *         key=Redirect)、验证码图片地址(部分情况,
	 *         key=VerifyCode)、firsttime状态(指示index是否自动刷新, key=Firsttime)、
	 *         返回的错误信息(部分情况,key=Msg)、登陆用户名(仅在登录成功时使用，key=login_username)、登陆用户密码(
	 *         仅在登录成功时使用，key=login_password)、登陆用户ID(仅在登录成功时使用，key=login_user_id)
	 *         。详情请参考开发文档
	 */
	public Map<String, Object> login(Map<String, String> parameter) {
		checkValidDAO();
		Map<String, Object> result = new HashMap<>();
		String text_username = null;
		String text_password = null;
		try {
			// Check invalidation of username and password in parameters
			if (parameter.get("Text_username") == null || parameter.get("Text_password") == null) {
				result.put("DispatchURL", "index.jsp");
				result.put("Redirect", true);
			} else if (!parameter.get("Text_username").isEmpty() || !parameter.get("Text_password").isEmpty()) {
				// Check whether username and password in parameters are empty
				// If not empty
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
							// Obtain user information and store in session(in UserServ)
							result.put("login_user", dao.getUserInfoByUsername(text_username, text_password));
							result.put("login_username", text_username);
							result.put("login_password", text_password);
							result.put("login_user_id", getUserIDByName(text_username));
							logger.info("User Login Succeeded: Username <" + text_username + ">");
						} else {
							// Login failed, username/password is wrong
							result.put("DispatchURL", "index.jsp");
							result.put("Msg", "Username(ID) or password is wrong");
							result.put("Firsttime", false);
							result.put("VerifyCode", VerifyCode.getVerifyCode());
							logger.info("User Login Failed: Username <" + text_username
									+ "> with wrong username(ID) or password.");
						}
					else {
						// verify code is wrong
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
					// Checking succeed, then automatically login
					result.put("DispatchURL", "homepage");
					result.put("Redirect", true);
					// Obtain user information and store in session(in UserServ)
					result.put("login_user", dao.getUserInfoByUsername(parameter.get("login_username"), parameter.get("login_password")));
					logger.info("User Session Login: Username <" + parameter.get("login_username") + ">");
				} else {
					result.put("DispatchURL", "index.jsp");
					result.put("Firsttime", false);
					result.put("VerifyCode", VerifyCode.getVerifyCode());
					logger.info("User Session Login Failed: Username <" + parameter.get("login_username") + ">");
				}
			}
		} catch (Exception e) {
			// An error occurred when executing query
			result.put("DispatchURL", "index.jsp");
			result.put("Firsttime", false);
			result.put("VerifyCode", VerifyCode.getVerifyCode());
			result.put("Msg", "Sorry! An internal error occurred.");
			logger.error("Internal error with login! Error:<"+e.getMessage()+">");
		}
		return result;
	}

	/**
	 * 检查用户登录信息是否正确<br>
	 * 本函数只使用username和password，仅在login页面登录时使用
	 * 
	 * @param username
	 *            待检查的用户名
	 * @param password
	 *            待检查的密码
	 * @return 验证结果，true代表验证成功，false代表验证失败
	 * @throws Exception 数据库执行失败时抛出异常
	 */
	private boolean checkLogin(String username, String password) throws Exception {
		checkValidDAO();
		Map<String, String> para = new HashMap<>();
		para.put("Text_username", username);
		para.put("Text_password", password);
		List<Object> query_result = null;
		try {
			query_result = dao.getLogin(para);
			//logger.debug("query_result:"+query_result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("", e);
			throw e;
		}
		if ((long) query_result.get(0) == 1)
			return true;
		else
			return false;
	}

	/**
	 * 检查用户登录信息是否正确，使用Object类型username、password，减少在调用函数中Object转String的操作<br>
	 * <strong>注意：本方法未被使用</strong>
	 * 
	 * @param username
	 *            待验证用户名
	 * @param password
	 *            待验证密码
	 * @return 验证结果，true代表验证成功，false代表验证失败
	 */
	public static boolean checkLogin(Object username, Object password) {
		checkValidDAO();
		Map<String, String> para = new HashMap<>();
		if (username == null || password == null)
			return false;
		else {
			para.put("Text_username", (String) username);
			para.put("Text_password", (String) password);
			List<Object> query_result = null;
			try {
				query_result = dao.getLogin(para);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return false;
			}
			if ((long) query_result.get(0) == 1)
				return true;
			else
				return false;
		}
	}

	/**
	 * 本方法用来验证session中存储的用户登录信息，与之前的方法不同，user_id也在验证范围。本方法使用String类型的参数。<br>
	 * <strong>注意：本方法未被使用</strong>
	 * 
	 * @param user_id
	 *            待验证的用户ID
	 * @param username
	 *            待验证的用户名
	 * @param password
	 *            待验证的密码
	 * @return 验证结果，true代表验证成功，false代表验证失败
	 */
	public static boolean checkLogin(String user_id, String username, String password) {
		checkValidDAO();
		Map<String, String> para = new HashMap<>();
		para.put("Text_user_id", user_id);
		para.put("Text_username", username);
		para.put("Text_password", password);
		List<Object> query_result = null;
		try {
			query_result = dao.getLogin(para);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
		if ((long) query_result.get(0) == 1)
			return true;
		else
			return false;
	}

	/**
	 * 本方法用来验证session中存储的用户登录信息，与之前的方法不同，user_id也在验证范围。本方法使用Object类型的参数。<br>
	 * 
	 * @param user_id
	 *            待验证的用户ID
	 * @param username
	 *            待验证的用户名
	 * @param password
	 *            待验证的密码
	 * @return 验证结果，true代表验证成功，false代表验证失败
	 */
	public static boolean checkLogin(Object user_id, Object username, Object password) {
		checkValidDAO();
		Map<String, String> para = new HashMap<>();
		if (user_id == null || username == null || password == null)
			return false;
		else {
			para.put("Text_user_id", (String) user_id);
			para.put("Text_username", (String) username);
			para.put("Text_password", (String) password);
			List<Object> query_result = null;
			try {
				query_result = dao.getLogin(para);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return false;
			}
			if ((long) query_result.get(0) == 1)
				return true;
			else
				return false;
		}
	}

	/**
	 * 检查用户名是否存在
	 * 
	 * @param username
	 *            待检查的用户名
	 * @return 检查结果，布尔型
	 */
	public static boolean checkUsername(String username) {
		checkValidDAO();
		Map<String, String> para = new HashMap<>();
		para.put("Text_username", username);
		long rs = 0;
		try {
			rs = (long) dao.checkUsername(para).get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
		if (rs == 0)
			return false;
		else if (rs == 1)
			return true;
		else {
			// In theory, this situation does not occur since
			// DAO#checkUsername() checks validation of result from DB
			logger.error("Duplicate User: Username <" + username + ">");
			return true;
		}
	}

	/**
	 * 根据username获取user_id
	 * 
	 * @param username
	 *            待查询的用户名
	 * @return user_id字符串
	 */
	public static String getUserIDByName(String username) {
		checkValidDAO();
		try {
			List<Object> query_result = dao.getUserIDByName(username);
			return (String) query_result.get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 根据user_id获取username
	 * @param user_id 用户ID
	 * @return user_id对应的用户名
	 */
	public static String getUsernameByID(String user_id) {
		checkValidDAO();
		try {
			String query_result = dao.getUsernameByID(user_id);
			return query_result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return "";
		}
	}
	
	public Map<String, Object> displaySetting(Map<String, String> parameters) {
		checkValidDAO();
		String user_id = parameters.get("login_user_id");
		String password = parameters.get("login_password");
		Map<String, Object> result = new HashMap<>();
		try {
			User user = dao.getUserInfoById(user_id, password);
			result.put("User_id", user.getUserId());
			result.put("Username", user.getUsername());
			result.put("Avatar", ImageLoader.genAddrOfAvatar(user_id));
			result.put("MRT", user.getReloadTimesMax());
			result.put("LT", user.getLoadTimeout());
			List<PersonalAlbum> pAlbum = SouvenirsDAO.getInstance().getAllPAlbumInfo(user_id, SouvenirsDAO.PERSONAL_ALBUM);
			List<String> pAlbum_name = new ArrayList<>();
			for (PersonalAlbum personalAlbum : pAlbum) {
				pAlbum_name.add(personalAlbum.getAlbumName());
			}
			result.put("PAlbum", pAlbum_name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("Fail to display user's setting page. Parameters: user_id=<"+user_id+">, passwrod=<"+password+">");
		}
		result.put("DispatchURL", "setting.jsp");
		return result;
	}
	
	public Map<String, Object> updateSettings(Map<String, String> parameters, FileItem file_handle) {
		checkValidDAO();
		String user_id = parameters.get("login_user_id");
		String password = parameters.get("login_password");
		String username = parameters.get("username");
		String old_password = parameters.get("old_password");
		String new_password = parameters.get("new_password");
		String new_password_comfirm = parameters.get("new_pwd_confirm");
		String pwd_checkbox = parameters.get("pwd_checkbox");
		
		//logger.debug("parameters: "+parameters);
		JSONArray exec_result = new JSONArray();
		Map<String, Object> result = new HashMap<>();
		try {
			User user = dao.getUserInfoById(user_id, password);
			//logger.debug("before username");
			if (!user.getUsername().contentEquals(username)) {
				int rs = dao.updateUsername(user_id, username);
				if (rs == DEFAULT_AFFECTED_ROW) {
					JSONObject obj = new JSONObject();
					obj.put("item", "user ID");
					obj.put("result", "success");
					exec_result.put(obj);
					result.put("new_username", username);
					logger.info("User updated its username: Parameters: user_id=<"+user_id+">, previous username=<"+user.getUsername()+">, "
							+ "current username=<"+username+">");
				} else {
					JSONObject obj = new JSONObject();
					obj.put("item", "user ID");
					obj.put("result", "Invalid result set");
					exec_result.put(obj);
					logger.warn("User failed to update its username: Parameters: user_id=<"+user_id+">, previous username=<"+user.getUsername()+">, "
							+ "current username=<"+username+">");					
				}
			}
			//logger.debug("after username");
			// 改密码部分
			if (pwd_checkbox!=null && pwd_checkbox.contentEquals("on") && old_password.contentEquals(password) && new_password.contentEquals(new_password_comfirm)) {
				int rs = dao.updatePassword(user_id, new_password);
				if (rs == DEFAULT_AFFECTED_ROW) {
					JSONObject obj = new JSONObject();
					obj.put("item", "password");
					obj.put("result", "success");
					exec_result.put(obj);
					result.put("new_password", new_password);
					parameters.put("login_password", new_password);
					logger.info("User updated its password: Parameters: user_id=<"+user_id+">, previous password=<"+password+">, "
							+ "current password=<"+new_password+">");
				} else {
					JSONObject obj = new JSONObject();
					obj.put("item", "password");
					obj.put("result", "Invalid result set");
					exec_result.put(obj);
					logger.warn("User failed to update its password: Parameters: user_id=<"+user_id+">, previous password=<"+password+">, "
							+ "current password=<"+new_password+">");					
				}
			}
			//logger.debug("after password");
			// 改MRT部分
			if (parameters.get("MRT").matches("^[0-9]*[1-9][0-9]*")) {
				int MRT = Integer.valueOf(parameters.get("MRT"));
				
				if (user.getReloadTimesMax() != MRT) {
					int rs = dao.updateMRT(user_id, MRT);
					if (rs == DEFAULT_AFFECTED_ROW) {
						JSONObject obj = new JSONObject();
						obj.put("item", "MRT");
						obj.put("result", "success");
						exec_result.put(obj);
						logger.info("User updated its reload_times_max settings: Parameters: user_id=<"+user_id+">, previous MRT=<"+user.getReloadTimesMax()+">, "
								+ "current MRT=<"+MRT+">");
					} else {
						JSONObject obj = new JSONObject();
						obj.put("item", "MRT");
						obj.put("result", "Invalid result set");
						exec_result.put(obj);
						logger.warn("User failed to update its  reload_times_max settings: Parameters: user_id=<"+user_id+">, previous MRT=<"+user.getReloadTimesMax()+">, "
								+ "current MRT=<"+MRT+">");					
					}
				}
			} else {
				JSONObject obj = new JSONObject();
				obj.put("item", "MRT");
				obj.put("result", "Invalid result set");
				exec_result.put(obj);
				logger.info("User failed to update its  reload_times_max settings since invalid MRT format: "
						+ "Parameters: user_id=<"+user_id+">, MRT=<"+parameters.get("MRT")+">");	
			}
			// 改LT部分
			if (parameters.get("LT").matches("^[0-9]*[1-9][0-9]*")) {
				int LT =  Integer.valueOf(parameters.get("LT"));
				
				if (user.getLoadTimeout() != LT) {
					int rs = dao.updateLT(user_id, LT);
					if (rs == DEFAULT_AFFECTED_ROW) {
						JSONObject obj = new JSONObject();
						obj.put("item", "LT");
						obj.put("result", "success");
						exec_result.put(obj);
						logger.info("User updated its load_timeout settings: Parameters: user_id=<"+user_id+">, previous LT=<"+user.getLoadTimeout()+">, "
								+ "current LT=<"+LT+">");
					} else {
						JSONObject obj = new JSONObject();
						obj.put("item", "LT");
						obj.put("result", "Invalid result set");
						exec_result.put(obj);
						logger.warn("User failed to update its  load_timeout settings: Parameters: user_id=<"+user_id+">, previous LT=<"+user.getLoadTimeout()+">, "
								+ "current LT=<"+LT+">");					
					}
				}
			} else {
				JSONObject obj = new JSONObject();
				obj.put("item", "LT");
				obj.put("result", "Invalid result set");
				exec_result.put(obj);
				logger.info("User failed to update its  load_timeout settings since invalid LT format: "
						+ "Parameters: user_id=<"+user_id+">, LT=<"+parameters.get("LT")+">");	
			}
			//logger.debug("after lt "+file_handle.getSize());
			// Write file into disk
			if (file_handle.getSize() != 0){
				String album_name = parameters.get("album_name");
				String filename = parameters.get("filename");
				// Form absolute file path
				String uploadPath = PropertyOper.GetValueByKey("souvenirs.properties", "data_path") + File.separator
						+ user_id + File.separator + album_name;
	
				// Create path if it does not exist
				File uploadDir = new File(uploadPath);
				logger.debug("upload_path:" + uploadDir.getPath() + " " + uploadDir.exists());
				if (!uploadDir.exists()) {
					logger.debug(uploadDir.mkdirs());
				}
	
				// Create new file
				File storeFile = new File(uploadPath + File.separator +filename);
				logger.debug(uploadPath);
				
				// Save image to disk
				try {
					file_handle.write(storeFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					JSONObject obj = new JSONObject();
					obj.put("item", "profile picture");
					obj.put("result", e.getMessage());
					exec_result.put(obj);
					throw new Exception("Cannot write profile picture since "+e.getMessage());
				}
				// 到这里说明文件修改成功
				UploadDAO udao = UploadDAO.getInstance();
				Map<String, String> para = new HashMap<>();
				para.put("user_id", user_id);
				para.put("album_name", album_name);
				para.put("filename", filename);
				para.put("format", filename.substring(filename.lastIndexOf(".")+1));
				para.put("img_description", "This is user&apos;s profile picture");
				int rs = udao.addPicture(para);
				if (rs == DEFAULT_AFFECTED_ROW) {
					String avatar = File.separator + user_id + File.separator + album_name + File.separator + filename;
					avatar.replaceAll("\\\\", "\\\\\\\\");
					logger.debug("avatar: "+avatar);
					int rs2 = dao.updateAvatar(user_id, avatar);
					if (rs2 == DEFAULT_AFFECTED_ROW) { 
						JSONObject obj = new JSONObject();
						obj.put("item", "profile picture");
						obj.put("result", "success");
						exec_result.put(obj);
						logger.info("User changed its avatar. parameters: user_id=<"+user_id+">, previous avatar=<"+user.getAvatar()+">, current avatar=<"+avatar+">");
						
					}else {
						JSONObject obj = new JSONObject();
						obj.put("item", "profile picture");
						obj.put("result", "Cannot update database record.");
						exec_result.put(obj);
						logger.info("User failed to change its avatar. parameters: user_id=<"+user_id+">, previous avatar=<"+user.getAvatar()+">, current avatar=<"+avatar+">");
					}
				} else {
					JSONObject obj = new JSONObject();
					obj.put("item", "profile picture");
					obj.put("result", "Cannot add profile picture as a new picture.");
					exec_result.put(obj);
					logger.info("User failed to add avatar as new picture. parameters: user_id=<"+user_id+">, previous avatar=<"+user.getAvatar()+">, "
							+ "album=<"+album_name+"> filename=<"+filename+">");

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JSONObject obj = new JSONObject();
			obj.put("item", "update");
			obj.put("result", e.getMessage());
			exec_result.put(obj);
			logger.warn("User failed to update its information since "+e.getMessage()+". Parameters: "+parameters);
		}
		result.putAll(displaySetting(parameters));
		result.put("result", result.toString());
		return result;
	}
}
