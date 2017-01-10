package user.web;

import java.util.*;

import org.apache.log4j.Logger;

import souvenirs.dao.SouvenirsDAO;
import tool.VerifyCode;
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

	/**
	 * ����û���¼��Ϣ�Ƿ���ȷ<br>
	 * ������ֻʹ��username��password������loginҳ���¼ʱʹ��
	 * 
	 * @param username
	 *            �������û���
	 * @param password
	 *            ����������
	 * @return ��֤�����true������֤�ɹ���false������֤ʧ��
	 */
	private boolean checkLogin(String username, String password) {
		checkValidDAO();
		Map<String, String> para = new HashMap<>();
		para.put("Text_username", username);
		para.put("Text_password", password);
		List<Object> query_result = null;
		try {
			query_result = dao.getLogin(para);
			logger.debug("query_result:"+query_result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("", e);
			return false;
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
}
