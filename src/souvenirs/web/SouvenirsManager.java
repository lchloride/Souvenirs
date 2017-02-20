package souvenirs.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import group.Group;
import souvenirs.Comment;
import souvenirs.PersonalAlbum;
import souvenirs.Picture;
import souvenirs.SharedAlbum;
import souvenirs.dao.SouvenirsDAO;
import tool.Base64;
import tool.FileOper;
import tool.ImageLoader;
import tool.PropertyOper;
import tool.exception.BadRequestException;
import tool.exception.RenameFolderErrorException;
import upload.dao.UploadDAO;
import user.web.UserManager;

/**
 * Souvenir��������ᡢ��Ƭ�����ҵ����
 */
public class SouvenirsManager {
	private static SouvenirsManager souvenirs_manager = new SouvenirsManager();
	private static Logger logger = Logger.getLogger(SouvenirsManager.class);
	private SouvenirsDAO dao = null;
	final int OWNER_ID = 0;
	final int OWNER_ALBUM_NAME = 1;
	final int OWNER_FILENAME = 2;
	private final static int DEFAULT_AFFECTED_ROW = 1;
	private static String BASE_PATH = PropertyOper.GetValueByKey("souvenirs.properties", "data_path");
	public final static int DEFAULT_LATEST_NUMBER = 4;

	public SouvenirsManager() {
		// checkValidDAO();
	}

	/**
	 * ����ģʽ��ȡ����ķ���
	 * 
	 * @return SouvenirManager��Ķ���
	 */
	public static SouvenirsManager getInstance() {
		return souvenirs_manager;
	}

	/**
	 * ά��DAO����Ŀ�����
	 */
	private void checkValidDAO() {
		if (dao == null)
			dao = SouvenirsDAO.getInstance();
	}

