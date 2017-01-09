package user.dao;

import java.util.*;

import org.apache.log4j.Logger;

import tool.DB;

/*
 * �û����������ݷ��ʶ���
 * �Ե���ģʽʵ��
 */
public class UserDAO {
	private Logger logger = Logger.getLogger(UserDAO.class);
	private static UserDAO user_dao = new UserDAO();
	
	public static UserDAO getInstance() {
		return user_dao;
	}
	
	/*
	 * �û���¼ʱ����û����������Ƿ�ƥ��ĺ���������DB��execSQLQueryִ��
	 * @param para username & password
	 * @return һ��List������ֻ��һ��������ʾ�˸��û���������ƥ��ĸ�����0����ʾ��ƥ�䣬1����ʾƥ�䣬������ֵ��Ϊ����
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
	 * ע���õ�SQLģ�������
	 * @param para �ں�username��password
	 * @return һ����������ʾ���������0����ɹ���1����ʧ��
	 */
	public int register(Map<String, String> para) {
		String sql = "call AddUserWithoutAvatar(?, ?)";
		List<String> parameter = Arrays.asList(para.get("Text_username"), para.get("Text_password"));
		return (int) DB.execSQLQuery(sql, parameter).get(0).get(0);
	}
	
	/*
	 * ����û����Ƿ��Ѿ����ڵ�SQLģ�������׼��
	 * @param para �ں�username
	 * @result ���ز鵽�Ľ����0����δ���֣�1+�����ѳ���
	 */
	public List<Object> checkUsername(Map<String, String> para) {
		String sql = "select count(*) from user where username = ?";
		List<String> parameter = Arrays.asList(para.get("Text_username"));
		return DB.execSQLQuery(sql, parameter).get(0);
	}
	
	/*
	 * ͨ��username��ȡuser_id��SQLģ�������׼��
	 * @param username �û���
	 * @result һ��List��ֻ��һ��Ԫ�أ�����ID��
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
