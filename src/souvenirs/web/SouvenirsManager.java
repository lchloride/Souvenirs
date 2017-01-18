package souvenirs.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

import souvenirs.Comment;
import souvenirs.PersonalAlbum;
import souvenirs.Picture;
import souvenirs.SharedAlbum;
import souvenirs.dao.SouvenirsDAO;
import tool.ImageLoader;
import tool.PropertyOper;
import tool.exception.BadRequestException;
import user.web.UserManager;

/**
 * Souvenir制作和相册、照片管理的业务类
 */
public class SouvenirsManager {
	private static SouvenirsManager souvenirs_manager = new SouvenirsManager();
	private static Logger logger = Logger.getLogger(SouvenirsManager.class);
	private SouvenirsDAO dao = null;
	final int OWNER_ID = 0;
	final int OWNER_ALBUM_NAME = 1;
	final int OWNER_FILENAME = 2;

	public SouvenirsManager() {
		// checkValidDAO();
	}

	/**
	 * 单例模式获取对象的方法
	 * 
	 * @return SouvenirManager类的对象
	 */
	public static SouvenirsManager getInstance() {
		return souvenirs_manager;
	}

	/**
	 * 维护DAO对象的可用性
	 */
	private void checkValidDAO() {
		if (dao == null)
			dao = SouvenirsDAO.getInstance();
	}

	/**
	 * 获取相册的信息，显示在用户主页上。
	 * 
	 * @param parameter
	 *            前端传来的参数，key包括login_user_id(用户ID)
	 * @return 发回前端的显示参数，包括用户头像(key=Avatar)、个人相册json字符串列表(key=PAlbum_json_list)、
	 *         共享相册json字符串列表(key=SAlbum_json_list)、待转向的页面(key=DispatchURL)
	 * @throws Exception
	 *             获取Album信息失败会抛出异常
	 * @see souvenirs.dao.SouvenirsDAO#getPAlbumInfo(String, int)
	 * @see souvenirs.dao.SouvenirsDAO#getSAlbumInfo(String, int)
	 */
	public Map<String, Object> displayContent(Map<String, String> parameter) throws Exception {
		checkValidDAO();
		Map<String, Object> result = new HashMap<>();
		result.put("Avatar", ImageLoader.genAddrOfAvatar(parameter.get("login_user_id")));

		List<PersonalAlbum> rPAlbums = dao.getAllPAlbumInfo(parameter.get("login_user_id"),
				SouvenirsDAO.PERSONAL_ALBUM);
		JSONObject personal_album_json = null;
		Map<String, String> personal_album_map = new HashMap<>();
		List<String> person_album_json_list = new ArrayList<>();
		for (PersonalAlbum personalAlbum : rPAlbums) {
			personal_album_map.put("album_name", personalAlbum.getAlbumName());
			personal_album_map.put("cover_addr",
					ImageLoader.genAddrOfPAlbumCover(parameter.get("login_user_id"), personalAlbum.getAlbumName()));
			personal_album_json = new JSONObject(personal_album_map);
			person_album_json_list.add(personal_album_json.toString().replaceAll("'", "&apos;"));
		}
		result.put("PAlbum_json_list", person_album_json_list);
		// logger.debug("personal_album_json=<"+person_album_json_list+">");

		List<SharedAlbum> rSAlbums = dao.getAllSAlbumInfo(parameter.get("login_user_id"), SouvenirsDAO.SHARED_ALBUM);
		JSONObject shared_album_json = null;
		Map<String, String> shared_album_map = new HashMap<>();
		List<String> shared_album_json_list = new ArrayList<>();
		for (SharedAlbum sharedAlbum : rSAlbums) {
			shared_album_map.put("album_name", sharedAlbum.getSharedAlbumName());
			shared_album_map.put("cover_addr", ImageLoader.genAddrOfSAlbumCover(sharedAlbum.getGroupId()));
			shared_album_json = new JSONObject(shared_album_map);
			shared_album_json_list.add(shared_album_json.toString().replaceAll("'", "&apos;"));
		}
		result.put("SAlbum_json_list", shared_album_json_list);
		// logger.debug("personal_album_json=<"+person_album_json_list+">");
		result.put("DispatchURL", "homepage.jsp");
		return result;
	}

	/**
	 * 首次打开making页面，完成初始化部分内容(第一个选中的album_list所包含图片的地址json串)的显示以及制作模板的下载
	 * 
	 * @param parameter
	 *            前端传来的参数，key包括login_user_id(登录用户user_id)，template(用户选择的模板名称)
	 * @return 操作完成发送给前端的参数，包括album名称列表、默认显示的album内全部图片地址的json字符串
	 */
	public Map<String, Object> makingSouvenirs(Map<String, String> parameter) {
		checkValidDAO();
		Map<String, Object> result = new HashMap<>();
		result.put("Avatar", ImageLoader.genAddrOfAvatar(parameter.get("login_user_id")));
		if (!parameter.containsKey("query_type")) {
			List<Object> album_name_list = dao.getAlbumName(parameter.get("login_user_id"));
			result.put("Album_List", album_name_list);
			parameter.put("album_name", (String) album_name_list.get(0));
			result.put("Image_JSON", getImageAddrInAlbum(parameter));
			String template = PropertyOper.GetValueByKey("template.properties", parameter.get("template"));
			if (template == null || template.isEmpty())
				template = "[]";
			result.put("Template_json", template);
			result.put("DispatchURL", "canvas.jsp");
		} else {
			result.put("DispatchURL", "canvas.jsp");
		}
		return result;
	}

