package upload.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tool.DB;

public class UploadDAO {
	private static UploadDAO upload_dao = new UploadDAO();
	
	public static UploadDAO getInstance() {
		return upload_dao;
	}
	
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
	
	public Map<String, Object> delPicture(Map<String, String>para) {
		String sql = "DELETE FROM picture WHERE user_id = ? and album_name = ? and filename = ?";
		List<String> parameter = new ArrayList<>();
		parameter.add(para.get("user_id"));
		parameter.add(para.get("album_name"));
		parameter.add(para.get("filename"));
		return DB.execSQLUpdate(sql, parameter);
	}
	
	public Map<String, Object> addPicture(Map<String, String>para) {
		String sql = "INSERT INTO picture(user_id, album_name, filename, format, description) VALUES(?, ?, ?, ?, ?)";
		List<String> parameter = new ArrayList<>();
		parameter.add(para.get("user_id"));
		parameter.add(para.get("album_name"));
		parameter.add(para.get("filename"));
		parameter.add(para.get("format"));
		parameter.add(para.get("img_description"));
		return DB.execSQLUpdate(sql, parameter);
	}
}
