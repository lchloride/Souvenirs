package user.dao;

import java.util.*;

import org.apache.log4j.Logger;

import tool.DB;

/**
 * 用户操作的数据访问对象，
 * 以单例模式实现
 */
public class UserDAO {
	private Logger logger = Logger.getLogger(UserDAO.class);
	private static UserDAO user_dao = new UserDAO();
	
	/**
	 * 单例模式获取对象的方法
	 * @return UserDAO类的对象
	 */	
	public static UserDAO getInstance() {
		return user_dao;
	}
	
	/**
	 * 用户登录时检查用户名和密码是否匹配的函数，调用DB的execSQLQuery执行
	 * <strong>注意：DAO负责结果合法性的检查</strong> 
	 * @param para key包含Text_username(用户名)、Text_password(用户密码)、Text_user_id(用户ID，可不存在)
	 * @return 一个List，其中只有一个数，表示了该用户名和密码匹配的个数。0个表示不匹配，1个表示匹配，其他数值均为有误<br>
	 * @throws Exception 如果有多条数据可以与username匹配，抛出异常
	 */
	public List<Object> getLogin(Map<String, String> para) throws Exception {
		List<String>parameter = new ArrayList<>();
		String sql = "select count(*) from user where (username = ?";
		parameter.add(para.get("Text_username"));
		if (para.containsKey("Text_user_id")) {
			sql += " and user_id = ?";
			parameter.add(para.get("Text_user_id"));
		}
		sql += ") and password = ?";
		parameter.add(para.get("Text_password"));
		List<Object> result = DB.execSQLQuery(sql, parameter).get(0);
		if ((int)result.get(0) >1 || (int)result.get(0) < 0) {
			logger.error("Duplicate Users: username=<"+para.get("Text_username")+">");
			throw new Exception("Duplicate result found. Please contact administrator.");
		}else
			return result;
	}

	/**
	 * 注册操作的SQL模板与参数
	 * @param para key包括Text_username(待检查的用户名)和Text_password(待检查的密码)
	 * @return 一个整数，表示操作结果，0代表成功，1代表失败
	 */
	public int register(Map<String, String> para) {
		String sql = "call AddUserWithoutAvatar(?, ?)";
		List<String> parameter = Arrays.asList(para.get("Text_username"), para.get("Text_password"));
		return (int) DB.execSQLQuery(sql, parameter).get(0).get(0);
	}
	
	/**
	 * 检查用户名是否已经存在这个操作的SQL模板与参数
	 * @param para key包含Text_username(用户名)
	 * @return 返回查到的结果，0代表未出现，1+代表已出现。<i>不过大于1的数说明数据库中username有重复，是错误的情况</i>
	 * @throws Exception 检查到了多个匹配username的记录，抛出异常。
	 */
	public List<Object> checkUsername(Map<String, String> para) throws Exception {
		String sql = "select count(*) from user where username = ?";
		List<String> parameter = Arrays.asList(para.get("Text_username"));
		List<Object> result =  DB.execSQLQuery(sql, parameter).get(0);
		if ((int)result.get(0) > 1) {
			logger.error("Duplicate Users: username=<"+para.get("Text_username")+">");
			throw new Exception("Duplicate result found. Please contact administrator.");
		}else
			return result;
	}
	
	/**
	 * 通过username获取user_id的SQL模板与参数准备<br><strong>注意：DAO负责结果合法性的检查</strong> 
	 * @param username 用户名
	 * @return 一个List，只有一个元素，是其ID；
	 * @throws Exception 同一个用户名找到了多个ID或者一个ID也没有找到，都是不正常的结果抛出异常
	 */
	public List<Object> getUserIDByName(String username) throws Exception {
		String sql = "select user_id from user where username = ?";
		List<String> parameter = Arrays.asList(username);
		List<List<Object>> result = DB.execSQLQuery(sql, parameter);
		if (result.size() > 1) {
			logger.error("Duplicate Users:Username <"+username+">");
			throw new Exception("Duplicate Users");
		} else {
			if (result.size() == 0) {
				logger.warn("Non-existed Users:Username <"+username+">");
				throw new Exception("User does not exist.");			
			}			
			else 
				return result.get(0);
		}
	}
}
