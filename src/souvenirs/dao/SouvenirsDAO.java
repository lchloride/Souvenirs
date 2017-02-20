package souvenirs.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
	/**
	 * 查询相册或照片信息时，指定查询范围是个人相册。
	 */
	final public static int PERSONAL_ALBUM = 1;
	/**
	 * 查询相册或照片信息时，指定查询范围是共享相册。
	 */
	final public static int SHARED_ALBUM = 0;
	/**
	 * 查询相册或照片信息时，指定查询范围是全部相册。
	 * <strong>注意：查询全部相册时的返回值只能是个人相册(PersonalAlbum类)或共享相册(SharedAlbum类)，其中可能有信息的丢失。
	 * 查询照片信息的时候不会有信息的丢失，返回值都是Picture类。</strong>
	 */
	final public static int ALL_ALBUM = 2;
	/**
	 * 查询相册信息时，指定需要获取的列号
	 */
	final private static int SHARED_ALBUM_COL = 0;
	/**
	 * 查询照片点赞册信息时，指定需要获取的列号
	 */
	final public static int LIKE_USERNAME_COL = 0;
	/**
	 * 查询共享相册名称时，指定需要获取的列号
	 */
	final public static int SALBUM_NAME_ROW = 0;
	/**
	 * 分享照片成功时的返回值
	 */
	final public static int SHARE_PICTURE_SUCCESS = 1;
	/**
	 * 分享照片失败时的返回值
	 */
	final public static int SHARE_PICTURE_FAILURE = 0;
	/**
	 * 要分享的照片已经分享了(数据库中已存在)时的返回值
	 */
	final public static int SHARE_PICTURE_DUPLICATE = 2;

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
	 * @deprecated
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
	public List<Picture> getAllPictureInfo(String user_id, String album, int range) throws Exception {
		if (range != PERSONAL_ALBUM && range != SHARED_ALBUM && range != ALL_ALBUM)
			throw new Exception("Invalid Parameter: range=<"+range+">");
		String sql_a = "SELECT owner_id, owner_album_name, owner_filename, owner_format, owner_description, owner_upload_timestamp "
				+ "FROM souvenirs.query_available_image where user_id=? and  album_identifier=?";
		String sql_b = " and is_personal = ?";
		String sql_c = " order by album_identifier asc";
		String sql = "";
		List<String> parameter = null;
		if (range == ALL_ALBUM) {
			sql = sql_a + sql_c;
			parameter = Arrays.asList(user_id, album);
		}
		else {
			sql = sql_a + sql_b + sql_c;
			if (range == PERSONAL_ALBUM)
				parameter =  Arrays.asList(user_id, album, "true");
			else
				parameter =  Arrays.asList(user_id, album, "false");
		}
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
		String sql = "select user_id, album_name, filename, comment_id, comment_user_id, comment, is_valid, time, replied_comment_id " +
				"from souvenirs.`query_comment_and _reply` WHERE user_id=? and album_name=? and filename=? order by comment_id asc";
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
							"user.user_id=souvenirs.like_picture.like_user_id and like_picture.user_id=? and album_name=? and filename=? "+
							"order by souvenirs.like_picture.create_timestamp asc";
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
	 * @see group.Group
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
	
	/**
	 * 更新个人相册的相册名
	 * @param user_id 用户名
	 * @param original_album_name 原始相册名
	 * @param new_album_name 新相册名
	 * @return 更新相册名操作的执行结果
	 * @throws Exception 数据库语句执行失败会抛出异常
	 */
	public boolean updatePAlbumName(String user_id, String original_album_name, String new_album_name) throws Exception {
		String sql = "call UpdateAlbumName(?, ?, ?)";
		List<String> para = Arrays.asList(user_id, original_album_name, new_album_name);
		List<List<Object>> rs = DB.execSQLQuery(sql, para);
		if (rs.size() > 0 && rs.get(0).size() > 0)
			return ((int)rs.get(0).get(0)==0)?false:true;
		else
			throw new Exception("Invalid SQL Result with sql:<"+sql+">, parameters:<"+para+">");
	}
	
	/**
	 * 更新个人相册的简介
	 * @param user_id 用户名
	 * @param album_name 待更改相册的名称
	 * @param new_descritpion 新的简介
	 * @return sql操作所影响的行数，操作成功的话应为1
	 * @throws Exception 数据库语句执行失败会抛出异常
	 */
	public int updatePAlbumDescription(String user_id, String album_name, String new_description) throws Exception {
		String sql = "update album set intro=? where user_id=? and album_name = ?";
		List<String> para = Arrays.asList(new_description, user_id, album_name);
		int rs = DB.execSQLUpdate(sql, para);
		return rs;
	}
	
	/**
	 * 删除一张照片
	 * @param user_id 照片所属用户ID
	 * @param album_name 相册名
	 * @param filename 文件名
	 * @return sql操作所影响的行数
	 * @throws Exception 数据库语句执行失败会抛出异常
	 */
	public int deletePicture(String user_id, String album_name, String filename) throws Exception {
		String sql = "delete from picture where user_id=? and album_name=? and filename=?";
		List<String> para = Arrays.asList(user_id, album_name, filename);
		int rs = DB.execSQLUpdate(sql, para);
		return rs;
	}
	
	/**
	 * 更新个人相册的封面
	 * @param user_id 用户名
	 * @param album_name 相册名
	 * @param album_cover 新的相册封面地址
	 * @return sql操作所影响的行数
	 * @throws Exception 数据库语句执行失败会抛出异常
	 */
	public int updatePAlbumCover(String user_id, String album_name, String album_cover) throws Exception {
		String sql = "update album set album_cover = ? where user_id=? and album_name=?";
		List<String> para = Arrays.asList(album_cover, user_id, album_name);
		int rs = DB.execSQLUpdate(sql, para);
		return rs;
	}
	
	/**
	 * 更新共享相册的相册名
	 * @param user_id 执行操作的用户ID
	 * @param group_id 该共享相册所属的小组ID
	 * @param new_album_name 新的共享相册名
	 * @return sql操作所影响的行数
	 * @throws Exception 数据库语句执行失败会抛出异常
	 */
	public int updateSAlbumName(String user_id, String group_id, String new_album_name) throws Exception {
		String sql = "update `group`, user_belong_group set shared_album_name = ? "
				+ "where `group`.group_id = ? and `group`.group_id = user_belong_group.group_id and user_id=?";
		List<String> para = Arrays.asList(new_album_name, group_id, user_id);
		return DB.execSQLUpdate(sql, para);
	}
	
	/**
	 * 更新共享相册的简介
	 * @param user_id 执行操作的用户ID
	 * @param group_id 该共享相册所属的小组ID
	 * @param new_description 新的简介内容
	 * @return sql操作所影响的行数
	 * @throws Exception 数据库语句执行失败会抛出异常
	 */
	public int updateSAlbumDescription(String user_id, String group_id, String new_description) throws Exception {
		String sql = "update `group`, user_belong_group set intro = ? "
				+ "where `group`.group_id = ? and `group`.group_id = user_belong_group.group_id and user_id=?";
		List<String> para = Arrays.asList(new_description, group_id, user_id);
		return DB.execSQLUpdate(sql, para);
	}
	
	/**
	 * 更新共享相册的封面
	 * @param user_id 执行操作的用户ID
	 * @param group_id 该共享相册所属的小组ID
	 * @param new_cover 新的相册封面地址
	 * @return sql操作所影响的行数
	 * @throws Exception 数据库语句执行失败会抛出异常
	 */
	public int updateSAlbumCover(String user_id, String group_id, String new_cover) throws Exception {
		String sql = "update `group`, user_belong_group set album_cover = ? "
				+ "where `group`.group_id = ? and `group`.group_id = user_belong_group.group_id and user_id=?";
		List<String> para = Arrays.asList(new_cover, group_id, user_id);
		return DB.execSQLUpdate(sql, para);
	}
	
	/**
	 * 分享一张照片到一个共享相册
	 * @param user_id 照片所属用户ID
	 * @param album_name 照片所属相册
	 * @param filename 照片名
	 * @param group_id 共享相册所属的小组
	 * @return 一个整数，表示操作结果。数值的含义请参阅常量SHARE_PICTURE_SUCCESS、SHARE_PICTURE_FAILURE、SHARE_PICTURE_DUPLICATE
	 * @throws Exception 数据库操作异常或数据库查询结果为空会抛出异常
	 * @see souvenirs.dao.SouvenirsDAO#SHARE_PICTURE_SUCCESS
	 * @see souvenirs.dao.SouvenirsDAO#SHARE_PICTURE_FAILURE
	 * @see souvenirs.dao.SouvenirsDAO#SHARE_PICTURE_DUPLICATE
	 */
	public int sharePicture(String user_id, String album_name, String filename, String group_id) throws Exception {
		String sql = "call sharePicture(?, ?, ?, ?)";
		List<String>para = Arrays.asList(user_id, album_name, filename, group_id);
		List<List<Object>> rs = DB.execSQLQuery(sql, para);
		if (rs.size() > 0 && rs.get(0).size() > 0)
			return (int)rs.get(0).get(0);
		else
			throw new Exception("Invalid SQL Result with sql:<"+sql+">, parameters:<"+para+">");
	}
	
	/**
	 * 解除一个共享相册中已经分享的一张照片
	 * @param user_id 照片所属用户ID
	 * @param album_name 照片所属相册
	 * @param filename 照片名
	 * @param group_id 共享相册所属的小组
	 * @return 一个整数，表示操作所影响的行数。正常情况下应为1.
	 * @throws Exception 数据库操作异常会抛出异常
	 */
	public int unsharePicture(String user_id, String album_name, String filename, String group_id) throws Exception {
		String sql = "delete from salbum_own_picture where group_id=? and user_id=? and album_name=? and filename=?";
		List<String>para = Arrays.asList(group_id, user_id, album_name, filename);
		return DB.execSQLUpdate(sql, para);
	}
	
	/**
	 * 更新照片的名称
	 * @param user_id 照片所属用户ID
	 * @param album_name 照片所属相册
	 * @param original_filename 原始照片名
	 * @param new_filename 新照片名
	 * @return 布尔值，表示操作结果：true为成功；false为失败
	 * @throws Exception 数据库操作异常或数据库查询结果为空会抛出异常
	 */
	public boolean updatePictureName(String user_id, String album_name, String original_filename, String new_filename) throws Exception {
		String sql = "call UpdatePictureName(?, ?, ?, ?)";
		List<String>para = Arrays.asList(user_id, album_name, original_filename, new_filename);
		List<List<Object>> rs = DB.execSQLQuery(sql, para);
		if (rs.size() > 0 && rs.get(0).size() > 0)
			return ((int)rs.get(0).get(0)==0)?false:true;
		else
			throw new Exception("Invalid SQL Result with sql:<"+sql+">, parameters:<"+para+">");
	}
	
	/**
	 * 更新照片描述
	 * @param user_id 照片所属用户的ID
	 * @param album_name 照片所属相册的相册名
	 * @param filename 照片名
	 * @param new_description 新的描述文字(不需要原始描述文字)
	 * @return 一个整数，表示操作所影响的行数。正常情况下应为1.
	 * @throws Exception 数据库操作异常会抛出异常
	 */
	public int updatePictureDescription(String user_id, String album_name, String filename, String new_description) throws Exception {
		String sql = "update picture set description=? where user_id = ? and album_name = ? and filename = ?";
		List<String>para = Arrays.asList(new_description, user_id, album_name, filename);
		return DB.execSQLUpdate(sql, para);
	}
	
	/**
	 * 为一张照片点赞
	 * @param like_user_id 点赞的用户
	 * @param picture_user_id 照片所属用户的ID
	 * @param album_name 照片所属相册的相册名
	 * @param filename 照片名
	 * @return 一个整数，表示操作所影响的行数。正常情况下应为1.
	 * @throws Exception 数据库操作异常会抛出异常
	 */
	public int likePicture(String like_user_id, String picture_user_id, String album_name, String filename) throws Exception {
		String sql = "insert into like_picture(user_id, album_name, filename, like_user_id) values (?, ?, ?, ?)";
		List<String>para = Arrays.asList(picture_user_id, album_name, filename, like_user_id);
		return DB.execSQLUpdate(sql, para);
	}
	
	/**
	 * 取消某个用户对某张照片的点赞操作
	 * @param like_user_id 取消点赞的用户
	 * @param picture_user_id 照片所属用户的ID
	 * @param album_name 照片所属相册的相册名
	 * @param filename 照片名
	 * @return 一个整数，表示操作所影响的行数。正常情况下应为1. 
	 * @throws Exception 数据库操作异常会抛出异常
	 */
	public int dislikePicture(String like_user_id, String picture_user_id, String album_name, String filename) throws Exception {
		String sql = "delete from like_picture where user_id = ? and album_name = ? and filename = ? and like_user_id = ?";
		List<String>para = Arrays.asList(picture_user_id, album_name, filename, like_user_id);
		return DB.execSQLUpdate(sql, para);
	}
	
	/**
	 * 向一张照片添加一条评论
	 * @param user_id 照片所属用户的ID
	 * @param album_name 照片所属相册的相册名
	 * @param filename 照片名
	 * @param comment_user_id 添加评论的用户
	 * @param comment 评论内容
	 * @param reply_id 该条评论回复的评论编号，如果不回复任何一条评论则为0
	 * @return 布尔值，表示操作结果：true为成功；false为失败
	 * @throws Exception 数据库操作异常或数据库查询结果为空会抛出异常
	 */
	public boolean addComment(String user_id, String album_name, String filename, String comment_user_id, String comment, String reply_id) throws Exception {
		String sql = "call AddComment(?, ?, ?, ?, ?, ?)";
		List<String>para = Arrays.asList(user_id, album_name, filename, comment_user_id, comment, reply_id );
		List<List<Object>> rs = DB.execSQLQuery(sql, para);
		if (rs.size() > 0 && rs.get(0).size() > 0) {
			return ((int)rs.get(0).get(0)==0)?false:true;
		} else
			throw new Exception("Invalid SQL Result with sql:<"+sql+">, parameters:<"+para+">");
	}
	
	/**
	 * 举报一条评论
	 * @param report_user_id 举报人的ID
	 * @param picture_user_id 照片所属用户的ID
	 * @param album_name 照片所属相册的相册名
	 * @param picture_name 照片名
	 * @param comment_id 要举报的评论的编号
	 * @param report_label 举报标签
	 * @param report_content 举报的具体内容
	 * @return 布尔值，表示操作结果：true为成功；false为失败
	 * @throws Exception 数据库操作异常或数据库查询结果为空会抛出异常
	 */
	public boolean reportComment(String report_user_id, String picture_user_id, String album_name, String picture_name, 
			String comment_id, String report_label, String report_content) throws Exception {
		String sql = "call ReportComment(?, ?, ?, ?, ?, ?, ?)";
		List<String>para = Arrays.asList(report_user_id, picture_user_id, album_name, picture_name, comment_id, report_label, report_content );
		List<List<Object>> rs = DB.execSQLQuery(sql, para);
		if (rs.size() > 0 && rs.get(0).size() > 0) {
			return ((int)rs.get(0).get(0)==0)?false:true;
		} else
			throw new Exception("Invalid SQL Result with sql:<"+sql+">, parameters:<"+para+">");
	}
	
	public List<Picture> getLatestPictures(String user_id) throws Exception {
		String sql = "SELECT user_id, album_name, filename, format, description, upload_timestamp FROM souvenirs.picture where user_id = ? order by upload_timestamp desc";
		List<String>para = Arrays.asList(user_id);
		return DB.execSQLQuery(sql, para,  new PictureImplStore());
	}
	
	public int createPAlbum(String user_id, String album_name, String description, String cover) throws Exception {
		String sql = "insert into album(user_id, album_name, intro, album_cover) values(?,?,?,?)";
		List<String>para = Arrays.asList(user_id, album_name, description, cover);
		return DB.execSQLUpdate(sql, para);
	}
	
	public int deletePAlbum(String user_id, String album_name) throws Exception {
		String sql = "delete from album where user_id=? and album_name=?";
		List<String>para = Arrays.asList(user_id, album_name);
		return DB.execSQLUpdate(sql, para);		
	}
	
	public int addPicture(String user_id, String album_name, String filename, String format, String description) throws Exception {
		String sql = "INSERT INTO picture(user_id, album_name, filename, format, description) VALUES(?, ?, ?, ?, ?)";
		List<String> parameter = Arrays.asList(user_id, album_name, filename, format, description);
		return DB.execSQLUpdate(sql, parameter);
	}
}
