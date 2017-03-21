package group.web;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import group.Group;
import group.dao.GroupDAO;
import tool.ImageLoader;
import tool.PropertyOper;

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
		// int start_pos = 0;
		int content_leng = 10;
		if (parameter.containsKey("page_number")) {
			page_number = Integer.parseInt(parameter.get("page_number"));
		}
		if (parameter.containsKey("content_length"))
			content_leng = Integer.parseInt(parameter.get("content_length"));

		Map<String, Object> result = new HashMap<>();
		int total_number = content_leng;

		try {
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

	public Map<String, Object> createGroup(Map<String, String> parameters, FileItem file_handle) {
		checkValidDAO();
		String user_id = parameters.get("login_user_id");

		String group_name = parameters.get("group_name").replaceAll("'", "&apos;").replaceAll("\"", "&quot;");
		String description = parameters.get("description").replaceAll("'", "&apos;").replaceAll("\"", "&quot;");
		String default_cover = parameters.get("default_cover");
		String filename = parameters.get("filename");

		String salbum_name = parameters.get("salbum_name").replaceAll("'", "&apos;").replaceAll("\"", "&quot;");
		String cover = "";
		String rs = "";

		Map<String, Object> result = new HashMap<>();
		//logger.debug("default cover: " + default_cover);
		String format = filename.substring((filename.lastIndexOf(".") + 1));
		
		if (default_cover == null || !default_cover.contentEquals("on")) {// Specific
																			// cover

			try {
				rs = dao.createGroup(group_name, description, salbum_name, format);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.warn("An error occurred when creating new group. Error: " + e.getMessage());
				result.put("Error", e.getMessage());
			}
		} else
			try {
				rs = dao.createGroup(group_name, description, salbum_name);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.warn("An error occurred when creating new group. Error: " + e.getMessage());
				result.put("Error", e.getMessage());
			}

		if (rs != null && !rs.contentEquals("")) {
			if (default_cover == null || !default_cover.contentEquals("on")) {
				// Form absolute file path
				String uploadPath = PropertyOper.GetValueByKey("souvenirs.properties", "data_path") + File.separator
						+ "group";

				// Create path if it does not exist
				File uploadDir = new File(uploadPath);
				logger.debug("upload_path:" + uploadDir.getPath() + " " + uploadDir.exists());
				if (!uploadDir.exists()) {
					logger.debug(uploadDir.mkdirs());
				}

				// Create new file
				File storeFile = new File(uploadPath + File.separator + rs+"_cover."+format);
				logger.debug(uploadPath);

				// Save image to disk
				try {
					file_handle.write(storeFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					result.put("Error", "Fail to create cover image");
					logger.warn("Cannot create cover image. Parameters:  user_id=<" + user_id + ">, group_name=<"
							+ group_name + ">, " + "salbum_name=<" + salbum_name + ">, cover=<" + cover
							+ ">, is_deafult_cover=<" + default_cover + ">");
					result.put("Group_name", group_name);
					result.put("Description", description);
					result.put("SAlbum_name", salbum_name);
					result.put("DispatchURL", "group.jsp");
					result.put("Page", "create");
					return result;
				}
				try {
					if (dao.joininGroup(user_id, rs) != 1) {
						throw new Exception("Cannot Join new Group. Parameters: user_id=<"+user_id+">, group_id=<"+rs+">");
					}else {
						result.put("Success", "Success");
						result.put("DispatchURL", "group");
						result.put("Group_id", rs);
						result.put("Is_redirect", true);
						logger.info("User created a group. Parameters: user_id=<" + user_id + ">, group_name=<"
								+ group_name + ">, " + "salbum_name=<" + salbum_name + ">, cover=<" + cover
								+ ">, description=<"+description+">");						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					result.put("Error", "User failed to join in the new group. Please join in it manually. ");
					result.put("DispatchURL", "group");
					result.put("Page", "join");
					result.put("Group_id", rs);
					result.put("Is_redirect", true);
					logger.info("User created a group. Parameters: user_id=<" + user_id + ">, group_name=<"
							+ group_name + ">, " + "salbum_name=<" + salbum_name + ">, cover=<" + cover
							+ ">, description=<"+description+">");	
					logger.warn("User failed to join in the new group. Parameters: user_id=<"+user_id+">, group_id=<"+rs+">");
				}

			} else {
				try {
					if (dao.joininGroup(user_id, rs) != 1) {
						throw new Exception("Cannot Join new Group. Parameters: user_id=<"+user_id+">, group_id=<"+rs+">");
					}else {
						result.put("Success", "Success");
						result.put("DispatchURL", "group");
						result.put("Group_id", rs);
						result.put("Is_redirect", true);
						logger.info("User created a group. Parameters: user_id=<" + user_id + ">, group_name=<"
								+ group_name + ">, " + "salbum_name=<" + salbum_name
								+ ">, is_deafult_cover=<" + default_cover + ">, description=<"+description+">");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					result.put("Error", "User failed to join in the new group. Please join in it manually. ");
					result.put("DispatchURL", "group");
					result.put("Page", "join");
					result.put("Group_id", rs);
					result.put("Is_redirect", true);
					logger.info("User created a group. Parameters: user_id=<" + user_id + ">, group_name=<"
							+ group_name + ">, " + "salbum_name=<" + salbum_name + ">, cover=<default_cover>, description=<"+description+">");	
					logger.warn("User failed to join in the new group. Parameters: user_id=<"+user_id+">, group_id=<"+rs+">");
				}
			}
		} else {
			if (!result.containsKey("Error")) {
				result.put("Error", "Invalid database operation result.");
				logger.warn("Invalid database operation result. Parameters:  user_id=<" + user_id + ">, group_name=<"
						+ group_name + ">, " + "salbum_name=<" + salbum_name + ">, cover=<" + cover
						+ ">, is_deafult_cover=<" + default_cover + ">");
			}
			result.put("Group_name", group_name);
			result.put("Description", description);
			result.put("SAlbum_name", salbum_name);
			result.put("DispatchURL", "group.jsp");
			result.put("Page", "create");
		}
		return result;
	}
}
