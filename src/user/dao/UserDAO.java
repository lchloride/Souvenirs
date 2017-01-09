package user.dao;

import java.util.*;

import org.apache.log4j.Logger;

import tool.DB;

/**
 * �û����������ݷ��ʶ���
 * �Ե���ģʽʵ��
 */
public class UserDAO {
	private Logger logger = Logger.getLogger(UserDAO.class);
	private static UserDAO user_dao = new UserDAO();
	
	/**
	 * ����ģʽ��ȡ����ķ���
	 * @return UserDAO��Ķ���
	 */	
	public static UserDAO getInstance() {
		return user_dao;
	}
	
	/**
	 * �û���¼ʱ����û����������Ƿ�ƥ��ĺ���������DB��execSQLQueryִ��
	 * <strong>ע�⣺DAO�������Ϸ��Եļ��</strong> 
	 * @param para key����Text_username(�û���)��Text_password(�û�����)��Text_user_id(�û�ID���ɲ�����)
	 * @return һ��List������ֻ��һ��������ʾ�˸��û���������ƥ��ĸ�����0����ʾ��ƥ�䣬1����ʾƥ�䣬������ֵ��Ϊ����<br>
	 * @throws Exception ����ж������ݿ�����usernameƥ�䣬�׳��쳣
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
	 * ע�������SQLģ�������
	 * @param para key����Text_username(�������û���)��Text_password(����������)
	 * @return һ����������ʾ���������0����ɹ���1����ʧ��
	 */
	public int register(Map<String, String> para) {
		String sql = "call AddUserWithoutAvatar(?, ?)";
		List<String> parameter = Arrays.asList(para.get("Text_username"), para.get("Text_password"));
		return (int) DB.execSQLQuery(sql, parameter).get(0).get(0);
	}
	
	/**
	 * ����û����Ƿ��Ѿ��������������SQLģ�������
	 * @param para key����Text_username(�û���)
	 * @return ���ز鵽�Ľ����0����δ���֣�1+�����ѳ��֡�<i>��������1����˵�����ݿ���username���ظ����Ǵ�������</i>
	 * @throws Exception ��鵽�˶��ƥ��username�ļ�¼���׳��쳣��
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
	 * ͨ��username��ȡuser_id��SQLģ�������׼��<br><strong>ע�⣺DAO�������Ϸ��Եļ��</strong> 
	 * @param username �û���
	 * @return һ��List��ֻ��һ��Ԫ�أ�����ID��
	 * @throws Exception ͬһ���û����ҵ��˶��ID����һ��IDҲû���ҵ������ǲ������Ľ���׳��쳣
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
