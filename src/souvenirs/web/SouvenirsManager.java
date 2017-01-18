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
 * Souvenir��������ᡢ��Ƭ�����ҵ����
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
	 * �״δ�makingҳ�棬��ɳ�ʼ����������(��һ��ѡ�е�album_list������ͼƬ�ĵ�ַjson��)����ʾ�Լ�����ģ�������
	 * 
	 * @param parameter
	 *            ǰ�˴����Ĳ�����key����login_user_id(��¼�û�user_id)��template(�û�ѡ���ģ������)
	 * @return ������ɷ��͸�ǰ�˵Ĳ���������album�����б�Ĭ����ʾ��album��ȫ��ͼƬ��ַ��json�ַ���
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
	 * ��ȡ�ض�����е�����ͼƬ��ַ�������json�ַ���������json�ķ���
	 * 
	 * @see org.json
	 * @param parameter
	 *            ǰ�˴����Ĳ�����key����login_user_id(��¼���û�ID), album_name(�����)
	 * @return ���������ͼƬ���ֺ͵�ַ����ɵ�json�ַ���(���磺[{Filename: "A", Addr:"B"}, {Filename: "C", Addr:"D"}, ...])
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
	 * ��ȡ��ز�����ʾ��Album����ҳ��(album.jsp)
	 * 
	 * @param parameter
	 *            ǰ�˴����Ĳ����б�key����login_user_id(��¼�û���ID)��album_name(Ҫ��ȡ��������)
	 * @return ����ǰ�˵Ĳ����б������û�ͷ���ַ(key=Avatar)����������ĵ�ַ(key=Album_cover)������������(key=Album_name)��
	 *         ӵ���ߵ�����(key=Owner_name)��������(key=Description)�����������ͼƬJSON�ַ�����ɵ�List�б�(key=image_json_list)��
	 *         ��ת����ҳ��(key=DispatchURL)
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
	 * Picture����ҳ�����ʾ����ͼƬ��Ϣ����ҳ��
	 * @param parameter ǰ�˴����Ĳ���������ָ��ͼƬ��key����login_user_id(��¼�û�ID)��album_name(�����)��picture_name(��Ƭ��)
	 * @return ����ǰ�˵�Map������
	 * @throws BadRequestException ǰ�˷����Ĳ�����Чʱ�׳��쳣
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
