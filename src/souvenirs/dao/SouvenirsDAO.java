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
 * Souvenirs 数据访问层，单例模式
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
	 * 单例模式获取对象的方法
	 * 
	 * @return SouvenirDAO类的对象
	 */
	public static SouvenirsDAO getInstance() {
		return souvernirs_dao;
	}

	/**
	 * 生成获取全部album名字列表的sql模板，并组织参数交给DB执行
	 * 
	 * @param user_id
	 *            用户名
	 * @return album名字列表
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
	 * 获取某用户所有个人相册的全部信息。 <strong>注意：本函数也可以获取该用户所能访问的共享相册信息，但是获取的信息会以PersonalAlbum的形式存储。请慎重使用。</strong>
	 * 
	 * @param user_id
	 *            拥有相册的用户名
	 * @param type
	 *            获取的相册类型，类型由SouvenirsDAO的常量定义
	 * @return 一个列表，每一项都是一个PersonalAlbum对象
	 * @throws Exception
	 *             数据库查询错误或store接口调用错误会抛出异常
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
	 * 获取指定个人相册的全部信息
	 * 
	 * @param user_id
	 *            用户ID
	 * @param album_name
	 *            相册名
	 * @return 存放相册信息的PersonalAlbum类
	 * @throws Exception
	 *             数据库查询错误或store接口调用错误会抛出异常
	 * @see souvenirs.PersonalAlbum
	 */
	public PersonalAlbum getPAlbumInfo(String user_id, String album_name) throws Exception {
		String sql = "SELECT owner_id, album_name, intro, album_cover, create_timestamp FROM souvenirs.query_available_album where user_id=? and album_name=? and isPersonal='true'";
		List<String> parameter = Arrays.asList(user_id, album_name);
		return DB.execSQLQuery(sql, parameter, new PAlbumImplStore()).get(0);
	}

	/**
	 * 获取某用户可访问的所有共享相册的全部信息 <strong>注意：本函数也可以获取该用户的个人相册信息，但是获取的信息会以SharedAlbum的形式存储。请慎重使用。</strong>
	 * 
	 * @param user_id
	 *            用户名
	 * @param type
	 *            获取的相册类型，类型由SouvenirsDAO的常量定义
	 * @return 一个列表，每一项都是一个SharedAlbum对象
	 * @throws Exception
	 *             数据库查询错误或store接口调用错误会抛出异常
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
	 * 生成获取一个album全部照片地址的sql模板，并组织参数交给DB执行
	 * 
	 * @param user_id
	 *            用户名
	 * @param album_identifier
	 *            相册标识符；对于个人相册，它应该是相册名(album_name)；对于共享相册，它应该是小组ID(group_id)
	 * @return 照片主键组成的二维列表(user_id, album_name, filename)
	 */
	public List<List<Object>> getPictureAddrInAlbum(String user_id, String album_identifier) {
		String sql = "SELECT owner_id, owner_album_name, owner_filename FROM souvenirs.query_available_image where user_id=? and  album_identifier=?  order by album_identifier asc";
		List<String> parameter = Arrays.asList(user_id, album_identifier);
		return DB.execSQLQuery(sql, parameter);
	}

	/**
	 * 获取指定相册中全部图片的信息
	 * 
	 * @param user_id
	 *            用户ID
	 * @param album
	 *            相册名
	 * @return 一个Picture对象的List，存放了该相册中所有照片的信息
	 * @throws Exception
	 *             数据库查询执行失败，PictureImplStore的format方法执行失败都会抛出异常
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
	 * 获取指定照片的信息
	 * 
	 * @param user_id
	 *            用户ID
	 * @param album
	 *            相册名
	 * @param filename
	 *            照片名
	 * @return 一个存储照片所有信息的Picture对象
	 * @throws Exception
	 *             数据库查询失败或PictureImplStore的format方法执行失败都会抛出异常
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
	 * 获取指定照片的评论信息，包括评论自身的信息以及它回复的那条评论的信息
	 * 
	 * @param user_id
	 *            用户ID
	 * @param album_name
	 *            相册名
	 * @param filename
	 *            照片名
	 * @return 一个List，每个成员都是一个存储着评论信息的Comment对象
	 * @throws Exception
	 *             数据库查询失败或CommentImplStore的format方法执行失败都会抛出异常
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
	 * 获取指定的照片所分享到的小组
	 * @param user_id 用户名
	 * @param album_name 相册名
	 * @param filename 照片名
	 * @return 一个string的列表，存储了该照片已分享的小组的ID(小组的ID与共享相册一一对应)
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
	 * 获取喜欢指定照片的用户列表
	 * @param user_id 用户ID
	 * @param album_name 相册名
	 * @param filename 照片名
	 * @return 一个存储了喜欢该照片用户名的List列表
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
	 * 根据小组的ID获取该小组对应的共享相册的信息
	 * @param group_id 小组ID
	 * @return 小组ID对应的共享相册信息，存储在SharedAlbum中
	 * @throws Exception  数据库查询失败或CommentImplStore的format方法执行失败都会抛出异常
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
