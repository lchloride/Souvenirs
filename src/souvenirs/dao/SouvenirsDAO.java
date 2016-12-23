package souvenirs.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import tool.DB;

public class SouvenirsDAO {
	private Logger logger = Logger.getLogger(SouvenirsDAO.class);

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

	public List<List<Object>> getPictureAddrInAlbum(String user_id, String album) {
		String sql = "SELECT owner_id, owner_album_name, owner_filename FROM souvenirs.query_available_image where user_id='"
				+ user_id + "' and  album_name='" + DB.parsePara(album) + "' order by album_name asc";
		return DB.execSQLQuery(sql);
	}
}
