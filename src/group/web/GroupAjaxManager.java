package group.web;

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
	private static Logger logger = Logger.getLogger(GroupAjaxManager.class);
	private GroupDAO dao = null;
	private static String BASE_PATH = PropertyOper.GetValueByKey("souvenirs.properties", "data_path");
	private final static int DEFAULT_AFFECTED_ROW = 1;
	
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
	
	public String updateGroupInfo(Map<String, String> parameter) {
		checkValidDAO();
		String user_id = parameter.get("login_user_id");
		String new_group_name = parameter.get("group_name");
		new_group_name = new_group_name.replaceAll("'", "&apos;");
		String new_intro = parameter.get("intro");
		new_intro = new_intro.replaceAll("'", "&apos;");
		String group_id = parameter.get("group_id");
		
		logger.debug("new_name="+new_group_name+", new_intro="+new_intro);
		
		JSONArray result_json = new JSONArray();
		try {
			List<Group> old_group = dao.queryGroupByUserIDGroupID(user_id, group_id);
			if (old_group.size() != DEFAULT_AFFECTED_ROW) {
				throw new Exception("No matched group is found!");
			} else {
				// Current group name is different from the original one, update it 
				if (!old_group.get(0).getGroupName().contentEquals(new_group_name)) { 
					int rs = dao.updateGroupName(group_id, new_group_name);
					if (rs == DEFAULT_AFFECTED_ROW) {
						logger.info("User updated group name. Parameters: user_id=<"+user_id+">, group_id=<"+group_id+">,"
								+ " old_group_name=<"+old_group.get(0).getGroupName()+">, new_group_name=<"+new_group_name+">");
						JSONObject jObject = new JSONObject();
						jObject.put("item", "group name");
						jObject.put("result", "true");
						result_json.put(jObject);
					}else{
						logger.warn("User failed to update group name. Parameters: user_id=<"+user_id+">, group_id=<"+group_id+">,"
								+ " old_group_name=<"+old_group.get(0).getGroupName()+">, new_group_name=<"+new_group_name+">, result_set=<"+rs+">");
						JSONObject jObject = new JSONObject();
						jObject.put("item", "group name");
						jObject.put("result", "Invalid updating result.");
						result_json.put(jObject);
					}
				}
				
				// Current group introduction is different from the original one, update it 
				if (!old_group.get(0).getIntro().contentEquals(new_intro)) { 
					int rs = dao.updateIntro(group_id, new_intro);
					if (rs == DEFAULT_AFFECTED_ROW) {
						logger.info("User updated group introduction. Parameters: user_id=<"+user_id+">, group_id=<"+group_id+">,"
								+ " old_intro=<"+old_group.get(0).getIntro()+">, new_intro=<"+new_intro+">");
						JSONObject jObject = new JSONObject();
						jObject.put("item", "introduction");
						jObject.put("result", "true");
						result_json.put(jObject);
					}else{
						logger.warn("User failed to update group nintroduction. Parameters: user_id=<"+user_id+">, group_id=<"+group_id+">,"
								+ " old_intro=<"+old_group.get(0).getIntro()+">, new_intro=<"+new_intro+">, result_set=<"+rs+">");
						JSONObject jObject = new JSONObject();
						jObject.put("item", "introduction");
						jObject.put("result", "Invalid updating result.");
						result_json.put(jObject);
					}
				}
	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("Updating group information failed since "+e.getMessage());
			return "[{\"item\": \"updating group\"}, {\"result\": \""+e.getMessage()+"\"}]";
		}
		if (result_json.length()==0) 
			return "[{\"item\":\"updating group\",\"result\":\"no item changed\"}]";
		else {
			String json = showMyGroup(parameter);
			result_json.put(json);
			return result_json.toString();
		}
	}
	
	public String leaveGroup(Map<String, String>parameter) {
		checkValidDAO();
		String user_id = parameter.get("login_user_id");
		String group_id = parameter.get("group_id");
		String result = "";
		
		try {
			int rs =dao.leaveGroup(user_id, group_id);
			if (rs == DEFAULT_AFFECTED_ROW) {
				logger.info("User left a group. Parameters: user_id=<"+user_id+">, group_id=<"+group_id+">");
				result = "true";
			} else {
				logger.warn("User failed to leave a group. Parameters: user_id=<"+user_id+">, group_id=<"+group_id+">");
				result = "Invalid operation result.";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("Fail to delete user-group relation since "+e.getMessage());
			return e.getMessage();
		}
		return result;
	}
}
