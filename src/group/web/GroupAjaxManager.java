package group.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import group.Group;
import group.dao.GroupDAO;
import tool.PropertyOper;

public class GroupAjaxManager {
	private static GroupAjaxManager group_manager = new GroupAjaxManager();
	private static Logger logger = Logger.getLogger(GroupManager.class);
	private GroupDAO dao = null;
	private static String BASE_PATH = PropertyOper.GetValueByKey("souvenirs.properties", "data_path");

	/**
	 * 单例模式获取对象的方法
	 * 
	 * @return SouvenirsAjaxManager类的对象
	 */
	public static GroupAjaxManager getInstance() {
		return group_manager;
	}

	/**
	 * 维护DAO对象的可用性
	 */
	private void checkValidDAO() {
		if (dao == null)
			dao = GroupDAO.getInstance();
	}

	public String showMyGroup(Map<String, String>parameter) {
		checkValidDAO();
		
		String user_id = parameter.get("login_user_id");
		int page_number = 1;
		int start_pos = 0;
		int content_leng = 10;
		if (parameter.containsKey("page_number")) {
			logger.debug(Integer.parseInt(parameter.get("page_number")));
			page_number = Integer.parseInt(parameter.get("page_number"));

		}
		if (parameter.containsKey("content_length"))
			content_leng = Integer.parseInt(parameter.get("content_length"));
		
		start_pos = (page_number-1)*content_leng;
		
		JSONArray group_list_json = new JSONArray();
		try {
			List<Group> belong_group = dao.queryGroupByUserID(user_id, start_pos, content_leng);
			JSONObject group_object = null;
			for (Group group : belong_group) {
				group_object = new JSONObject();
				group_object.put("group_id", group.getGroupId());
				group_object.put("group_name", group.getGroupName());
				group_object.put("salbum_name", group.getSharedAlbumName());
				group_object.put("intro", group.getIntro());
				group_object.put("album_cover", group.getAlbumCover());
				group_object.putOnce("create_timestamp", group.getCreateTimestamp());
				group_list_json.put(group_object);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("group_list_json "+group_list_json.toString());
		return group_list_json.toString();
	}
}
