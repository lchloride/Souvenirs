package user.dao;

import java.util.*;

import org.apache.log4j.Logger;

import tool.DB;

/*
 * 用户操作的数据访问对象
 * 以单例模式实现
 */
public class UserDAO {
	private Logger logger = Logger.getLogger(UserDAO.class);
	private static UserDAO user_dao = new UserDAO();
	
	public static UserDAO getInstance() {
		return user_dao;
	}
	
	/*
	 * 用户登录时检查用户名和密码是否匹配的函数，调用DB的execSQLQuery执行
	 * @param para username & password
	 * @return 一个List，其中只有一个数，表示了该用户名和密码匹配的个数。0个表示不匹配，1个表示匹配，其他数值均为有误
	 */
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

	/*
	 * 注册用的SQL模板与参数
	 * @param para 内含username和password
	 * @return 一个整数，表示操作结果，0代表成功，1代表失败
	 */
	public int register(Map<String, String> para) {
		String sql = "call AddUserWithoutAvatar(?, ?)";
		List<String> parameter = Arrays.asList(para.get("Text_username"), para.get("Text_password"));
		return (int) DB.execSQLQuery(sql, parameter).get(0).get(0);
	}
	
	/*
	 * 检查用户名是否已经存在的SQL模板与参数准备
	 * @param para 内含username
	 * @result 返回查到的结果，0代表未出现，1+代表已出现
	 */
	public List<Object> checkUsername(Map<String, String> para) {
		String sql = "select count(*) from user where username = ?";
		List<String> parameter = Arrays.asList(para.get("Text_username"));
		return DB.execSQLQuery(sql, parameter).get(0);
	}
	
	/*
	 * 通过username获取user_id的SQL模板与参数准备
	 * @param username 用户名
	 * @result 一个List，只有一个元素，是其ID；
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
				logger.error("Non-existed Users:Username <"+username+">");
				throw new Exception("User does not exist.");			
			}			
			else 
				return result.get(0);
		}
	}
}
