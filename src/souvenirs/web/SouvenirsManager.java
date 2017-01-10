package souvenirs.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import souvenirs.dao.SouvenirsDAO;
import tool.ImageLoader;
import tool.PropertyOper;

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
		//checkValidDAO();
	}
	
	/**
	 * 单例模式获取对象的方法
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
	 * 显示用户主页，获取相册的信息。<strong>注意：本方法未完成</strong>
	 * @param parameter 前端传来的参数
	 * @return 发回前端的显示参数
	 */
	public Map<String, Object> displayContent(Map<String, String> parameter) {
		checkValidDAO();
		Map<String, Object> result = new HashMap<>();
		List<String> para = new ArrayList<>();
		para.add("user");
		para.add(parameter.get("login_user_id"));
		logger.debug("Image Query Parameter: "+para);
		result.put("Avatar" ,ImageLoader.genImageQuery(false, para));
		result.put("DispatchURL", "homepage.jsp");
		return result;
	}

	/**
	 * 首次打开making页面，完成初始化部分内容(第一个选中的album_list所包含图片的地址json串)的显示
	 * @param parameter 前端传来的参数，key包括login_user_id(登录用户user_id)
	 * @return 操作完成发送给前端的参数，包括album名称列表、默认显示的album内全部图片地址的json字符串
	 */
	public Map<String, Object> makingSouvenirs(Map<String, String> parameter) {
		checkValidDAO();
		Map<String, Object> result = new HashMap<>();
		if (!parameter.containsKey("query_type")){
			List<Object> album_name_list = dao.getAlbumName(parameter.get("login_user_id"));
			result.put("Album_List", album_name_list);
			parameter.put("album_name", (String)album_name_list.get(0));
			result.put("Image_JSON", getImageAddrInAlbum(parameter));
			String template = PropertyOper.GetValueByKey("template.properties", parameter.get("template"));
			if (template==null || template.isEmpty())
				template = "[]";
			result.put("Template_json", template);
			result.put("DispatchURL", "canvas.jsp");
		}else {
			result.put("DispatchURL", "canvas.jsp");
		}
		return result;
	}
	
	/**
	 * 获取特定相册中的所有图片地址，并组成json字符串，构造json的方法
	 *  @see org.json 
	 * @param parameter 前端传来的参数，key包括login_user_id(登录的用户ID), album_name(相册名)
	 * @return 相册中所有图片名字和地址所组成的json字符串(形如：[{filename: "A", addr:"B"}, {filename: "C", addr:"D"}, ...])
	 */
	public String getImageAddrInAlbum(Map<String, String> parameter) {
		checkValidDAO();
		//Obtain image address list in specific album
		//image_list is unrefined result set from DB and image_addr_list is refined result set which can be used to form json string
		List<List<Object>> image_list = dao.getPictureAddrInAlbum(parameter.get("login_user_id"), parameter.get("album_name"));
		List<Map<String, String>> image_addr_list = new ArrayList<>();
		for (List<Object> list : image_list) {
			//Form parameters for generating image address
			List<String> para = new ArrayList<>();
			para.add((String)list.get(OWNER_ID));
			para.add((String)list.get(OWNER_ALBUM_NAME));
			para.add((String)list.get(OWNER_FILENAME));
			//Form json object of filename and address
			Map<String, String> image_content = new HashMap<>();
			image_content.put("Filename", (String)list.get(OWNER_FILENAME));
			image_content.put("Addr", ImageLoader.genImageQuery(true, para));
			image_addr_list.add(image_content);
		}
		//From json string from image_addr_list which stores json object of each image
		JSONArray json_array = new JSONArray();
		for (Map<String,String> map : image_addr_list) {
			json_array.put(map);
		}
		logger.debug("json:"+json_array);
		return json_array.toString();
	}
}
