package souvenirs.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import group.Group;
import group.dao.GroupImplStore;
import souvenirs.Comment;
import souvenirs.PersonalAlbum;
import souvenirs.Picture;
import souvenirs.SharedAlbum;
import tool.DB;

/**
 * Souvenirs ���ݷ��ʲ㣬����ģʽ
 */
public class SouvenirsDAO {
	private Logger logger = Logger.getLogger(SouvenirsDAO.class);
	private static SouvenirsDAO souvernirs_dao = new SouvenirsDAO();

	final public static int PERSONAL_ALBUM = 1;
	final public static int SHARED_ALBUM = 0;
	final public static int ALL_ALBUM = 2;
	
	final public static int SHARED_ALBUM_COL = 0;

	final public static int LIKE_USERNAME_COL = 0;
	
	final public static int SALBUM_NAME_ROW = 0;
	/**
	 * ����ģʽ��ȡ����ķ���
	 * 
	 * @return SouvenirDAO��Ķ���
	 */
	public static SouvenirsDAO getInstance() {
		return souvernirs_dao;
	}

	/**
	 * ���ɻ�ȡȫ��album�����б��sqlģ�壬����֯��������DBִ��
	 * 
	 * @param user_id
	 *            �û���
	 * @return album�����б�
	 */
	public List<Object> getAlbumName(String user_id) {
		List<List<Object>> rs = new ArrayList<List<Object>>();
		String sql = "SELECT album_name FROM souvenirs.query_available_album where user_id=? order by album_name asc";
		List<String> parameter = Arrays.asList(user_id);
		rs = DB.execSQLQuery(sql, parameter);
		List<Object> result = new ArrayList<>();
		for (List<Object> list : rs) {
			result.add(list.get(0));
		}
		return result;
	}

	/**
	 * ��ȡĳ�û����и�������ȫ����Ϣ�� <strong>ע�⣺������Ҳ���Ի�ȡ���û����ܷ��ʵĹ��������Ϣ�����ǻ�ȡ����Ϣ����PersonalAlbum����ʽ�洢��������ʹ�á�</strong>
	 * 
	 * @param user_id
	 *            ӵ�������û���
	 * @param type
	 *            ��ȡ��������ͣ�������SouvenirsDAO�ĳ�������
	 * @return һ���б�ÿһ���һ��PersonalAlbum����
	 * @throws Exception
	 *             ���ݿ��ѯ�����store�ӿڵ��ô�����׳��쳣
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
		List<String> parameter = Arrays.asList(user_id);
		return DB.execSQLQuery(sql, parameter, new PAlbumImplStore());
	}

	/**
	 * ��ȡָ����������ȫ����Ϣ
	 * 
	 * @param user_id
	 *            �û�ID
	 * @param album_name
	 *            �����
	 * @return ��������Ϣ��PersonalAlbum��
	 * @throws Exception
	 *             ���ݿ��ѯ�����store�ӿڵ��ô�����׳��쳣
	 * @see souvenirs.PersonalAlbum
	 */
	public PersonalAlbum getPAlbumInfo(String user_id, String album_name) throws Exception {
		String sql = "SELECT owner_id, album_name, intro, album_cover, create_timestamp FROM souvenirs.query_available_album where user_id=? and album_name=? and isPersonal='true'";
		List<String> parameter = Arrays.asList(user_id, album_name);
		return DB.execSQLQuery(sql, parameter, new PAlbumImplStore()).get(0);
	}