	/**
	 * ��ȡ������Ϣ����ʾ���û���ҳ�ϡ�
	 * 
	 * @param parameter
	 *            ǰ�˴����Ĳ�����key����login_user_id(�û�ID)
	 * @return ����ǰ�˵���ʾ�����������û�ͷ��(key=Avatar)���������json�ַ����б�(key=PAlbum_json_list)��
	 *         �������json�ַ����б�(key=SAlbum_json_list)����ת���ҳ��(key=DispatchURL)
	 * @throws Exception
	 *             ��ȡAlbum��Ϣʧ�ܻ��׳��쳣
	 * @see souvenirs.dao.SouvenirsDAO#getAllPAlbumInfo(String, int)
	 * @see souvenirs.dao.SouvenirsDAO#getAllSAlbumInfo(String, int)
	 */
	public Map<String, Object> displayContentOld(Map<String, String> parameter) throws Exception {
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
			shared_album_map.put("group_id", sharedAlbum.getGroupId());
			shared_album_json = new JSONObject(shared_album_map);
			shared_album_json_list.add(shared_album_json.toString().replaceAll("'", "&apos;"));
		}
		result.put("SAlbum_json_list", shared_album_json_list);
		// logger.debug("personal_album_json=<"+person_album_json_list+">");
		result.put("DispatchURL", "homepage.jsp");
		return result;
	}

	/**
	 * ��ȡ������Ϣ����ʾ���û���ҳ�ϡ�
	 * 
	 * @param parameter
	 *            ǰ�˴����Ĳ�����key����login_user_id(�û�ID)
	 * @return ����ǰ�˵���ʾ�����������û�ͷ��(key=Avatar)���������json�ַ����б�(key=PAlbum_json_list)��
	 *         �������json�ַ����б�(key=SAlbum_json_list)����ת���ҳ��(key=DispatchURL)
	 * @throws Exception
	 *             ��ȡAlbum��Ϣʧ�ܻ��׳��쳣
	 * @see souvenirs.dao.SouvenirsDAO#getAllPAlbumInfo(String, int)
	 * @see souvenirs.dao.SouvenirsDAO#getAllSAlbumInfo(String, int)
	 */
	public Map<String, Object> displayContent(Map<String, String> parameter) throws Exception {
		checkValidDAO();
		String user_id = parameter.get("login_user_id");
		Map<String, Object> result = new HashMap<>();
		result.put("Avatar", ImageLoader.genAddrOfAvatar(user_id));
		result.put("Upload_result", parameter.get("Upload_result"));
		
		// Get information of latest uploaded pictures
		List<Picture> latest_picture_list = dao.getLatestPictures(user_id);
		List<String> picture_json_list = new ArrayList<>();
		Map<String, String> image_content = null;
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int idx = 0;
		for (Picture image_item : latest_picture_list) {
			// Form json object of filename and address
			if (idx >= DEFAULT_LATEST_NUMBER)
				break;
			image_content = new HashMap<>();
			image_content.put("UserID", image_item.getUserId());
			image_content.put("AlbumName", image_item.getAlbumName());
			image_content.put("Filename", image_item.getFilename());
			image_content.put("Addr", ImageLoader.genAddrOfPicture(image_item.getUserId(), image_item.getAlbumName(),
					image_item.getFilename()));
			image_content.put("Username", UserManager.getUsernameByID(image_item.getUserId()));
			image_content.put("Description", image_item.getDescription());
			image_content.put("UploadTime", sdf.format(image_item.getUploadTimestamp()));
			picture_json_list.add(new JSONObject(image_content).toString());
			idx++;
		}
		result.put("picture_json_list", picture_json_list);
		result.put("Picture_count", latest_picture_list.size());

		// Get information list of user's personal albums
		List<PersonalAlbum> rPAlbums = dao.getAllPAlbumInfo(user_id, SouvenirsDAO.PERSONAL_ALBUM);
		JSONObject personal_album_json = null;
		Map<String, Object> personal_album_map = new HashMap<>();
		List<String> person_album_json_list = new ArrayList<>();
		for (PersonalAlbum personalAlbum : rPAlbums) {
			personal_album_map.put("album_name", personalAlbum.getAlbumName());
			personal_album_map.put("cover_addr",
					ImageLoader.genAddrOfPAlbumCover(user_id, personalAlbum.getAlbumName()));
			List<Picture> picture_in_palbum = dao.getAllPictureInfo(user_id, personalAlbum.getAlbumName(),
					SouvenirsDAO.PERSONAL_ALBUM);
			personal_album_map.put("pictures_count", picture_in_palbum.size());
			personal_album_json = new JSONObject(personal_album_map);
			person_album_json_list.add(personal_album_json.toString().replaceAll("'", "&apos;"));
		}
		result.put("PAlbum_json_list", person_album_json_list);
		result.put("PAlbum_count", rPAlbums.size());
		// logger.debug("personal_album_json=<"+person_album_json_list+">");

		List<SharedAlbum> rSAlbums = dao.getAllSAlbumInfo(user_id, SouvenirsDAO.SHARED_ALBUM);
		JSONObject shared_album_json = null;
		Map<String, String> shared_album_map = new HashMap<>();
		List<String> shared_album_json_list = new ArrayList<>();
		for (SharedAlbum sharedAlbum : rSAlbums) {
			shared_album_map.put("album_name", sharedAlbum.getSharedAlbumName());
			shared_album_map.put("cover_addr", ImageLoader.genAddrOfSAlbumCover(sharedAlbum.getGroupId()));
			shared_album_map.put("group_id", sharedAlbum.getGroupId());
			List<Picture> picture_in_salbum = dao.getAllPictureInfo(user_id, sharedAlbum.getGroupId(),
					SouvenirsDAO.SHARED_ALBUM);
			personal_album_map.put("pictures_count", picture_in_salbum.size());
			shared_album_json = new JSONObject(shared_album_map);
			shared_album_json_list.add(shared_album_json.toString().replaceAll("'", "&apos;"));
		}
		result.put("SAlbum_json_list", shared_album_json_list);
		result.put("SAlbum_count", rSAlbums.size());
		// logger.debug("personal_album_json=<"+person_album_json_list+">");
		result.put("DispatchURL", "homepage.jsp");
		return result;
	}

	/**
	 * �״δ�makingҳ�棬��ɳ�ʼ����������(��һ��ѡ�е�album_list������ͼƬ�ĵ�ַjson��)����ʾ�Լ�����ģ�������
	 * 
	 * @param parameter
	 *            ǰ�˴����Ĳ�����key����login_user_id(��¼�û�user_id)��template(�û�ѡ���ģ������)
	 * @return ������ɷ��͸�ǰ�˵Ĳ���������album�����б�Ĭ����ʾ��album��ȫ��ͼƬ��ַ��json�ַ���
	 * @throws Exception
	 *             ��ȡ���ݿ���Ϣʧ�ܻ�store�ӿ�ִ��ʧ�ܻ��׳��쳣
	 */
	public Map<String, Object> displayMakingSouvenirs(Map<String, String> parameter) throws Exception {
		checkValidDAO();
		Map<String, Object> result = new HashMap<>();
		result.put("Avatar", ImageLoader.genAddrOfAvatar(parameter.get("login_user_id")));
		if (!parameter.containsKey("query_type")) {
			List<SharedAlbum> salbum_list = dao.getAllSAlbumInfo(parameter.get("login_user_id"),
					SouvenirsDAO.SHARED_ALBUM); // dao.getAlbumName(parameter.get("login_user_id"));
			List<PersonalAlbum> palbum_list = dao.getAllPAlbumInfo(parameter.get("login_user_id"),
					SouvenirsDAO.PERSONAL_ALBUM);
			logger.debug("palbum_list:" + palbum_list);
			List<String> album_name_list = new ArrayList<>();
			JSONArray palbum_identifier_json = new JSONArray();
			JSONArray salbum_identifier_json = new JSONArray();
			for (PersonalAlbum personalAlbum : palbum_list) {
				album_name_list.add(personalAlbum.getAlbumName());
				palbum_identifier_json.put(personalAlbum.getAlbumName());
			}
			for (SharedAlbum sharedAlbum : salbum_list) {
				album_name_list.add(sharedAlbum.getSharedAlbumName());
				salbum_identifier_json.put(sharedAlbum.getGroupId());
			}

			result.put("Album_name_list", album_name_list);
			result.put("PAlbum_identifier_json", palbum_identifier_json.toString());
			result.put("SAlbum_identifier_json", salbum_identifier_json.toString());

			parameter.put("album_identifier", palbum_identifier_json.getString(0));
			parameter.put("range", "personal");
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
	 * ��ȡ�ض�����е�����ͼƬ��ַ�������json�ַ���������json�ķ���
	 * 
	 * @see org.json
	 * @param parameter
	 *            ǰ�˴����Ĳ�����key����login_user_id(��¼���û�ID), album_identifier(����ʶ�����������ָ��������������ָС����)��
	 *            range(��ѯ��Χ������ֵ��"personal":������ᡢ"shred":������ᡢ"all":ȫ�����)
	 * @return ���������ͼƬ���ֺ͵�ַ����ɵ�json�ַ��� (���磺[{UserID: "A1", AlbumName: "B1", Filename: "C1", Addr:"D1", "Username":"E1",
	 *         "Description":"F1"�� "UploadTime":"G1"}, {UserID: "A2", AlbumName: "B2", Filename: "C2", Addr:"D2",
	 *         "Username":"E2", "Description":"F2"�� "UploadTime":"G2"}, ...])
	 */
	public String getImageAddrInAlbum(Map<String, String> parameter) {
		checkValidDAO();
		// Obtain image address list in specific album
		// image_list is unrefined result set from DB and image_addr_list is refined result set which can be used to
		// form json string
		List<Picture> image_list = null;
		int range = -1;
		if (parameter.get("range").contentEquals("personal"))
			range = SouvenirsDAO.PERSONAL_ALBUM;
		else if (parameter.get("range").contentEquals("shared"))
			range = SouvenirsDAO.SHARED_ALBUM;
		else if (parameter.get("range").contentEquals("all"))
			range = SouvenirsDAO.ALL_ALBUM;
		else
			range = -1;

		try {
			image_list = dao.getAllPictureInfo(parameter.get("login_user_id"), parameter.get("album_identifier"),
					range);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("There are something wrong when getting image address in album <"
					+ parameter.get("login_user_id") + ">, (User ID:<" + parameter.get("album_identifier") + ">)", e);
		}
		// logger.debug("image_list: " + image_list);
		JSONArray json_array = new JSONArray();
		Map<String, String> image_content = null;
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Picture image_item : image_list) {
			// Form json object of filename and address
			image_content = new HashMap<>();
			image_content.put("UserID", image_item.getUserId());
			image_content.put("AlbumName", image_item.getAlbumName());
			image_content.put("Filename", image_item.getFilename());
			image_content.put("Addr", ImageLoader.genAddrOfPicture(image_item.getUserId(), image_item.getAlbumName(),
					image_item.getFilename()));
			image_content.put("Username", UserManager.getUsernameByID(image_item.getUserId()));
			image_content.put("Description", image_item.getDescription());
			image_content.put("UploadTime", sdf.format(image_item.getUploadTimestamp()));
			json_array.put(image_content);
		}
		logger.debug("json:" + json_array);
		return json_array.toString();
	}

	/**
	 * ��ȡ��ز�����ʾ��Personal Album����ҳ��(album.jsp)
	 * 
	 * @param parameter
	 *            ǰ�˴����Ĳ����б�key����login_user_id(��¼�û���ID)��album_name(Ҫ��ȡ��������)
	 * @return ����ǰ�˵Ĳ����б������Ƿ�Ϊ�������ı�־λ(key=Is_personal��Ϊtrue)���û�ͷ���ַ(key=Avatar)����������ĵ�ַ(key=Album_cover)������������(key=
	 *         Album_name)�� ӵ���ߵ�����(key=Owner_name)��������(key=Description)�����������ͼƬJSON�ַ�����ɵ�List�б�(key=image_json_list)��
	 *         ��ת����ҳ��(key=DispatchURL)<br>
	 *         image_json_list��ʽ�ο�getImageAddrInAlbum����
	 * @throws Exception
	 *             ��ȡ���ݿ���Ϣʧ�ܻ�store�ӿ�ִ��ʧ�ܻ��׳��쳣
	 * @see souvenirs.web.SouvenirsManager#getImageAddrInAlbum(Map)
	 */
	public Map<String, Object> displayPAlbumManager(Map<String, String> parameter) throws Exception {
		checkValidDAO();
		Map<String, Object> result = new HashMap<>();
		String user_id = parameter.get("login_user_id");
		String album_name = parameter.get("album_name");
		parameter.put("album_identifier", album_name);
		parameter.put("range", "personal");
		if (user_id == null || album_name == null || user_id.isEmpty() || album_name.isEmpty())
			throw new BadRequestException("Invalid Parameter user_id OR album_name");
		result.put("Is_personal", true);
		result.put("Avatar", ImageLoader.genAddrOfAvatar(user_id));

		result.put("Album_cover", ImageLoader.genAddrOfPAlbumCover(user_id, album_name));
		result.put("Album_name", album_name);
		result.put("Owner_name", UserManager.getUsernameByID(user_id));
		result.put("Description", dao.getPAlbumInfo(user_id, album_name).getIntro());
		logger.debug(parameter.get("update"));
		if (parameter.containsKey("update") && parameter.get("update").contentEquals("true"))
			result.put("Update", true);
		JSONArray jArray = new JSONArray(getImageAddrInAlbum(parameter));
		List<String> image_list = new ArrayList<>();
		for (int i = 0; i < jArray.length(); i++) {
			image_list.add(jArray.getJSONObject(i).toString());
		}
		// logger.debug("image_json_list: " + image_list);
		result.put("image_json_list", image_list);
		result.put("DispatchURL", "album.jsp");
		return result;
	}

	/**
	 * ��ȡ��ز�����ʾ��Shared Album����ҳ��(album.jsp)
	 * 
	 * @param parameter
	 *            ǰ�˴����Ĳ����б�key����login_user_id(��¼�û���ID)��group_id(Ҫ��ȡ�������������С��ID)
	 * @return ����ǰ�˵Ĳ����б������Ƿ�Ϊ�������ı�־λ(key=Is_personal��Ϊfalse)���û�ͷ���ַ(key=Avatar)����������ĵ�ַ(key=Album_cover)��
	 *         ����������(key=Album_name)��ӵ���ߵ�����(key=Owner_name)��������(key=Description)�����������ͼƬJSON�ַ�����ɵ�List�б�(key=
	 *         image_json_list)�� ��ת����ҳ��(key=DispatchURL)<br>
	 *         image_json_list��ʽ�ο�getImageAddrInAlbum����
	 * @throws BadRequestException
	 *             store�ӿ�ִ��ʧ�ܻ��׳��쳣
	 * @throws Exception
	 *             ��ȡ���ݿ���Ϣʧ��ʱ���׳��쳣
	 */
	public Map<String, Object> displaySAlbumManager(Map<String, String> parameter)
			throws BadRequestException, Exception {
		checkValidDAO();
		Map<String, Object> result = new HashMap<>();
		String user_id = parameter.get("login_user_id");
		String group_id = parameter.get("group_id");
		parameter.put("album_identifier", group_id);
		parameter.put("range", "shared");
		if (user_id == null || group_id == null || user_id.isEmpty() || group_id.isEmpty())
			throw new BadRequestException("Invalid Parameter user_id OR group_id");
		result.put("Is_personal", false);
		result.put("Avatar", ImageLoader.genAddrOfAvatar(user_id));
		result.put("Group_id", group_id);

		Group group = dao.getSAlbumInfo(group_id);
		result.put("Album_cover", ImageLoader.genAddrOfSAlbumCover(group_id));
		result.put("Album_name", group.getSharedAlbumName());
		result.put("Owner_name", group.getGroupName());
		result.put("Description", group.getIntro());

		JSONArray jArray = new JSONArray(getImageAddrInAlbum(parameter));
		List<String> image_list = new ArrayList<>();
		for (int i = 0; i < jArray.length(); i++) {
			image_list.add(jArray.getJSONObject(i).toString());
		}
		// logger.debug("image_json_list: " + image_list);
		result.put("image_json_list", image_list);
		result.put("DispatchURL", "album.jsp");
		return result;
	}

	/**
	 * Personal Picture����ҳ�����ʾ����ͼƬ��Ϣ����ҳ��(picture.jsp)
	 * 
	 * @param parameter
	 *            ǰ�˴����Ĳ���������ָ��ͼƬ��key����login_user_id(��¼�û�ID)��album_name(�����)��picture_name(��Ƭ��)��
	 *            user_id(ҳ����ʾ��Ƭ���û������ڲ鿴���������Ƭ������´���)
	 * @return ����ǰ�˵�Map�������û���(key=Username)�������(key=Album_name)����Ƭ��(key=Picture_name)����Ƭ��ַ(key=Picture)��
	 *         ��ʽ(key=Format)�����(key=Description)�����۵�json�ַ�����ɵ��б�(key=Comment_json_list)������С���Ƿ�ӵ�б�����Ƭ��json�ַ����б�(key=
	 *         SAlbum_own_pic_json_list)��
	 *         �����û��б�(key=Liking_person_json)����תҳ��(key=DispatchURL)��ӵ���ߵ��û���(key=Owner)���Ƿ�Ϊ���������Ƭ�ı�־(key=Is_personal)��
	 *         <p>
	 *         Comment_json_list��ÿ����Ա����һ��JSON����������������ߵ��û���(key=comment_username)�������ߵ�ͷ��(key=comment_user_avatar)��
	 *         ��������(key=comment_content)������ʱ��(key=comment_time)�����������ظ������۵��������û���(key=reply_username)<br>
	 *         SAlbum_own_pic_json_list��ÿ����Ա����һ��JSON�����������������������(key=salbum_name)���Ƿ�ӵ�б�����Ƭ�ı�־(key=is_shared)
	 *         </p>
	 * @throws BadRequestException
	 *             ǰ�˷����Ĳ�����Чʱ�׳��쳣
	 * @throws Exception
	 *             ��ȡ���ݿ���Ϣʧ�ܻ�store�ӿ�ִ��ʧ�ܻ��׳��쳣
	 */
	public Map<String, Object> displayPictureManager(Map<String, String> parameter)
			throws BadRequestException, Exception {
		checkValidDAO();
		Map<String, Object> result = new HashMap<>();
		String album_name = parameter.get("album_name");
		String picture_name = parameter.get("picture_name");
		String user_id = parameter.get("user_id") == null ? parameter.get("login_user_id") : parameter.get("user_id");
		if (album_name == null || picture_name == null || album_name.isEmpty() || picture_name.isEmpty())
			throw new BadRequestException("Invalid Parameter album_name OR picture_name");

		// Basic parameters without SQL query
		result.put("Is_personal", true);
		result.put("Picture_user_id", user_id);
		result.put("Login_user_id", user_id);
		result.put("Username", UserManager.getUsernameByID(user_id));
		result.put("Owner", UserManager.getUsernameByID(user_id));
		result.put("Album_name", album_name);
		result.put("Picture_name", picture_name.substring(0, picture_name.lastIndexOf('.')));
		result.put("Picture", ImageLoader.genAddrOfPicture(user_id, album_name, picture_name));
		result.put("Avatar", ImageLoader.genAddrOfAvatar(user_id));

		// Advanced parameters with SQL query
		Picture pic = dao.getPictureInfo(user_id, album_name, picture_name);
		if (pic == null)
			throw new BadRequestException("Cannot find picture with user_id(<" + user_id + ">) album_name(<"
					+ album_name + ">) OR picture_name(<" + picture_name + ">)");
		result.put("Format", pic.getFormat());
		result.put("Description", pic.getDescription());

		// Query comment of picture and format json string
		List<Comment> comments = dao.getAllComments(user_id, album_name, picture_name);
		List<String> comment_json_list = new ArrayList<>();
		JSONObject jsonObject = null;
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Comment comment : comments) {
			jsonObject = new JSONObject();
			// logger.debug("comment_user_id:"+comment.getCommentUserId());
			jsonObject.put("comment_id", comment.getCommentId());
			jsonObject.put("comment_username", UserManager.getUsernameByID(comment.getCommentUserId()));
			jsonObject.put("comment_user_avatar", ImageLoader.genAddrOfAvatar(comment.getCommentUserId()));
			jsonObject.put("comment_content", comment.getCommentContent());
			jsonObject.put("comment_time", sdf.format(comment.getTime()));
			jsonObject.put("is_valid", comment.getIsValid() == 1 ? true : false);
			jsonObject.put("replied_comment_id", comment.getRepliedCommentId());
			comment_json_list.add(jsonObject.toString());
		}
		result.put("Comment_json_list", comment_json_list);
		// logger.debug("comment_json_list:"+comment_json_list);

		// Query share status of album and format json string
		List<SharedAlbum> sAlbums = dao.getAllSAlbumInfo(user_id, SouvenirsDAO.SHARED_ALBUM);
		List<String> picSAlbums = dao.getPictureBelongGroup(user_id, album_name, picture_name);
		JSONObject own_json_item = null;
		List<String> salbum_own_pic_json_list = new ArrayList<>();
		for (SharedAlbum sharedAlbum : sAlbums) {
			own_json_item = new JSONObject();
			own_json_item.put("group_id", sharedAlbum.getGroupId());
			own_json_item.put("salbum_name", sharedAlbum.getSharedAlbumName());
			if (picSAlbums.contains(sharedAlbum.getGroupId())) {
				own_json_item.put("is_shared", true);
			} else {
				own_json_item.put("is_shared", false);
			}
			salbum_own_pic_json_list.add(own_json_item.toString());
		}
		// logger.debug("salbum_own_pic_json_list:"+salbum_own_pic_json_list);
		result.put("SAlbum_own_pic_json_list", salbum_own_pic_json_list);

		// Query liking status and format json string
		List<String> liking_person_list = dao.getLikingPersons(user_id, album_name, picture_name);
		JSONArray liking_person_json = new JSONArray(liking_person_list);
		result.put("Liking_person_json", liking_person_json);
		// logger.debug("liking_person_json:"+liking_person_json);
		if (parameter.get("success_msg") != null) {
			JSONArray success_msg = new JSONArray(URLDecoder.decode(parameter.get("success_msg"), "UTF-8"));
			result.put("Success_msg", success_msg.toString());
		} else
			result.put("Success_msg", new JSONArray().toString());
		if (parameter.get("failure_msg") != null) {
			JSONArray failure_msg = new JSONArray(URLDecoder.decode(parameter.get("failure_msg"), "UTF-8"));
			result.put("Failure_msg", failure_msg.toString());
		} else
			result.put("Failure_msg", new JSONArray().toString());
		result.put("DispatchURL", "picture.jsp");
		return result;
	}

	/**
	 * ��ʾ�����������Ƭ������
	 * 
	 * @param parameter
	 *            ǰ�˴����Ĳ���������ָ��ͼƬ��key����login_user_id(��¼�û�ID)��album_name(�����)��picture_name(��Ƭ��)�� user_id(ҳ����ʾ��Ƭ���û���)
	 * @return ����ǰ�˵�Map�������û���(key=Username)�������(key=Album_name)����Ƭ��(key=Picture_name)����Ƭ��ַ(key=Picture)��
	 *         ��ʽ(key=Format)�����(key=Description)�����۵�json�ַ�����ɵ��б�(key=Comment_json_list)������С���Ƿ�ӵ�б�����Ƭ��json�ַ����б�(key=
	 *         SAlbum_own_pic_json_list)��
	 *         �����û��б�(key=Liking_person_json)����תҳ��(key=DispatchURL)��ӵ���ߵ��û���(key=Owner)���Ƿ�Ϊ���������Ƭ�ı�־(key=Is_personal)��
	 *         ��¼�û����û���(key=Username)��С��ID(key=Group_id)��С������(key=Group_name)
	 *         <p>
	 *         Comment_json_list��ÿ����Ա����һ��JSON����������������ߵ��û���(key=comment_username)�������ߵ�ͷ��(key=comment_user_avatar)��
	 *         ��������(key=comment_content)������ʱ��(key=comment_time)�����������ظ������۵��������û���(key=reply_username)<br>
	 *         SAlbum_own_pic_json_list��ÿ����Ա����һ��JSON�����������������������(key=salbum_name)���Ƿ�ӵ�б�����Ƭ�ı�־(key=is_shared)
	 *         </p>
	 * @throws BadRequestException
	 *             ǰ�˷����Ĳ�����Чʱ�׳��쳣
	 * @throws Exception
	 *             ��ȡ���ݿ���Ϣʧ�ܻ�store�ӿ�ִ��ʧ�ܻ��׳��쳣
	 */
	public Map<String, Object> displaySPictureManager(Map<String, String> parameter)
			throws BadRequestException, Exception {
		// Picture manager of shared album is similar to one of personal album, therefore query picture details should
		// reuse the method of personal picture
		// and then modify some parameters of displaying such as album name and share album list
		Map<String, Object> result = displayPictureManager(parameter);
		result.put("Is_personal", false);
		result.put("Login_user_id", parameter.get("login_user_id"));
		result.put("Username", UserManager.getUsernameByID(parameter.get("login_user_id")));
		String group_id = parameter.get("group_id");
		if (group_id == null || group_id.isEmpty())
			throw new BadRequestException("Invalid Parameter album_name OR picture_name");
		result.put("Group_id", parameter.get("group_id"));
		Group group = dao.getSAlbumInfo(group_id);
		result.put("Group_name", group.getSharedAlbumName());
		result.put("SAlbum_own_pic_json_list", new ArrayList<>());
		return result;
	}

	/**
	 * ��ʾԭʼ��С��Ƭ�Ĵ�����,ֻ������е���Ƭ�ſ��Ա���ȡ����ʾ��ͼ
	 * 
	 * @param para
	 *            ������key����addr(��Ƭ�����ص�ַ��method����)��content(Image��ַ�е�content)
	 * @return ����ǰ�˵Ĳ���������ԭʼ��С��Ƭ�ĵ�ַ(key=Picture)����ת��ҳ��(key=DispatchURL)����Ƭ��(key=Picture_name)��ҳ�����(key=Title)
	 * @see tool.ImageLoader
	 */
	public Map<String, Object> showPicture(Map<String, String> para) {
		// TODO Auto-generated method stub
		// The reason for using picture address instead of primary keys is to avoid SQL query with low efficiency
		Map<String, Object> result = new HashMap<>();
		String addr = para.get("addr");
		String[] addr_split = addr.split("&|\\?");
		String method = addr_split[1].substring(addr_split[1].indexOf('=') + 1);
		// logger.debug(addr+para.get("content"));
		String content = para.get("content");
		// Only pictures in albums can be displayed with origin size
		if (method.contentEquals("direct")) {
			// Picture original address
			result.put("Picture", addr + "&content=" + content);
			result.put("DispatchURL", "showPicture.jsp");
			// Decode original address to obtain its filename
			String[] img_url_para = Base64.decode(content).split("/");
			result.put("Picture_name", img_url_para[OWNER_FILENAME]);
			result.put("Title", img_url_para[OWNER_FILENAME]);
		} else {
			result.put("DispatchURL", "showPicture.jsp");
		}
		return result;
	}

	public Map<String, Object> displaySharePicture(Map<String, String> parameter)
			throws BadRequestException, Exception {
		checkValidDAO();
		String user_id = parameter.get("login_user_id");
		Map<String, Object> result = new HashMap<>();
		result.put("DispatchURL", "share.jsp");
		result.put("Avatar", ImageLoader.genAddrOfAvatar(user_id));
		String group_id = parameter.get("group_id");
		if (group_id == null || group_id.isEmpty())
			throw new BadRequestException("Invalid parameter group_id=null or group is empty.");

		try {
			List<PersonalAlbum> album_list = dao.getAllPAlbumInfo(user_id, SouvenirsDAO.PERSONAL_ALBUM);
			List<String> album_name_list = new ArrayList<>();
			for (PersonalAlbum pAlbum : album_list) {
				album_name_list.add(pAlbum.getAlbumName());
			}
			result.put("Album_name_list", album_name_list);

			if (album_name_list.size() == 0)
				throw new Exception("No album for user <" + user_id + ">");
			Map<String, String> para = new HashMap<>();
			para.put("login_user_id", user_id);
			para.put("album_identifier", album_name_list.get(0));
			para.put("range", "true");
			logger.debug("para:" + para);
			/*
			 * JSONArray jArray = new JSONArray(getImageAddrInAlbum(para)); List<String> image_json_list = new
			 * ArrayList<>(); for (int i = 0; i < jArray.length(); i++) {
			 * image_json_list.add(jArray.getJSONObject(i).toString()); }
			 */
			result.put("image_list_json", getImageAddrInAlbum(para));

			result.put("Group_id", group_id);
			Group sAlbum = dao.getSAlbumInfo(group_id);
			result.put("SAlbum_name", sAlbum.getSharedAlbumName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	public Map<String, Object> updatePictureInfo(Map<String, String> parameter) throws Exception {
		checkValidDAO();
		logger.debug("parameters:" + parameter);
		String user_id = parameter.get("login_user_id");
		String format = parameter.get("format");
		String origin_picture_name = parameter.get("original_picture_name") + "." + format;
		String picture_name = parameter.get("picture_name") + "." + format;
		String album_name = parameter.get("album_name");
		String original_description = parameter.get("original_description");
		String description = parameter.get("description");
		String salbum_json = parameter.get("salbum_json");
		Map<String, Object> result = new HashMap<>();
		//Picture pic = null;
		List<String> success_result = new ArrayList<>();
		List<String> failure_result = new ArrayList<>();
		try {
			// pic = dao.getPictureInfo(user_id, album_name, origin_picture_name);
		} catch (Exception e) {
			// Picture to be updated cannot be found.
			// update_result.add("Picture to be updated cannot be found.");
			logger.warn("Picture to be updated cannot be found. Parameters: user_id=<" + user_id + ">, album_name=<"
					+ album_name + ">, " + "filename=<" + origin_picture_name + ">");
			// result.put("result_list", URLEncoder.encode((new JSONArray(update_result)).toString(), "UTF-8"));
			throw e;
		}
		try {
			if (!picture_name.contentEquals(origin_picture_name)) {
				String album_path = BASE_PATH + File.separator + user_id + File.separator + album_name;
				// Rename picture first
				if (!FileOper.rename(album_path + File.separator + origin_picture_name,
						album_path + File.separator + picture_name)) {
					// Renaming failed, throw exception
					throw new RenameFolderErrorException(
							"Cannot rename picture from " + origin_picture_name + " to " + picture_name);
				}
				// Renaming succeeded then update record in database(table picture)
				boolean rs = dao.updatePictureName(user_id, album_name, origin_picture_name, picture_name);
				if (rs) {
					// Updating table succeeded
					success_result.add("picture name");
					logger.info("Updating picture name succeed. Parameters: user_id=<" + user_id + ">, album_name=<"
							+ album_name + ">, " + "previous filename=<" + origin_picture_name + ">, current filename=<"
							+ picture_name + ">");
				} else {
					// Updating table failed
					failure_result.add("picture name");
					logger.info("Updating picture name failed. Parameters: user_id=<" + user_id + ">, album_name=<"
							+ album_name + ">, " + "previous filename=<" + origin_picture_name + ">, current filename=<"
							+ picture_name + ">");
				}
			}
		} catch (RenameFolderErrorException e) {
			// Renaming failure exception is captured, just print error message
			failure_result.add("picture name: " + e.getMessage().substring(0, Math.min(100, e.getMessage().length())));
			logger.warn("Updating picture name failed since renaming failure. Parameters: user_id=<" + user_id
					+ ">, album_name=<" + album_name + ">, " + "previous filename=<" + origin_picture_name
					+ ">, current filename=<" + picture_name + ">", e);
		} catch (Exception e) {
			// Non-renaming failure exception is captured, undo renaming operation as well as print error message
			String album_path = BASE_PATH + File.separator + user_id + File.separator + album_name;
			if (!FileOper.rename(album_path + File.separator + picture_name,
					album_path + File.separator + origin_picture_name)) {
				// Undo renaming failed, this means system error occurred, so throw exception
				logger.error(
						"Undo renaming picture failed, which leads to inconsistence between database and file system. Parameters: "
								+ parameter);
				throw e;
			}
			failure_result.add("picture name: " + e.getMessage().substring(0, Math.min(100, e.getMessage().length())));
			logger.warn("Updating picture name failed. Parameters: user_id=<" + user_id + ">, album_name=<" + album_name
					+ ">, " + "previous filename=<" + origin_picture_name + ">, current filename=<" + picture_name
					+ ">", e);
		}

		try {
			if (!description.contentEquals(original_description)) {
				int rs = dao.updatePictureDescription(user_id, album_name, picture_name, description);
				if (rs == DEFAULT_AFFECTED_ROW) {
					success_result.add("description");
					logger.info(
							"Updating picture description succeed. Parameters: user_id=<" + user_id + ">, album_name=<"
									+ album_name + ">, " + "filename=<" + picture_name + ">, previous description=<"
									+ original_description + ">, current description=<" + description + ">");
				} else {
					failure_result.add("description");
					logger.info(
							"Updating picture description succeed. Parameters: user_id=<" + user_id + ">, album_name=<"
									+ album_name + ">, " + "filename=<" + picture_name + ">, previous description=<"
									+ original_description + ">, current description=<" + description + ">");
				}
			}
		} catch (Exception e) {
			failure_result.add("description: " + e.getMessage().substring(0, Math.min(100, e.getMessage().length())));
			logger.warn("Updating picture description succeed. Parameters: user_id=<" + user_id + ">, album_name=<"
					+ album_name + ">, " + "filename=<" + picture_name + ">, previous description=<"
					+ original_description + ">, current description=<" + description + ">", e);
		}

		try {
			List<String> original_shared_group = dao.getPictureBelongGroup(user_id, album_name, picture_name);
			JSONArray salbum_share_list = new JSONArray(salbum_json);
			for (int i = 0; i < salbum_share_list.length(); i++) {
				JSONObject salbum_item = salbum_share_list.getJSONObject(i);
				if (salbum_item.getBoolean("is_shared") != original_shared_group
						.contains(salbum_item.getString("group_id"))) {
					if (salbum_item.getBoolean("is_shared")) {
						int rs = dao.sharePicture(user_id, album_name, picture_name, salbum_item.getString("group_id"));
						if (rs == SouvenirsDAO.SHARE_PICTURE_SUCCESS) {
							success_result.add("sharing state of " + salbum_item.getString("salbum_name"));
							logger.info("Sharing picture succeed. Parameters: user_id=<" + user_id + ">, album_name=<"
									+ album_name + ">, " + "filename=<" + picture_name + ">, group id=<"
									+ salbum_item.getString("group_id") + ">");
						} else if (rs == SouvenirsDAO.SHARE_PICTURE_DUPLICATE) {
							failure_result.add("sharing " + salbum_item.getString("salbum_name")
									+ " since this picture has already existed.");
							logger.error("Sharing picture failed since duplicated picture. Parameters: user_id=<"
									+ user_id + ">, album_name=<" + album_name + ">, " + "filename=<" + picture_name
									+ ">, group id=<" + salbum_item.getString("group_id") + ">");
						} else {
							failure_result.add("sharing " + salbum_item.getString("salbum_name"));
							logger.info("Sharing picture failed. Parameters: user_id=<" + user_id + ">, album_name=<"
									+ album_name + ">, " + "filename=<" + picture_name + ">, group id=<"
									+ salbum_item.getString("group_id") + ">");
						}
					} else {
						int rs = dao.unsharePicture(user_id, album_name, picture_name,
								salbum_item.getString("group_id"));
						if (rs == DEFAULT_AFFECTED_ROW) {
							success_result.add("unsharing state of " + salbum_item.getString("salbum_name"));
							logger.info("Unsharing picture succeed. Parameters: user_id=<" + user_id + ">, album_name=<"
									+ album_name + ">, " + "filename=<" + picture_name + ">, group id=<"
									+ salbum_item.getString("group_id") + ">");
						} else {
							failure_result.add("unsharing " + salbum_item.getString("salbum_name"));
							logger.info("Unsharing picture failed. Parameters: user_id=<" + user_id + ">, album_name=<"
									+ album_name + ">, " + "filename=<" + picture_name + ">, group id=<"
									+ salbum_item.getString("group_id") + ">");
						}
					}
				}
			}
		} catch (Exception e) {
			failure_result.add(
					"changing sharing status: " + e.getMessage().substring(0, Math.min(100, e.getMessage().length())));
			logger.info("Unsharing picture succeed. Parameters: user_id=<" + user_id + ">, album_name=<" + album_name
					+ ">, " + "filename=<" + picture_name + ">", e);
		}
		result.put("Is_redirect", true);
		String url = "/Souvenirs/picture?album_name=" + URLEncoder.encode(album_name, "UTF-8") + "&picture_name="
				+ URLEncoder.encode(picture_name, "UTF-8") + "&success_msg="
				+ URLEncoder.encode((new JSONArray(success_result).toString()), "UTF-8") + "&failure_msg="
				+ URLEncoder.encode((new JSONArray(failure_result).toString()), "UTF-8");

		result.put("DispatchURL", url);
		return result;
	}

	public String createPAlbum(Map<String, String> parameter, FileItem file_handle) throws Exception {
		checkValidDAO();
		String result = "";

		// Prepare parameters for querying album name and adding image
		String user_id = parameter.get("login_user_id");
		String album_name = parameter.get("album_name");
		String origin_filename = parameter.get("filename");
		String format = origin_filename.substring(origin_filename.lastIndexOf(".") + 1);
		String description = parameter.get("description");
		String default_cover = parameter.get("default_cover");
		String cover = "";
		if (default_cover == null || !default_cover.contentEquals("on"))
			cover = File.separator + user_id + File.separator + album_name + File.separator + origin_filename;
		else
			cover = "\\\\res\\\\default_cover.png";
		
		try {
			// Add a row of image into DB
			int sql_exec_result = dao.createPAlbum(user_id, album_name, description, cover.replaceAll("\\\\", "\\\\\\\\"));
			
			if (sql_exec_result != 1)
				throw new Exception("Uploading failed. Cannot insert new record into database.");
			
			// Insertion into database succeeded
			if (default_cover ==null || !default_cover.contentEquals("on")) {
				// Form absolute file path
				String uploadPath = PropertyOper.GetValueByKey("souvenirs.properties", "data_path") + File.separator+user_id+File.separator+album_name;

				// Create path if it does not exist
				File uploadDir = new File(uploadPath);
				logger.debug("upload_path:" + uploadDir.getPath() + " " + uploadDir.exists());
				if (!uploadDir.exists()) {
					logger.debug(uploadDir.mkdirs());
				}

				// System.getProperties().list(System.out);
				// Create new file
				File storeFile = new File(uploadPath+File.separator+origin_filename);
				logger.debug(uploadPath);

				// Save image to disk
				try {
					file_handle.write(storeFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// Delete added row of image if there is exception when writing image
					if (dao.deletePAlbum(user_id, album_name) == DEFAULT_AFFECTED_ROW) {
						logger.info("User(id=<" + user_id	+ ">) deleted the new personal album since there are something wrong when writing cover.");
					} else {
						// Deletion failed, there is uniformity between database and file data
						logger.error("User(id=<" + user_id + ">) failed to delete the new personal album database record "
								+ "although there are something wrong when writing cover, which leads to inconsistency in database!");
					}
					// Set error message and quit
					//result = displayContent(parameter);
					//result.put("Upload_result", e.getMessage());
					result = "/Souvenirs/homepage?Upload_result="+URLEncoder.encode(e.getMessage(), "UTF-8").substring(0, Math.min(e.getMessage().length(), 800));
					logger.info("User failed to create a personal album, error:<" + e.getMessage()
							+ "> with parameters:<" + parameter + ">");
					return result;
				}
				// Insert cover image as the first image of new personal album
				int rs = dao.addPicture(user_id, album_name, origin_filename, format, "This is the cover of album.");
				if (rs != DEFAULT_AFFECTED_ROW) {
					logger.error("Cannot insert cover image record into database as the first image in the album. Parameters:<"+parameter+">");
					throw new Exception("Cannot insert cover image record into database as the first image in the album. Parameters:<"+parameter+">");
				} else {
					// Set success message (Cover assigned)
					//result = displayContent(parameter);
					//result.put("Upload_result", "true");
					result = "/Souvenirs/homepage?Upload_result=true";
					logger.info("User(id=" + parameter.get("login_user_id") + ") created a personal album. Parameters:<"
							+ parameter + ">");
				}
			} else {
				// Set success message (Default cover)
				//result = displayContent(parameter);
				//result.put("Upload_result", "true");
				result = "/Souvenirs/homepage?Upload_result=true";
				logger.info("User(id=" + parameter.get("login_user_id") + ") created a personal album. Parameters:<"
						+ parameter + ">");
			}
		} catch (Exception e) {
			// TODO: handle exception
			// Adding failed
			//result = displayContent(parameter);
			//result.put("Upload_result", e.getMessage());
			result = "/Souvenirs/homepage?Upload_result="+URLEncoder.encode(e.getMessage(), "UTF-8").substring(0, Math.min(e.getMessage().length(), 800));
			logger.info("User failed to create a personal album, error:<" + e.getMessage() + "> with parameters:<" + parameter
					+ ">");
		}
		return result;

	}
}
