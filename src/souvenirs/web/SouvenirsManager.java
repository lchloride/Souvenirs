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
		//checkValidDAO();
	}
	
	/**
	 * ����ģʽ��ȡ����ķ���
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
	 * ��ʾ�û���ҳ����ȡ������Ϣ��<strong>ע�⣺������δ���</strong>
	 * @param parameter ǰ�˴����Ĳ���
	 * @return ����ǰ�˵���ʾ����
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
	 * �״δ�makingҳ�棬��ɳ�ʼ����������(��һ��ѡ�е�album_list������ͼƬ�ĵ�ַjson��)����ʾ
	 * @param parameter ǰ�˴����Ĳ�����key����login_user_id(��¼�û�user_id)
	 * @return ������ɷ��͸�ǰ�˵Ĳ���������album�����б�Ĭ����ʾ��album��ȫ��ͼƬ��ַ��json�ַ���
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
	 * ��ȡ�ض�����е�����ͼƬ��ַ�������json�ַ���������json�ķ���
	 *  @see org.json 
	 * @param parameter ǰ�˴����Ĳ�����key����login_user_id(��¼���û�ID), album_name(�����)
	 * @return ���������ͼƬ���ֺ͵�ַ����ɵ�json�ַ���(���磺[{filename: "A", addr:"B"}, {filename: "C", addr:"D"}, ...])
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