	/**
	 * ��ȡĳ�û��ɷ��ʵ����й�������ȫ����Ϣ <strong>ע�⣺������Ҳ���Ի�ȡ���û��ĸ��������Ϣ�����ǻ�ȡ����Ϣ����SharedAlbum����ʽ�洢��������ʹ�á�</strong>
	 * 
	 * @param user_id
	 *            �û���
	 * @param type
	 *            ��ȡ��������ͣ�������SouvenirsDAO�ĳ�������
	 * @return һ���б�ÿһ���һ��SharedAlbum����
	 * @throws Exception
	 *             ���ݿ��ѯ�����store�ӿڵ��ô�����׳��쳣
	 * @see souvenirs.dao.SouvenirsDAO#SHARED_ALBUM
	 * @see souvenirs.dao.SouvenirsDAO#ALL_ALBUM
	 * @see souvenirs.SharedAlbum
	 */
	public List<SharedAlbum> getAllSAlbumInfo(String user_id, int type) throws Exception {
		String sql = "SELECT owner_id, album_name, album_cover, create_timestamp FROM souvenirs.query_available_album where user_id=?";
		if (type == PERSONAL_ALBUM)
			sql += " and isPersonal = 'true'";
		else if (type == SHARED_ALBUM)
			sql += " and isPersonal = 'false'";
		sql += " order by album_name asc";
		List<String> parameter = Arrays.asList(user_id);
		return DB.execSQLQuery(sql, parameter, new SAlbumImplStore());
	}

	/**
	 * ���ɻ�ȡһ��albumȫ����Ƭ��ַ��sqlģ�壬����֯��������DBִ��
	 * 
	 * @param user_id
	 *            �û���
	 * @param album_identifier
	 *            ����ʶ�������ڸ�����ᣬ��Ӧ���������(album_name)�����ڹ�����ᣬ��Ӧ����С��ID(group_id)
	 * @return ��Ƭ������ɵĶ�ά�б�(user_id, album_name, filename)
	 */
	public List<List<Object>> getPictureAddrInAlbum(String user_id, String album_identifier) {
		String sql = "SELECT owner_id, owner_album_name, owner_filename FROM souvenirs.query_available_image where user_id=? and  album_identifier=?  order by album_identifier asc";
		List<String> parameter = Arrays.asList(user_id, album_identifier);
		return DB.execSQLQuery(sql, parameter);
	}

	/**
	 * ��ȡָ�������ȫ��ͼƬ����Ϣ
	 * 
	 * @param user_id
	 *            �û�ID
	 * @param album
	 *            �����
	 * @return һ��Picture�����List������˸������������Ƭ����Ϣ
	 * @throws Exception
	 *             ���ݿ��ѯִ��ʧ�ܣ�PictureImplStore��format����ִ��ʧ�ܶ����׳��쳣
	 * @see souvenirs.Picture
	 * @see souvenirs.dao.PictureImplStore#format(List)
	 */
	public List<Picture> getAllPictureInfo(String user_id, String album) throws Exception {
		String sql = "SELECT owner_id, owner_album_name, owner_filename, owner_format, owner_description, owner_upload_timestamp "
				+ "FROM souvenirs.query_available_image where user_id=? and  album_identifier=?  order by album_identifier asc";
		List<String> parameter = Arrays.asList(user_id, album);
		logger.debug("parameter:" + parameter);
		return DB.execSQLQuery(sql, parameter, new PictureImplStore());
	}

	/**
	 * ��ȡָ����Ƭ����Ϣ
	 * 
	 * @param user_id
	 *            �û�ID
	 * @param album
	 *            �����
	 * @param filename
	 *            ��Ƭ��
	 * @return һ���洢��Ƭ������Ϣ��Picture����
	 * @throws Exception
	 *             ���ݿ��ѯʧ�ܻ�PictureImplStore��format����ִ��ʧ�ܶ����׳��쳣
	 * @see souvenirs.Picture
	 * @see souvenirs.dao.PictureImplStore#format(List)
	 */
	public Picture getPictureInfo(String user_id, String album, String filename) throws Exception {
		String sql = "SELECT owner_id, owner_album_name, owner_filename, owner_format, owner_description, owner_upload_timestamp "
				+ "FROM souvenirs.query_available_image where user_id=? and  album_identifier=?  and owner_filename=?";
		List<String> parameter = Arrays.asList(user_id, album, filename);
		List<Picture> result = DB.execSQLQuery(sql, parameter, new PictureImplStore());
		if (result.size() == 0)
			return null;
		else
			return result.get(0);
	}

