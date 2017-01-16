package souvenirs.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import souvenirs.PersonalAlbum;
import souvenirs.Picture;
import souvenirs.SharedAlbum;
import tool.DB;

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
	 * ��ȡĳ�û����и�������ȫ����Ϣ��
	 * <strong>ע�⣺������Ҳ���Ի�ȡ���û����ܷ��ʵĹ��������Ϣ�����ǻ�ȡ����Ϣ����PersonalAlbum����ʽ�洢��������ʹ�á�</strong>
	 * @param user_id ӵ�������û���
	 * @param type ��ȡ��������ͣ�������SouvenirsDAO�ĳ�������
	 * @return һ���б�ÿһ���һ��PersonalAlbum����
	 * @throws Exception ���ݿ��ѯ�����store�ӿڵ��ô�����׳��쳣
	 * @see souvenirs.dao.SouvenirsDAO#PERSONAL_ALBUM
	 * @see souvenirs.dao.SouvenirsDAO#ALL_ALBUM
	 * @see souvenirs.PersonalAlbum
	 */
	public List<PersonalAlbum> getAllPAlbumInfo(String user_id, int type) throws Exception {
		String sql = "SELECT owner_id, album_name, intro, album_cover, create_timestamp FROM souvenirs.query_available_album where user_id=?";
		if (type == PERSONAL_ALBUM)
			sql += " and isPersonal = 'true'";
		else if (type == SHARED_ALBUM) 
			sql += " and isPersonal = 'false'";
		sql += " order by album_name asc";
		List<String>parameter = Arrays.asList(user_id);
		return DB.execSQLQuery(sql, parameter, new PAlbumImplStore());
	}

	/**
	 * ��ȡָ����������ȫ����Ϣ
	 * @param user_id �û�ID
	 * @param album_name �����
	 * @return ��������Ϣ��PersonalAlbum��
	 * @throws Exception ���ݿ��ѯ�����store�ӿڵ��ô�����׳��쳣
	 * @see souvenirs.PersonalAlbum
	 */
	public PersonalAlbum getPAlbumInfo(String user_id, String album_name) throws Exception {
		String sql = "SELECT owner_id, album_name, intro, album_cover, create_timestamp FROM souvenirs.query_available_album where user_id=? and album_name=? and isPersonal='true'";
		List<String>parameter = Arrays.asList(user_id, album_name);
		return DB.execSQLQuery(sql, parameter, new PAlbumImplStore()).get(0);
	}
	
	/**
	 * ��ȡĳ�û��ɷ��ʵ����й�������ȫ����Ϣ
	 * <strong>ע�⣺������Ҳ���Ի�ȡ���û��ĸ��������Ϣ�����ǻ�ȡ����Ϣ����SharedAlbum����ʽ�洢��������ʹ�á�</strong>
	 * @param user_id �û���
	 * @param type ��ȡ��������ͣ�������SouvenirsDAO�ĳ�������
	 * @return һ���б�ÿһ���һ��SharedAlbum����
	 * @throws Exception ���ݿ��ѯ�����store�ӿڵ��ô�����׳��쳣
	 * @see souvenirs.dao.SouvenirsDAO#SHARED_ALBUM
	 * @see souvenirs.dao.SouvenirsDAO#ALL_ALBUM
	 * @see souvenirs.SharedAlbum
	 */
	public List<SharedAlbum> getAllSAlbumInfo(String user_id, int type) throws Exception {
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
	
	/**
	 * ��ȡָ�������ȫ��ͼƬ����Ϣ
	 * @param user_id �û�ID
	 * @param album �����
	 * @return һ��Picture�����List������˸������������Ƭ����Ϣ
	 * @throws Exception ���ݿ��ѯִ��ʧ�ܣ�PictureImplStore��format����ִ��ʧ�ܶ����׳��쳣
	 * @see souvenirs.Picture
	 * @see souvenirs.dao.PictureImplStore#format(List)
	 */
	public List<Picture> getAllPictureInfo(String user_id, String album) throws Exception {
		String sql = "SELECT owner_id, owner_album_name, owner_filename, owner_format, owner_description, owner_upload_timestamp FROM souvenirs.query_available_image where user_id=? and  album_name=?  order by album_name asc";
		List<String>parameter = Arrays.asList(user_id, album);
		logger.debug("parameter:"+parameter);
		return DB.execSQLQuery(sql, parameter, new PictureImplStore());
	}


}
