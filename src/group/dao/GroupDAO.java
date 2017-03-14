package group.dao;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import group.Group;
import tool.DB;

public class GroupDAO {
	private Logger logger = Logger.getLogger(GroupDAO.class);
	private static GroupDAO group_dao = new GroupDAO();
	
	/**
	 * 单例模式获取对象的方法
	 * 
	 * @return SouvenirDAO类的对象
	 */
	public static GroupDAO getInstance() {
		return group_dao;
	}
	
	public List<Group> queryGroupByUserID(String user_id, int start_pos, int content_length) throws Exception {
		String sql = "select group_id, group_name, shared_album_name, intro, album_cover, create_timestamp from query_group_with_user where user_id=? limit ?, ?";
		List<Object> para = Arrays.asList(user_id, start_pos, content_length);
		return DB.execSQLQueryO(sql, para, new GroupImplStore());
	}
	
	public int getGroupNumberByUserID(String user_id) throws Exception {
		String sql = "select count(*) from query_group_with_user where user_id = ?";
		List<Object> para = Arrays.asList(user_id);
		List<List<Object>> result = DB.execSQLQueryO(sql, para);
		if (result.size() == 0 || result.get(0).size() == 0)
			throw new Exception("No Result!");
		else
			return (new Long((Long)result.get(0).get(0))).intValue();
	}
}