	/**
	 * ��ȡָ����Ƭ��������Ϣ�����������������Ϣ�Լ����ظ����������۵���Ϣ
	 * 
	 * @param user_id
	 *            �û�ID
	 * @param album_name
	 *            �����
	 * @param filename
	 *            ��Ƭ��
	 * @return һ��List��ÿ����Ա����һ���洢��������Ϣ��Comment����
	 * @throws Exception
	 *             ���ݿ��ѯʧ�ܻ�CommentImplStore��format����ִ��ʧ�ܶ����׳��쳣
	 * @see souvenirs.Comment
	 * @see souvenirs.dao.CommentImplStore#format(List)
	 */
	public List<Comment> getAllComments(String user_id, String album_name, String filename) throws Exception {
		String sql = "SELECT user_id, album_name, picture_name, comment_user_id, comment_content, time, reply_user_id, reply_content"
				+ " FROM souvenirs.`query_comment_and _reply` WHERE user_id=? and album_name=? and picture_name=? order by time asc";
		List<String> parameter = Arrays.asList(user_id, album_name, filename);
		return DB.execSQLQuery(sql, parameter, new CommentImplStore());
	}

	/**
	 * ��ȡָ������Ƭ��������С��
	 * @param user_id �û���
	 * @param album_name �����
	 * @param filename ��Ƭ��
	 * @return һ��string���б��洢�˸���Ƭ�ѷ����С���ID(С���ID�빲�����һһ��Ӧ)
	 */
	public List<String> getPictureBelongGroup(String user_id, String album_name, String filename) {
		String sql = "SELECT group_id from salbum_own_picture where user_id=? and album_name=? and filename=?";
		List<String> parameter = Arrays.asList(user_id, album_name, filename);
		List<List<Object>> rs = DB.execSQLQuery(sql, parameter);
		List<String> result = new ArrayList<>();
		for (List<Object> list : rs) {
			result.add((String) list.get(SHARED_ALBUM_COL));
		}
		return result;
	}
	
	/**
	 * ��ȡϲ��ָ����Ƭ���û��б�
	 * @param user_id �û�ID
	 * @param album_name �����
	 * @param filename ��Ƭ��
	 * @return һ���洢��ϲ������Ƭ�û�����List�б�
	 */
	public List<String> getLikingPersons(String user_id, String album_name, String filename) {
		String sql = "SELECT user.username from souvenirs.like_picture, user where "+
							"user.user_id=souvenirs.like_picture.like_user_id and like_picture.user_id=? and album_name=? and filename=?";
		List<String> parameter = Arrays.asList(user_id, album_name, filename);
		List<List<Object>> rs = DB.execSQLQuery(sql, parameter);
		List<String> result = new ArrayList<>();
		for (List<Object> list : rs) {
			result.add((String) list.get(LIKE_USERNAME_COL));
		}
		return result;
	}
	
	/**
	 * ����С���ID��ȡ��С���Ӧ�Ĺ���������Ϣ
	 * @param group_id С��ID
	 * @return С��ID��Ӧ�Ĺ��������Ϣ���洢��SharedAlbum��
	 * @throws Exception  ���ݿ��ѯʧ�ܻ�CommentImplStore��format����ִ��ʧ�ܶ����׳��쳣
	 * @see souvenirs.SharedAlbum
	 * @see souvenirs.dao.SAlbumImplStore#format(List)
	 */
	public Group getSAlbumInfo(String group_id) throws Exception {
		String sql = "SELECT group_id, group_name, intro, shared_album_name, album_cover, create_timestamp FROM souvenirs.`group` where group_id=?";
		List<String> parameter = Arrays.asList(group_id);
		List<Group> rs = DB.execSQLQuery(sql, parameter, new GroupImplStore());
		Group result = new Group();
		if (rs.size() == 0)
			return result;
		else
			return rs.get(SALBUM_NAME_ROW);
	}
}
