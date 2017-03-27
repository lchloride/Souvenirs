package user.dao;

import java.util.*;

import org.apache.log4j.Logger;

import tool.DB;
import user.User;

/**
 * �û����������ݷ��ʶ���
 * �Ե���ģʽʵ��
 */
public class UserDAO {
	private static Logger logger = Logger.getLogger(UserDAO.class);
	//private static UserDAO user_dao = new UserDAO();
	private static final UserDAO user_dao = new UserDAO();  

	
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
		if ((long)result.get(0) >1 || (long)result.get(0) < 0) {
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
		if ((long)result.get(0) > 1) {
			logger.error("Duplicate Users: username=<"+para.get("Text_username")+">");
			throw new Exception("Duplicate result found. Please contact administrator.");
		}else
			return result;
	}
	
	/**
	 * ׼����username��ȡuser_id��SQLģ��Ͳ���<br><strong>ע�⣺DAO�������Ϸ��Եļ��</strong> 
	 * @param username �û���
	 * @return һ��List��ֻ��һ��Ԫ�أ�����ID��
	 * @throws Exception ͬһ���û����ҵ��˶��ID����һ��IDҲû���ҵ������ǲ������Ľ�����׳��쳣�����ݿ��ѯִ�г���Ҳ���׳��쳣
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
	
	/**
	 * ׼����user_id��ȡusername��SQLģ��Ͳ���<br><strong>ע�⣺DAO�������Ϸ��Եļ��</strong> 
	 * @param user_id �û�ID
	 * @return user_id��Ӧ���û���
	 * @throws Exception ͬһ���û����ҵ��˶��ID����һ��IDҲû���ҵ������ǲ������Ľ�����׳��쳣�����ݿ��ѯִ�г���Ҳ���׳��쳣
	 */
	public String getUsernameByID(String user_id) throws Exception {
		String sql = "select username from user where user_id = ?";
		List<String> parameter = Arrays.asList(user_id);
		List<List<Object>> result = DB.execSQLQuery(sql, parameter);
		if (result.size() > 1) {
			logger.error("Duplicate Users:User ID <"+user_id+">");
			throw new Exception("Duplicate Users");
		} else {
			if (result.size() == 0) {
				logger.warn("Non-existed Users: User ID <"+user_id+">");
				throw new Exception("User does not exist.");			
			}			
			else 
				return (String)result.get(0).get(0);
		}		
	}
	
	/**
	 * ͨ���û���ID��password��ȡ���û�����Ϣ(�������ֶ�)
	 * @param user_id �û�ID
	 * @param password ���û���Ӧ������
	 * @return User���󣬱������û�����Ϣ(�������ֶ�)
	 * @throws Exception ���ݿ�ִ��ʧ�ܻ��׳��쳣
	 */
	public User getUserInfoById(String user_id, String password) throws Exception {
		String sql = "select user_id, username, avatar, create_timestamp, reload_times_max, load_timeout from user where user_id=? and password=?";
		List<String> para = Arrays.asList(user_id, password);
		List<User> rs = DB.execSQLQuery(sql, para, new UserImplStore());
		if (rs.size() > 0) {
			return rs.get(0);
		} else
			throw new Exception("Invalid SQL Result with sql:<"+sql+">, parameters:<"+para+">");
	}
	
	/**
	 * ͨ���û���ID��password��ȡ���û�����Ϣ(�������ֶ�)
	 * @param username �û���
	 * @param password ���û���Ӧ������
	 * @return User���󣬱������û�����Ϣ(�������ֶ�)
	 * @throws Exception ���ݿ�ִ��ʧ�ܻ��׳��쳣
	 */
	public User getUserInfoByUsername(String username, String password) throws Exception {
		String sql = "select user_id, username, avatar, create_timestamp, reload_times_max, load_timeout from user where username=? and password=?";
		List<String> para = Arrays.asList(username, password);
		List<User> rs = DB.execSQLQuery(sql, para, new UserImplStore());
		if (rs.size() > 0) {
			return rs.get(0);
		} else
			throw new Exception("Invalid SQL Result with sql:<"+sql+">, parameters:<"+para+">");
	}
	
	public int updateUsername(String user_id, String new_username) throws Exception {
		String sql = "update user set username = ? where user_id = ?";
		List<String> para = Arrays.asList(new_username, user_id);
		return DB.execSQLUpdate(sql, para);
	}
	
	public int updatePassword(String user_id, String new_password) throws Exception {
		String sql = "update user set password = ? where user_id = ?";
		List<String> para = Arrays.asList(new_password, user_id);
		return DB.execSQLUpdate(sql, para);
	}
	
	public int updateMRT(String user_id, int new_MRT) throws Exception {
		String sql = "update user set reload_times_max = ? where user_id = ?";
		List<Object> para = Arrays.asList(new_MRT, user_id);
		return DB.execSQLUpdateO(sql, para);
	}
	
	public int updateLT(String user_id, int new_LT) throws Exception {
		String sql = "update user set load_timeout = ? where user_id = ?";
		List<Object> para = Arrays.asList(new_LT, user_id);
		return DB.execSQLUpdateO(sql, para);
	}
	
	public int updateAvatar(String user_id, String avatar) throws Exception {
		String sql = "update user set avatar = ? where user_id = ?";
		List<String> para = Arrays.asList(avatar, user_id);
		return DB.execSQLUpdate(sql, para);
	}
}
