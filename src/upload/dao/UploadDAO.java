package upload.dao;

import java.util.ArrayList;
import java.util.List;

import tool.DB;

public class UploadDAO {
	
	public List<Object> getAlbumName(String user_id) {
		List<List<Object>> rs = new ArrayList<List<Object>>();
		String sql = "SELECT album_name FROM souvenirs.query_available_album where user_id='" + user_id
				+ "' order by album_name asc";
		rs = DB.execSQLQuery(sql);
		List<Object> result = new ArrayList<>();
		for (List<Object> list : rs) {
			result.add(list.get(0));
		}
		return result;
	}
}
