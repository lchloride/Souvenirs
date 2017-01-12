package souvenirs.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import souvenirs.PAlbumImplStore;
import souvenirs.PersonalAlbum;
import souvenirs.SAlbumImplStore;
import souvenirs.SharedAlbum;
import sun.net.www.content.text.plain;
import tool.DB;
import tool.Store;

/**
 * Souvenirs ���ݷ��ʲ㣬����ģʽ
 */
public class SouvenirsDAO  {
	private Logger logger = Logger.getLogger(SouvenirsDAO.class);
	private static SouvenirsDAO souvernirs_dao = new SouvenirsDAO();
	
	final public static int PERSONAL_ALBUM = 1;
	final public static int SHARED_ALBUM = 0;
	final public static int ALL_ALBUM = 2;
	
	/**
	 * ����ģʽ��ȡ����ķ���
	 * @return SouvenirDAO��Ķ���
	 */	
	public static SouvenirsDAO getInstance() {
		return souvernirs_dao;
	}
	
	/**
	 * ���ɻ�ȡȫ��album�����б��sqlģ�壬����֯��������DBִ��
	 * @param user_id �û���
	 * @return album�����б�
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
	 * 
	 */
	public List<PersonalAlbum> getPAlbumInfo(String user_id, int type) throws Exception {
		String sql = "SELECT owner_id, album_name, intro, album_cover, create_timestamp FROM souvenirs.query_available_album where user_id=?";
		if (type == PERSONAL_ALBUM)
			sql += " and isPersonal = 'true'";
		else if (type == SHARED_ALBUM) 
			sql += " and isPersonal = 'false'";
		sql += " order by album_name asc";
		List<String>parameter = Arrays.asList(user_id);
		return DB.execSQLQuery(sql, parameter, new PAlbumImplStore());
	}
	
	public List<SharedAlbum> getSAlbumInfo(String user_id, int type) throws Exception {
		String sql = "SELECT owner_id, album_name, intro, album_cover, create_timestamp FROM souvenirs.query_available_album where user_id=?";
		if (type == PERSONAL_ALBUM)
			sql += " and isPersonal = 'true'";
		else if (type == SHARED_ALBUM) 
			sql += " and isPersonal = 'false'";
		sql += " order by album_name asc";
		List<String>parameter = Arrays.asList(user_id);
		return DB.execSQLQuery(sql, parameter, new SAlbumImplStore());
	}
	
	/**
	 * ���ɻ�ȡһ��albumȫ����Ƭ��ַ��sqlģ�壬����֯��������DBִ��
	 * @param user_id �û���
	 * @param album �����
	 * @return ��Ƭ������ɵĶ�ά�б�(user_id, album_name, filename)
	 */
	public List<List<Object>> getPictureAddrInAlbum(String user_id, String album) {
		String sql = "SELECT owner_id, owner_album_name, owner_filename FROM souvenirs.query_available_image where user_id=? and  album_name=?  order by album_name asc";
		List<String>parameter = Arrays.asList(user_id, album);
		return DB.execSQLQuery(sql, parameter);
	}


}