	/**
	 * 获取特定相册中的所有图片地址，并组成json字符串，构造json的方法
	 * 
	 * @see org.json
	 * @param parameter
	 *            前端传来的参数，key包括login_user_id(登录的用户ID), album_name(相册名)
	 * @return 相册中所有图片名字和地址所组成的json字符串(形如：[{Filename: "A", Addr:"B"}, {Filename: "C", Addr:"D"}, ...])
	 */
	public String getImageAddrInAlbum(Map<String, String> parameter) {
		checkValidDAO();
		// Obtain image address list in specific album
		// image_list is unrefined result set from DB and image_addr_list is refined result set which can be used to
		// form json string
		List<Picture> image_list = null;
		try {
			image_list = dao.getAllPictureInfo(parameter.get("login_user_id"), parameter.get("album_name"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("There are something wrong when getting image address in album <"
					+ parameter.get("login_user_id") + ">, (User ID:<" + parameter.get("album_name") + ">)", e);
		}
		logger.debug("image_list: " + image_list);
		JSONArray json_array = new JSONArray();
		Map<String, String> image_content = null;
		for (Picture image_item : image_list) {
			// Form json object of filename and address
			image_content = new HashMap<>();
			image_content.put("Filename", image_item.getFilename());
			image_content.put("Addr", ImageLoader.genAddrOfPicture(image_item.getUserId(), image_item.getAlbumName(),
					image_item.getFilename()));
			json_array.put(image_content);
		}
		logger.debug("json:" + json_array);
		return json_array.toString();
	}

	/**
	 * 获取相关参数显示在Album管理页面(album.jsp)
	 * 
	 * @param parameter
	 *            前端传来的参数列表，key包括login_user_id(登录用户的ID)、album_name(要获取相册的名称)
	 * @return 返回前端的参数列表，包括用户头像地址(key=Avatar)、该相册封面的地址(key=Album_cover)、该相册的名称(key=Album_name)、
	 *         拥有者的名称(key=Owner_name)、相册介绍(key=Description)、相册所含的图片JSON字符串组成的List列表(key=image_json_list)、
	 *         待转发的页面(key=DispatchURL)
	 * @throws Exception
	 */
	public Map<String, Object> displayAlbumManager(Map<String, String> parameter) throws Exception {
		checkValidDAO();
		Map<String, Object> result = new HashMap<>();
		String user_id = parameter.get("login_user_id");
		String album_name = parameter.get("album_name");
		if (user_id == null || album_name == null || user_id.isEmpty() || album_name.isEmpty())
			throw new BadRequestException("Invalid Parameter user_id OR album_name");
		result.put("Avatar", ImageLoader.genAddrOfAvatar(user_id));

		result.put("Album_cover", ImageLoader.genAddrOfPAlbumCover(user_id, parameter.get("album_name")));
		result.put("Album_name", parameter.get("album_name"));
		result.put("Owner_name", UserManager.getUsernameByID(user_id));
		result.put("Description", dao.getPAlbumInfo(user_id, album_name).getIntro());
		JSONArray jArray = new JSONArray(getImageAddrInAlbum(parameter));
		List<String> image_list = new ArrayList<>();
		for (int i = 0; i < jArray.length(); i++) {
			image_list.add(jArray.getJSONObject(i).toString());
		}
		logger.debug("image_json_list: " + image_list);
		result.put("image_json_list", image_list);
		result.put("DispatchURL", "album.jsp");
		return result;
	}

	/**
	 * Picture管理页面的显示，将图片信息发给页面
	 * @param parameter 前端传来的参数，用来指定图片。key包括login_user_id(登录用户ID)、album_name(相册名)、picture_name(照片名)
	 * @return 发回前端的Map，包括
	 * @throws BadRequestException 前端发来的参数无效时抛出异常
	 * @throws Exception 
	 */
	public Map<String, Object> displayPictureManager(Map<String, String> parameter)
			throws BadRequestException, Exception {
		checkValidDAO();
		Map<String, Object> result = new HashMap<>();
		String album_name = parameter.get("album_name");
		String picture_name = parameter.get("picture_name");
		String user_id = parameter.get("login_user_id");
		if (album_name == null || picture_name == null || album_name.isEmpty() || picture_name.isEmpty())
			throw new BadRequestException("Invalid Parameter album_name OR picture_name");
		result.put("Username", UserManager.getUsernameByID(user_id));
		result.put("Album_name", album_name);
		result.put("Picture_name", picture_name);
		result.put("Picture", ImageLoader.genAddrOfPicture(user_id, album_name, picture_name));
		Picture pic = dao.getPictureInfo(user_id, album_name, picture_name);
		if (pic == null)
			throw new BadRequestException("Cannot find picture with user_id(<" + user_id + ">) album_name(<"
					+ album_name + ">) OR picture_name(<" + picture_name + ">)");
		result.put("Format", pic.getFormat());
		result.put("Description", pic.getDescription());
		List<Comment> comments = dao.getAllComments(user_id, album_name, picture_name);
		logger.debug("comment:"+comments);
		List<String> comment_json_list = new ArrayList<>();
		JSONObject jsonObject = null;
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Comment comment : comments) {
			jsonObject = new JSONObject();
			logger.debug("comment_user_id:"+comment.getCommentUserId());
			jsonObject.put("comment_username", UserManager.getUsernameByID(comment.getCommentUserId()));
			jsonObject.put("comment_user_avatar", ImageLoader.genAddrOfAvatar(comment.getCommentUserId()));
			jsonObject.put("comment_content", comment.getCommentContent());
			jsonObject.put("comment_time", sdf.format(comment.getTime()));
			jsonObject.put("reply_username", UserManager.getUsernameByID(comment.getReplyUserId()));
			comment_json_list.add(jsonObject.toString());
		}
		result.put("Comment_json_list", comment_json_list);
		logger.debug("comment_json_list:"+comment_json_list);
		result.put("DispatchURL", "picture.jsp");
		return result;
	}
}
