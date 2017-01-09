package souvenirs.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import souvenirs.dao.SouvenirsDAO;
import tool.ImageLoader;

public class SouvenirsManager {
	private static SouvenirsManager souvenirs_manager = new SouvenirsManager();
	//private Map<String, String> parameter = null;
	private static Logger logger = Logger.getLogger(SouvenirsManager.class);
	private SouvenirsDAO dao = null;
	final int OWNER_ID = 0;
	final int OWNER_ALBUM_NAME = 1;
	final int OWNER_FILENAME = 2;
	
	public SouvenirsManager() {
		dao = SouvenirsDAO.getInstance();
	}
	
	public static SouvenirsManager getInstance() {
		return souvenirs_manager;
	}
	
	/*
	 * 显示用户主页，获取相册的信息。未完成
	 */
	public Map<String, Object> displayContent(Map<String, String> parameter) {
		Map<String, Object> result = new HashMap<>();
		List<String> para = new ArrayList<>();
		para.add("user");
		para.add(parameter.get("login_user_id"));
		logger.debug("Image Query Parameter: "+para);
		result.put("Avatar" ,ImageLoader.genImageQuery(false, para));
		result.put("DispatchURL", "homepage.jsp");
		return result;
	}

	/*
	 * 首次打开making页面，完成初始化部分内容的显示
	 * @param 前端传来的参数
	 * @return 操作完成发送给前端的参数
	 */
	public Map<String, Object> makingSouvenirs(Map<String, String> parameter) {
		Map<String, Object> result = new HashMap<>();
		if (!parameter.containsKey("query_type")){
			List<Object> album_name_list = dao.getAlbumName(parameter.get("login_user_id"));
			result.put("Album_List", album_name_list);
			parameter.put("album_name", (String)album_name_list.get(0));
			result.put("Image_JSON", getImageAddrInAlbum(parameter));
			result.put("DispatchURL", "canvas.jsp");
		}else {
			result.put("DispatchURL", "canvas.jsp");
		}
		return result;
	}
	
	/*
	 * 获取特定相册中的所有图片地址，并组成json字符串
	 * @param parameter 前端传来的参数
	 * @return 相册中所有图片名字和地址所组成的json字符串
	 */
	public String getImageAddrInAlbum(Map<String, String> parameter) {
		List<List<Object>> image_list = dao.getPictureAddrInAlbum(parameter.get("login_user_id"), parameter.get("album_name"));
		List<Map<String, String>> image_addr_list = new ArrayList<>();
		for (List<Object> list : image_list) {
			List<String> para = new ArrayList<>();
			para.add((String)list.get(OWNER_ID));
			para.add((String)list.get(OWNER_ALBUM_NAME));
			para.add((String)list.get(OWNER_FILENAME));
			Map<String, String> image_content = new HashMap<>();
			image_content.put("Filename", (String)list.get(OWNER_FILENAME));
			image_content.put("Addr", ImageLoader.genImageQuery(true, para));
			image_addr_list.add(image_content);
		}
		JSONArray json_array = new JSONArray();
		for (Map<String,String> map : image_addr_list) {
			json_array.put(map);
		}
		logger.debug("json:"+json_array);
		return json_array.toString();
	}
}
