package upload.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tool.DB;

/*
 * Upload功能的数据访问层，完成对应功能的sql语句模板的生成和模板参数的准备
 * UploadDAO实现的是单例模式
 */
public class UploadDAO {
	private static UploadDAO upload_dao = new UploadDAO();
	
	/**
	 * 单例模式获取对象的方法
	 * @return UploadDAO类的对象
	 */	
	public static UploadDAO getInstance() {
		return upload_dao;
	}
	
	/**
	 * 获取user_id的相册列表
	 * @param user_id 用户ID
	 * @return 相册名列表
	 */
	public List<Object> getAlbumName(String user_id) {
		List<List<Object>> rs = new ArrayList<List<Object>>();
		String sql = "SELECT album_name FROM album where user_id=? order by album_name asc";
		List<String> parameter = new ArrayList<>();
		parameter.add(user_id);
		rs = DB.execSQLQuery(sql, parameter);
		List<Object> result = new ArrayList<>();
		for (List<Object> list : rs) {
			result.add(list.get(0));
		}
		return result;
	}
	
	/**
	 * 删除user_id用户的album_name相册的名为filename的一张照片
	 * @param para key包含user_id(用户名), album_name(相册名), filename(文件名)
	 * @return 执行操作所影响的行数
	 * @throws Exception 数据库执行失败时抛出异常
	 * @see tool.DB#execSQLUpdate(String, List)
	 */
	public int delPicture(Map<String, String>para) throws Exception {
		String sql = "DELETE FROM picture WHERE user_id = ? and album_name = ? and filename = ?";
		List<String> parameter = new ArrayList<>();
		parameter.add(para.get("user_id"));
		parameter.add(para.get("album_name"));
		parameter.add(para.get("filename"));
		return DB.execSQLUpdate(sql, parameter);
	}
	
	/**
	 * 添加user_id的album_name下的一张名为filename的照片
	 * @param para key包含user_id(用户名), album_name(相册名), filename(文件名), format(格式), img_description(照片描述)
	 * @return 执行操作所影响的行数
	 * @throws Exception 数据库语句执行失败时抛出异常
	 * @see tool.DB#execSQLUpdate(String, List)
	 */
	public int addPicture(Map<String, String>para) throws Exception {
		String sql = "INSERT INTO picture(user_id, album_name, filename, format, description) VALUES(?, ?, ?, ?, ?)";
		List<String> parameter = new ArrayList<>();
		parameter.add(para.get("user_id"));
		parameter.add(para.get("album_name").replaceAll("'", "&apos;"));
		parameter.add(para.get("filename").replaceAll("'", "&apos;"));
		parameter.add(para.get("format"));
		parameter.add(para.get("img_description").replaceAll("'", "&apos;"));
		return DB.execSQLUpdate(sql, parameter);
	}
}
