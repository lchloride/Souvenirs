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
 * �û�����ҵ���࣬������û���صĲ������û�ע�ᡢ�û���¼������û��Ƿ��Ѿ���¼���û��޸���Ϣ���û��ǳ�<br>
 * ����û���¼�ķ����Ծ�̬��������ʽ�ṩAPI
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
	 * ����ģʽ��ȡ����ķ���
	 * 
	 * @return UserManager��Ķ���
	 */
	public static UserManager getInstance() {
		return user_manager;
	}

	/**
	 * ά��DAO����Ŀ�����
	 */
	private static void checkValidDAO() {
		if (dao == null)
			dao = UserDAO.getInstance();
	}

	/**
	 * ע�����
	 * 
	 * @param parameter
	 *            ǰ�˴����Ĳ�����key����Text_username(�û���)��Text_password(����)��
	 *            login_user_id(session�洢���û�ID),
	 *            login_username(session�洢���û���)��login_password(session�洢���û�����)
	 * @return ����ǰ�˵Ĳ�����������ת���ҳ���ַ(key=DispatchURL)��ע��Ľ��(key=Result)�����صĽ����Ϣ(key=Msg)
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
	 * ��ҳ�ĵ�¼����
	 * 
	 * @param parameter
	 *            ǰ�˴����Ĳ�����key����Text_username(�û���)��Text_password(����)��
	 *            Text_verifycode(��֤��)��login_user_id(session�洢���û�ID),
	 *            login_username(session�洢���û���)��login_password(session�洢���û�����)��
	 *            login_verifycode_name(session�洢����֤��)
	 * @return ����ǰ�˵Ĳ�����������ת���ҳ���ַ(key=DispatchURL)��redirect״̬(redirect/foward,
	 *         key=Redirect)����֤��ͼƬ��ַ(�������,
	 *         key=VerifyCode)��firsttime״̬(ָʾindex�Ƿ��Զ�ˢ��, key=Firsttime)��
	 *         ���صĴ�����Ϣ(�������,key=Msg)����½�û���(���ڵ�¼�ɹ�ʱʹ�ã�key=login_username)����½�û�����(
	 *         ���ڵ�¼�ɹ�ʱʹ�ã�key=login_password)����½�û�ID(���ڵ�¼�ɹ�ʱʹ�ã�key=login_user_id)
	 *         ��������ο������ĵ�
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
	 * ����û���¼��Ϣ�Ƿ���ȷ<br>
	 * ������ֻʹ��username��password������loginҳ���¼ʱʹ��
	 * 
	 * @param username
	 *            �������û���
	 * @param password
	 *            ����������
	 * @return ��֤�����true������֤�ɹ���false������֤ʧ��
	 * @throws Exception ���ݿ�ִ��ʧ��ʱ�׳��쳣
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
	 * ����û���¼��Ϣ�Ƿ���ȷ��ʹ��Object����username��password�������ڵ��ú�����ObjectתString�Ĳ���<br>
	 * <strong>ע�⣺������δ��ʹ��</strong>
	 * 
	 * @param username
	 *            ����֤�û���
	 * @param password
	 *            ����֤����
	 * @return ��֤�����true������֤�ɹ���false������֤ʧ��
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
	 * ������������֤session�д洢���û���¼��Ϣ����֮ǰ�ķ�����ͬ��user_idҲ����֤��Χ��������ʹ��String���͵Ĳ�����<br>
	 * <strong>ע�⣺������δ��ʹ��</strong>
	 * 
	 * @param user_id
	 *            ����֤���û�ID
	 * @param username
	 *            ����֤���û���
	 * @param password
	 *            ����֤������
	 * @return ��֤�����true������֤�ɹ���false������֤ʧ��
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
	 * ������������֤session�д洢���û���¼��Ϣ����֮ǰ�ķ�����ͬ��user_idҲ����֤��Χ��������ʹ��Object���͵Ĳ�����<br>
	 * 
	 * @param user_id
	 *            ����֤���û�ID
	 * @param username
	 *            ����֤���û���
	 * @param password
	 *            ����֤������
	 * @return ��֤�����true������֤�ɹ���false������֤ʧ��
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
	 * ����û����Ƿ����
	 * 
	 * @param username
	 *            �������û���
	 * @return �������������
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
	 * ����username��ȡuser_id
	 * 
	 * @param username
	 *            ����ѯ���û���
	 * @return user_id�ַ���
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
	 * ����user_id��ȡusername
	 * @param user_id �û�ID
	 * @return user_id��Ӧ���û���
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
			// �����벿��
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
			// ��MRT����
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
			// ��LT����
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
				// ������˵���ļ��޸ĳɹ�
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
