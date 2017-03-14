package group.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import group.Group;
import group.dao.GroupDAO;
import tool.ImageLoader;

public class GroupManager {
	private static GroupManager group_manager = new GroupManager();
	private static Logger logger = Logger.getLogger(GroupManager.class);
	private GroupDAO dao = null;

	/**
	 * 单例模式获取对象的方法
	 * 
	 * @return GroupManager类的对象
	 */
	public static GroupManager getInstance() {
		return group_manager;
	}

	/**
	 * 维护DAO对象的可用性
	 */
	private void checkValidDAO() {
		if (dao == null)
			dao = GroupDAO.getInstance();
	}
	
	public Map<String, Object> displayGroupManager(Map<String, String> parameter) {
		checkValidDAO();
		String user_id = parameter.get("login_user_id");
		GroupAjaxManager gam = GroupAjaxManager.getInstance();
		
		int page_number = 1;
		//int start_pos = 0;
		int content_leng = 10;
		if (parameter.containsKey("page_number")) {
			logger.debug(Integer.parseInt(parameter.get("page_number")));
			page_number = Integer.parseInt(parameter.get("page_number"));

		}
		if (parameter.containsKey("content_length"))
			content_leng = Integer.parseInt(parameter.get("content_length"));
		
		//start_pos = (page_number-1)*content_leng;
		
		Map<String, Object> result = new HashMap<>();
		int total_number = content_leng;
		
		try {
			/*List<Group> belong_group = dao.queryGroupByUserID(user_id, start_pos, content_leng);
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
			}*/
			result.put("My_group_list_json", gam.showMyGroup(parameter));
			total_number = dao.getGroupNumberByUserID(user_id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.put("My_group_page_number", page_number);
		result.put("Content_length", content_leng);
		result.put("DispatchURL", "group.jsp");
		result.put("Avatar", ImageLoader.genAddrOfAvatar(user_id));
		result.put("My_group_total_items", total_number);
		
		return result;
	}
}
