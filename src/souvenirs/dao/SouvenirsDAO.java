package souvenirs.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import tool.DB;

/**
 * Souvenirs 数据访问层，单例模式
 */
public class SouvenirsDAO {
	private Logger logger = Logger.getLogger(SouvenirsDAO.class);
	private static SouvenirsDAO souvernirs_dao = new SouvenirsDAO();
	
	/**
	 * 单例模式获取对象的方法
	 * @return SouvenirDAO类的对象
	 */	
	public static SouvenirsDAO getInstance() {
		return souvernirs_dao;
	}
	
	/**
	 * 生成获取全部album名字列表的sql模板，并组织参数交给DB执行
	 * @param user_id 用户名
	 * @return album名字列表
	 */
	public List<Object> getAlbumName(String user_id) {
		List<List<Object>> rs = new ArrayList<List<Object>>();
		String sql = "SELECT album_name FROM souvenirs.query_available_album where user_id=? order by album_name asc";
		List<String>parameter = Arrays.asList(user_id);
		rs = DB.execSQLQuery(sql, parameter);
		List<Object> result = new ArrayList<>();
		for (List<Object> list : rs) {
			result.add(list.get(0));
		}
		return result;
	}

	/**
	 * 生成获取一个album全部照片地址的sql模板，并组织参数交给DB执行
	 * @param user_id 用户名
	 * @param album 相册名
	 * @return 照片主键组成的二维列表(user_id, album_name, filename)
	 */
	public List<List<Object>> getPictureAddrInAlbum(String user_id, String album) {
		String sql = "SELECT owner_id, owner_album_name, owner_filename FROM souvenirs.query_available_image where user_id=? and  album_name=? order by album_name asc";
		List<String>parameter = Arrays.asList(user_id, album);
		return DB.execSQLQuery(sql, parameter);
	}
}
