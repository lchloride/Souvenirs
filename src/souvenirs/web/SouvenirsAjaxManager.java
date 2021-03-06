/**
 * 
 */
package souvenirs.web;

import java.io.File;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group.Group;
import souvenirs.Comment;
import souvenirs.PersonalAlbum;
import souvenirs.Picture;
import souvenirs.dao.SouvenirsDAO;
import tool.FileOper;
import tool.ImageLoader;
import tool.PropertyOper;
import tool.exception.BadRequestException;
import tool.exception.RenameFolderErrorException;
import user.web.UserManager;

/**
 *Souvenirs完成Ajax操作的业务类，比如：更新相册的名字、更新相册的封面、删除相册中照片等
 */
public class SouvenirsAjaxManager {
	private static SouvenirsAjaxManager souvenirs_manager = new SouvenirsAjaxManager();
	private static Logger logger = Logger.getLogger(SouvenirsManager.class);
	private final static int DEFAULT_AFFECTED_ROW = 1;
	private SouvenirsDAO dao = null;
	private static String BASE_PATH = PropertyOper.GetValueByKey("souvenirs.properties", "data_path");

	/**
	 * 单例模式获取对象的方法
	 * 
	 * @return SouvenirsAjaxManager类的对象
	 */
	public static SouvenirsAjaxManager getInstance() {
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
	 * 更新相册名的业务操作：个人相册和共享相册的更名业务都由该方法执行
	 * @param parameter 从前端发来的参数，key包括old_name(原相册名)、new_name(新相册名)、
	 * 							login_user_id(登录用户名：更新个人相册时用于确定相册所属的用户；更新共享相册时用户检查该用户是否具有更新的权限)、
	 * 							is_personal(标识相册归属的标志，“true”表示该相册为个人相册；"false"表示该相册为共享相册；其他情况均为参数错误)、
	 * 							group_id(共享相册所归属的小组ID，仅在共享相册的情况下存在)
	 * @return 向前端发送的操作结果字符串。如果操作成功，则返回"true"，否则没有返回值，错误会以异常的形式抛出。
	 * @throws Exception 数据库语句执行错误会抛出异常。
	 * @throws BadRequestException 参数错误会抛出异常
	 * @throws RenameFolderErrorException 文件系统中相册的文件夹重命名失败会发生错误会抛出异常
	 */
	public String updateAlbumName(Map<String, String> parameter) throws RenameFolderErrorException, BadRequestException, Exception {
		checkValidDAO();
		//Get basic parameters from front side as string type
		String result = new String();
		String original_album_name = parameter.get("old_name");
		String new_album_name = parameter.get("new_name");
		String user_id = parameter.get("login_user_id");
		String is_personal = parameter.get("is_personal");
		try {
			if (is_personal.contentEquals("true")) {
				// Update name of personal album
				
				// Rename folder name
				if (!FileOper.rename(BASE_PATH + File.separator + user_id + File.separator + original_album_name,
						BASE_PATH + File.separator + user_id + File.separator + new_album_name))
					throw new RenameFolderErrorException("Rename folder failed. original path:<" + BASE_PATH
							+ File.separator + user_id + File.separator + original_album_name + "> new_path<"
							+ BASE_PATH + File.separator + user_id + File.separator + new_album_name + ">");
				// Update item in database
				boolean rs = dao.updatePAlbumName(user_id, original_album_name, new_album_name);
				logger.debug(rs);
				if (!rs)
					throw new Exception("Updating personal album name failed.  Parameters:<"
							+ Arrays.asList(user_id, original_album_name, new_album_name) + ">");
				//Result back to front side
				result = "true";
				//Write log
				logger.info("Updating personal album name succeed. User_id<" + user_id + ">, original_album_name<"
						+ original_album_name + ">, new_album_name<" + new_album_name + ">");
				
			} else if (is_personal.contentEquals("false")){
				//  Update name of shared album
				// Get group id to be operated
				String group_id = parameter.get("group_id");
				// Update record in database
				int rs = dao.updateSAlbumName(user_id, group_id, new_album_name);
				// Check result of updating operation
				if (rs != DEFAULT_AFFECTED_ROW)
					throw new Exception("Updating shared album name failed.  Parameters:<"
							+ Arrays.asList(user_id, original_album_name, new_album_name, group_id) + ">");
				// Result back to front side
				result = "true";
				// Write log
				logger.info("Updating shared album name succeed. User_id<" + user_id + ">, original_album_name<"
						+ original_album_name + ">, new_album_name<" + new_album_name + ">, group_id = <"+group_id+">");
			} else {
				// Invalid parameter
				throw new BadRequestException("Cannot update album name with error of invalid parameter is_personal. Parameters: User_id<" + 
									user_id + ">, is_personal=<"+is_personal+">");
			}
		} catch (RenameFolderErrorException e) {
			throw e;
		} catch (Exception e) {
			// There are two key steps to finish personal album name updating. The one is to rename its folder, the other is to update record in database.
			// These two operations should be executed in one combination. However, in this field, it means renaming is successful but updating is failed.
			// Thus, to avoid  inconsistency between database and file system,  renaming operation must be undone.
			// Besides, if undo operation is failed, an error message must be written in the log.
			// In the situation of updating shared album name, there is no need to do so since there is no file operation.
			if (is_personal.contentEquals("true") &&
					!FileOper.rename(BASE_PATH + File.separator + user_id + File.separator + new_album_name,
					BASE_PATH + File.separator + user_id + File.separator + original_album_name)) {
				logger.error(
						"Cannot rollback when updating album name failed, which cause inconsistency between file system and database."
								+ "Rename from <" + BASE_PATH + File.separator + user_id + File.separator
								+ new_album_name + "> to <" + BASE_PATH + File.separator + user_id + File.separator
								+ original_album_name + ">");
			}
			throw e;
		}
		return result;
	}

	/**
	 * 更新相册的简介，包括个人相册和共享相册
	 * @param parameter 前端发来的参数表，key包括album_name(相册名)、new_description(新的简介内容)
	 * 						login_user_id(登录用户ID：对于个人相册，该参数定义了个人相册所属的用户；对于共享相册，该参数将用户操作的权限检查)、
	 * 						is_personal(标识相册归属的标志，“true”表示该相册为个人相册；"false"表示该相册为共享相册；其他情况均为参数错误)、
	 * 						group_id(共享相册所归属的小组ID，仅在共享相册的情况下存在)
	 * @return 向前端发送的操作结果字符串。如果操作成功，则返回"true"，否则没有返回值，错误会以异常的形式抛出。
	 * @throws Exception 数据库语句执行失败会抛出异常
	 * @throws BadRequestException 无效参数会抛出异常
	 */
	public String updateDescription(Map<String, String> parameter) throws BadRequestException, Exception {
		checkValidDAO();
		// Get basic parameters from front side
		String user_id = parameter.get("login_user_id");
		String album_name = parameter.get("album_name");
		String new_description = parameter.get("new_description");
		String is_personal = parameter.get("is_personal");
		String result = new String();
		
		try {
			if (is_personal.contentEquals("true")) {
				// Update description personal album
				
				// Update record in database
				int rs = dao.updatePAlbumDescription(user_id, album_name, new_description);
				// Check whether updating operation is successful or not
				if (rs != DEFAULT_AFFECTED_ROW)
					throw new Exception("Updating personal album description failed.  Parameters:<"
							+ Arrays.asList(user_id, album_name, new_description) + ">");
				
				// Result back to front side
				result = "true";
				// Write log
				logger.info("Updating personal album description succeeded.  Parameters:<"
							+ Arrays.asList(user_id, album_name, new_description) + ">");
			} else if (is_personal.contentEquals("false")) {
				// Update description of shared album 
				
				String group_id = parameter.get("group_id");
				//	Update record in database
				int rs = dao.updateSAlbumDescription(user_id, group_id, new_description);
				// Check whether updating operation is successful or not
				if (rs != DEFAULT_AFFECTED_ROW)
					throw new Exception("Updating shared album description failed.  Parameters:<"
							+ Arrays.asList(user_id, album_name, new_description, group_id) + ">");
				// Result back to front side
				result = "true";
				// Write log
				logger.info("Updating shared album description succeeded.  Parameters:<"
						+ Arrays.asList(user_id, album_name, new_description, group_id) + ">");
			} else {
				// Parameter is_personal is invalid
				throw new BadRequestException("Cannot update album description with error of invalid parameter is_personal. Parameters: User_id<" + 
									user_id + ">, is_personal=<"+is_personal+">");
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * 删除一张照片。理论上用户只能删除自己的照片
	 * @param parameter 前端发来的参数表，key包括album_name(相册名)、filename(要删除的照片名)
	 * 						login_user_id(登录用户ID：对于个人相册，该参数定义了个人相册所属的用户；对于共享相册，该参数将用户操作的权限检查)、
	 * @return 向前端发送的操作结果字符串。如果操作成功，则返回"true"，否则没有返回值，错误会以异常的形式抛出。
	 * @throws Exception 数据库语句执行失败会抛出异常
	 */
	public String deletePicture(Map<String, String> parameter) throws Exception {
		checkValidDAO();
		String result = "true";
		String user_id = parameter.get("login_user_id");
		String album_name = parameter.get("album_name");
		String filename = parameter.get("filename");
		try {
			// Delete all related record in database
			int rs = dao.deletePicture(user_id, album_name, filename);
			// If rs equals to zero, it means delete operation failed; if rs is
			// larger than one, there must be something wrong with rs.
			if (rs != DEFAULT_AFFECTED_ROW)
				throw new Exception(
						"Cannot delete picture item in database. parameter: user_id=<" + user_id + ">, album_name=<"
								+ album_name + ">, filename=<" + filename + ">, sql affecting rows=<" + rs + ">");
			
			// Delete picture in file system (Attention: this operation cannot be undone!)
			if (!FileOper.deleteFile(
					BASE_PATH + File.separator + user_id + File.separator + album_name + File.separator + filename)) {
				logger.error("Cannot delete picture on the disk. User_id=<" + user_id + ">, Album_name=<" + album_name
						+ ">, File name=<" + filename + ">");
				// If deletion failed, write error message in log
				throw new Exception("Cannot delete picture on the disk. User_id=<" + user_id + ">, Album_name=<"
						+ album_name + ">, File name=<" + filename + ">");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		logger.info("Deleting picture succeeded. User_id=<" + user_id + ">, Album_name=<" + album_name
				+ ">, File name=<" + filename + ">");
		return result;
	}

	/**
	 * 更新相册的封面，包括个人相册和共享相册
	 * @param parameter 前端发来的参数表，key包括album_name(个人相册的情况下指待操作的相册名；共享相册的情况下指新封面所属的相册)、
	 * 						new_cover(新的相册封面的文件名，不论是对于个人相册还是共享相册)
	 * 						login_user_id(登录用户ID：对于个人相册，该参数定义了个人相册所属的用户；对于共享相册，该参数将用户操作的权限检查)、
	 * 						is_personal(标识相册归属的标志，“true”表示该相册为个人相册；"false"表示该相册为共享相册；其他情况均为参数错误)、
	 * 						group_id(共享相册所归属的小组ID，仅在共享相册的情况下存在)、
	 * 						user_id(新封面图片所属的用户ID，仅在共享相册的情况下存在)
	 * @return 向前端发送的操作结果字符串。如果操作成功，则返回"true"，否则没有返回值，错误会以异常的形式抛出。
	 * @throws Exception 数据库语句执行失败会抛出异常
	 * @throws BadRequestException 无效参数会抛出异常
	 */
	public String updateAlbumCover(Map<String, String> parameter) throws BadRequestException, Exception {
		checkValidDAO();
		String user_id = parameter.get("login_user_id");
		String album_name = parameter.get("album_name");
		String new_album_cover = parameter.get("new_cover");
		String is_personal = parameter.get("is_personal");

		String result = "true";
		try {
			if (is_personal.contentEquals("true")) {
				// Update cover of personal album
				
				// Combine album cover absolute address: format as "base path/user id/album name/filename"
				String new_album_cover_addr = File.separator + user_id + File.separator + album_name + File.separator
						+ new_album_cover;
				// Replace '\' with '\\' to avoid character escaping when combine sql query 
				new_album_cover_addr = new_album_cover_addr.replaceAll("\\\\", "\\\\\\\\");
				
				// Back up original album info
				PersonalAlbum pAlbum = dao.getPAlbumInfo(user_id, album_name);
				// Update record in database  
				int rs = dao.updatePAlbumCover(user_id, album_name, new_album_cover_addr);
				// Check whether updating operation is successful or not
				if (rs != DEFAULT_AFFECTED_ROW)
					throw new Exception("Updating address of personal album(<"+album_name+">) cover failed. parameters: user_id=<" + user_id
							+ ">, album_name=<" + album_name + ">, " + "to be updated cover=<" + new_album_cover
							+ ">, sql affecting rows=<" + rs + ">");
				else 
					// Write log
					logger.info("Updating address of  album cover succeeded. parameters: user_id=<" + user_id
							+ ">, album_name=<" + album_name + ">, " + "original album cover=<" + pAlbum.getAlbumCover()
							+ ">, current album cover=<" + new_album_cover_addr + ">");
			} else if (is_personal.contentEquals("false")) {
				// Update cover of shared album
				// There are two key operations. The one is to copy new cover to group folder and rename it with format "%group id%_cover.jpg"
				// The other is update album_cover field  in database.
				// Although these two operation should be done in a whole, coping file without updating database failure can also be accepted.
				String group_id = parameter.get("group_id");
				
				String new_user_id =URLDecoder.decode(parameter.get("user_id"), "UTF-8");
				// Format cover source absolute path, format as "%base path%/%user id%/%album name%/%filename%"
				String cover_source = BASE_PATH+File.separator+new_user_id+File.separator+album_name+File.separator+new_album_cover;
				// Format cover destination relative path, without base path. Its format is like "/group/%group id%_cover.%format%"
				String cover_rela_dst = File.separator+"group"+File.separator+group_id+"_cover"+
													new_album_cover.substring(new_album_cover.lastIndexOf('.'));
				// Backup original shared album information
				Group sAlbum = dao.getSAlbumInfo(group_id);
				// Copy new cover to cover_dst
				if (!FileOper.copyFile(cover_source, BASE_PATH+cover_rela_dst)) {
					throw new Exception("Copying shared album(<"+album_name+">) cover image failed. parameters: user_id=<" + user_id
							+ ">, group_id=<" + group_id + ">, " + "to be updated cover=<" + new_album_cover + "> from user id=<"+new_user_id+">");
				} else {
					// Replace '\' with '\\' to avoid character escaping when combine sql query and then update album_cover field in database
					int rs = dao.updateSAlbumCover(user_id, group_id, cover_rela_dst.replaceAll("\\\\", "\\\\\\\\"));
					// Check whether updating operation is successful or not
					if (rs != DEFAULT_AFFECTED_ROW)
						// if failed, throw exception
						throw new Exception("Updating address of shared album(<"+album_name+">) cover in database failed. parameters: user_id=<" + user_id
							+ ">, group_id=<" + group_id + ">, " + "to be updated cover=<" + new_album_cover + "> from user id=<"+new_user_id+">");
					else
						// if succeeded, write log
						logger.info("Updating address of shared album cover succeeded. parameters: user_id=<" + user_id
									+ ">, group_id=<" + group_id + ">, " + "previous album cover=<" + sAlbum.getAlbumCover()
									+ ">, current album cover=<" + cover_rela_dst + ">, currrent cover is from <"+cover_source+">");
				}
			} else {
				// Invalid parameter is_personal
				throw new BadRequestException("Cannot update album name with error of invalid parameter is_personal. Parameters: User_id<" + 
						user_id + ">, is_personal=<"+is_personal+">");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("false," + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 获取相册封面，包括个人相册和共享相册
	 * @param parameter 前端发来的参数表，key包括album_name(相册名；共享相册的情况下该参数没有实际意义)、
	 * 						login_user_id(登录用户ID：对于个人相册，该参数定义了个人相册所属的用户；对于共享相册，该参数将用户操作的权限检查)、
	 * 						is_personal(标识相册归属的标志，“true”表示该相册为个人相册；"false"表示该相册为共享相册；其他情况均为参数错误)、
	 * 						group_id(共享相册所归属的小组ID，仅在共享相册的情况下存在)、
	 * @return 向前端发送的操作结果字符串。如果操作成功，则返回"true"，否则没有返回值，错误会以异常的形式抛出。
	 * @throws BadRequestException 无效参数会抛出异常
	 */
	public String queryAlbumCover(Map<String, String> parameter) throws BadRequestException {
		checkValidDAO();
		String user_id = parameter.get("login_user_id");
		String album_name = parameter.get("album_name");
		String is_personal = parameter.get("is_personal");
		String group_id = parameter.get("group_id");
		String result = null;
		if (is_personal.contentEquals("true"))
			// Query cover address of personal album
			result = ImageLoader.genAddrOfPAlbumCover(user_id, album_name);
		else if (is_personal.contentEquals("false"))
			// Query cover address of shared album
			result = ImageLoader.genAddrOfSAlbumCover(group_id);
		else
			// Invalid parameter is_personal
			throw new BadRequestException("Cannot update album name with error of invalid parameter is_personal. Parameters: User_id<" + 
					user_id + ">, is_personal=<"+is_personal+">");
			
		return result;
	}
	
	/**
	 * 获取某个用户的一个相册中的全部照片信息，并以json字符串的形式发给前端。
	 * <p>
	 * 发送到前端的数据格式：<br>
	 * json字符串，由多个对象组成的json数组；其中每个json对象由7个域组成：Filename, Addr, UserID, AlbumName, Username, Description, UploadTime
	 * </p>
	 * @param parameter 发给SouvenirsAjaxManager的参数表，key包括login_user_id(登录用户名)、album_identifier(相册标识符：
	 * 				personal album指的是album name；shared album指的是group_id)
	 * @return 返回相册中全部照片信息所组成的json字符串
	 * @see souvenirs.web.SouvenirsManager#getImageAddrInAlbum(Map)
	 */
	public String queryPictureInAlbum(Map<String, String>parameter) {
		checkValidDAO();
		SouvenirsManager sm = SouvenirsManager.getInstance();
		String result = sm.getImageAddrInAlbum(parameter);
		return result;
	}
	
	/**
	 * 批量分享照片到一个共享相册
	 * @param parameter 从前端发来的参数列表，key包括list_json(要分享照片的相册名与照片名组成的json字符串，
	 * 				形如[{"album_name":"A1", "filename":"B1"}, {"album_name":"A2", "filename":"B2"}, ...]); login_user_id(登录用户ID、，也是照片所属用户的ID)；
	 * 				group_id(共享相册所属的小组ID)
	 * @return json字符串，存放了操作成功、失败、键值重复三种结果分别对应的照片集合，
	 * 				形如{"success_list", ["A1", "A2", ...]}, {"failure_list", ["B1", "B2", ...]}, {"duplication_list", ["C1", "C2", ...]}
	 * @throws JSONException 传入的JSON数据解析失败会抛出异常
	 * @throws Exception 数据库语句执行时抛出异常时会抛出异常
	 */
	public String sharePictures(Map<String, String>parameter) throws JSONException, Exception {
		checkValidDAO();
		
		// Get parameters form front side
		String result = "";
		// A json string storing key information of sharing album   
		String share_list_json = parameter.get("list_json");
		// Login user id
		String user_id = parameter.get("login_user_id");
		// group_id indicates shared album
		String group_id = parameter.get("group_id");
		// Three list storing picture sets of each operation result
		List<String> failure_list = new ArrayList<>();
		List<String> success_list = new ArrayList<>();
		List<String> duplication_list = new ArrayList<>();
		
		try {
			// Parse share_json_list to an array
			JSONArray jsonArray = new JSONArray(share_list_json);
			JSONObject jsonObject = null;
			
			// Check validation of user_id and group_id
			if (user_id == null || user_id.isEmpty() || group_id==null || group_id.isEmpty())
				throw new BadRequestException("user_id / group_id is invalid! Parameters: <"+parameter+">");
			
			// For each item in jsonArray(which stands for one picture to be shared), use method sharePicture() in DAO to share it 
			for (int i=0; i<jsonArray.length(); i++) {
				// Get its information
				jsonObject = jsonArray.getJSONObject(i);
				// Share it
				int rs = dao.sharePicture(user_id, jsonObject.optString("album_name"), jsonObject.optString("filename"), group_id);
				// Check result
				if (rs == SouvenirsDAO.SHARE_PICTURE_DUPLICATE)
					duplication_list.add(jsonObject.optString("album_name")+" - "+jsonObject.optString("filename"));
				else if (rs == SouvenirsDAO.SHARE_PICTURE_SUCCESS)
					success_list.add(jsonObject.optString("album_name")+" - "+jsonObject.optString("filename"));
				else
					failure_list.add(jsonObject.optString("album_name")+" - "+jsonObject.optString("filename"));
			}
		} catch (JSONException e) {
			throw new JSONException("Bad parameter list_json!", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		// Form result json string
		JSONObject result_json = new JSONObject();
		result_json.put("failure_list", failure_list);
		result_json.put("success_list", success_list);
		result_json.put("duplication_list", duplication_list);
		result = result_json.toString();
		return result;
	}
	
	/**
	 * 为一张照片点赞
	 * @param parameter 从前端传来的参数列表，key包括picture_user_id(照片所属用户的ID)、like_user_id(点赞用户的ID)、album_name(照片所属相册的相册名)、
	 * 				picture_name(照片名)
	 * @return 一个json字符串，内容是操作后为该张照片点赞的用户列表, 格式参考souvenirs.dao.SouvenirsDAO#getLikingPersons()
	 * @throws Exception 数据库操作失败会抛出异常
	 * @see souvenirs.dao.SouvenirsDAO#getLikingPersons(String, String, String)
	 */
	public String likePicture(Map<String, String>parameter) throws Exception {
		checkValidDAO();
		// Get parameters from front side
		String picture_user_id = parameter.get("picture_user_id");
		String like_user_id = parameter.get("like_user_id");
		String album_name = parameter.get("album_name");
		String filename = parameter.get("picture_name");
		try {
			int rs = dao.likePicture(like_user_id, picture_user_id, album_name, filename);
			if (rs != DEFAULT_AFFECTED_ROW)
				throw new Exception("Cannot set favorite picture of "+picture_user_id+"/"+album_name+"/"+filename+".");			
		} catch (Exception e) {
			throw e;
		}
		// Query liking status and format json string 
		List<String> liking_person_list = dao.getLikingPersons(picture_user_id, album_name, filename);
		JSONArray liking_person_json = new JSONArray(liking_person_list);
		return liking_person_json.toString();
	}
	
	/**
	 * 取消对一张照片的赞
	 * @param parameter 前端发来的参数，key包括picture_user_id(照片所属用户的ID)、like_user_id(点赞用户的ID)、album_name(照片所属相册的相册名)、
	 * 				picture_name(照片名)
	 * @return 一个json字符串，内容是操作后为该张照片点赞的用户列表，格式参考souvenirs.dao.SouvenirsDAO#getLikingPersons()
	 * @throws Exception 数据库操作失败会抛出异常
	 * @see souvenirs.dao.SouvenirsDAO#getLikingPersons(String, String, String)
	 */
	public String dislikePicture(Map<String, String>parameter) throws Exception {
		checkValidDAO();
		// Get parameters from front side
		String picture_user_id = parameter.get("picture_user_id");
		String like_user_id = parameter.get("like_user_id");
		String album_name = parameter.get("album_name");
		String filename = parameter.get("picture_name");
		try {
			int rs = dao.dislikePicture(like_user_id, picture_user_id, album_name, filename);
			if (rs != DEFAULT_AFFECTED_ROW)
				throw new Exception("Cannot unset favorite picture of "+picture_user_id+"/"+album_name+"/"+filename+".");			
		} catch (Exception e) {
			throw e;
		}
		// Query liking status and format json string 
		List<String> liking_person_list = dao.getLikingPersons(picture_user_id, album_name, filename);
		JSONArray liking_person_json = new JSONArray(liking_person_list);
		return liking_person_json.toString();
	}
	
	/**
	 * 添加一条评论
	 * @param parameter 前端发来的参数，key包括login_user_id(登录用户ID，代表了发表评论的用户的ID)、picture_user_id(照片所属用户的ID)、
	 * 				album_name(照片所属相册的相册名)、picture_name(照片名)、comment(评论内容)、reply_comment_id(该评论所回复的评论的编号，如果不回复任何一条，其值为0)
	 * @return 添加的那条评论的格式输出json字符串
	 */
	public String addComment(Map<String, String> parameter) {
		checkValidDAO();
		String login_user_id = parameter.get("login_user_id");
		String picture_user_id = parameter.get("picture_user_id");
		String album_name = parameter.get("album_name");
		String filename = parameter.get("picture_name");
		String comment_content = parameter.get("comment");
		String reply_id = parameter.get("reply_comment_id");
		String result = "";
		try {
			if (comment_content==null || comment_content.isEmpty())
				throw new Exception("Empty Comment Content.");
			boolean rs = dao.addComment(picture_user_id, album_name, filename, login_user_id, comment_content, reply_id);
			if (rs) 
				logger.info("Add new comment successfully. Parameters: login user id=<"+login_user_id+">, "+
						"comment content=<"+comment_content+">, Picture=<"+picture_user_id+"/"+album_name+"/"+filename+">, "+
						"replied comment id = <"+reply_id+">");
			else {
				logger.info("Add new comment UNSUCCESSFULLY. Parameters: login user id=<"+login_user_id+">, "+
						"comment content=<"+comment_content+">, Picture=<"+picture_user_id+"/"+album_name+"/"+filename+">, "+
						"replied comment id = <"+reply_id+">");
				throw new Exception("Add new comment unsuccessfully.");
			}
			
			//Query comment of picture and format json string
			List<Comment> comments = dao.getAllComments(picture_user_id, album_name, filename);
			Comment comment = comments.get(comments.size()-1);
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("comment_id", comment.getCommentId());
			jsonObject.put("comment_username", UserManager.getUsernameByID(comment.getCommentUserId()));
			jsonObject.put("comment_user_avatar", ImageLoader.genAddrOfAvatar(comment.getCommentUserId()));
			jsonObject.put("comment_content", comment.getCommentContent());
			jsonObject.put("comment_time", sdf.format(comment.getTime()));
			jsonObject.put("is_valid", comment.getIsValid()==1?true:false);
			jsonObject.put("replied_comment_id", comment.getRepliedCommentId());
			result = jsonObject.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Add new comment UNSUCCESSFULLY. Parameters: login user id=<"+login_user_id+">, "+
					"comment content=<"+comment_content+">, Picture=<"+picture_user_id+"/"+album_name+"/"+filename+">, "+
					"replied comment id = <"+reply_id+">");
		}
		logger.debug("result:"+result);
		return result;
	}
	
	/**
	 * 举报一条评论
	 * @param parameter 前端发来的参数，key包括login_user_id(登录用户的ID，用来指定举报人的ID)、picture_uid(图片所属用户的ID)、
	 * 				album_name(图片所属相册的相册名)、picture_name(图片名)、comment_id(举报的那条评论的ID)、report_label(举报标签)、
	 * 				report_content(举报内容详情)
	 * @return 执行成功返回字符串true；否则无返回值并抛出异常
	 * @throws Exception 数据库执行失败时抛出异常
	 */
	public String reportComment(Map<String, String>parameter) throws Exception {
		checkValidDAO();
		String report_user_id = parameter.get("login_user_id");
		String picture_user_id = parameter.get("picture_uid");
		String album_name = parameter.get("album_name");
		String picture_name = parameter.get("picture_name");
		String comment_id = parameter.get("comment_id");
		String report_label = parameter.get("report_label");
		String report_content = parameter.get("report_content");
		
		try {
			boolean rs = dao.reportComment(report_user_id, picture_user_id, album_name, picture_name, comment_id, report_label, report_content);
			if (rs)
				logger.info("User (id:<"+report_user_id+">) reported a comment of parameters: <"+parameter+">");
			else {
				logger.info("Comment report failed. Parameters: <"+parameter+">");
				throw new Exception("Comment report failed. Parameters: <"+parameter+">");
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		return "true";
	}
	
	/**
	 * 删除一个个人相册
	 * @param parameter 前端发来的参数，key包括album_name(欲删除的相册名)
	 * @return 操作结果字符串，只要数据库删除操作成功返回“true”，否则无返回值。
	 * 
	 * <strong>注意：如果数据库删除成功而硬盘上的文件未删除成功也会返回"true"，但是会写入一条error日志，管理员日后需要手动删除掉磁盘上的文件</strong>
	 * @throws Exception 数据库操作失败或者删除操作失败会抛出异常
	 */
	public String deletePAlbum(Map<String, String>parameter) throws Exception {
		checkValidDAO();
		String user_id = parameter.get("login_user_id");
		String album_name = parameter.get("album_name");
		String result = "true";
		try {
			int rs = dao.deletePAlbum(user_id, album_name);
			if (rs == DEFAULT_AFFECTED_ROW) {
				String path = BASE_PATH + File.separator + user_id + File.separator + album_name;
				if (FileOper.deleteFile(path))
					logger.info("Delete personal album successfully! Parameters:<"+parameter+">");
				else {
					logger.error("Album data in database has been deleted, BUT files on the disk failed to delete. Please delete them manually. Parameter:<"+parameter+">");
					
				}
			} else {
				throw new Exception("Operation with no effect");
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Cannot delete personal album since "+e.getMessage()+". Parameters:<"+parameter+">");
			throw e;
		}
		return result;
	}
	
	public String searchPictures(Map<String, String> parameters) {
		checkValidDAO();
		String user_id = parameters.get("login_user_id");
		String keyword = parameters.get("keyword");
		
		JSONArray result = new JSONArray();
		Map<String, String> image_content;
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			List<List<Object>> group = dao.searchPictures(user_id, keyword);
			for (List<Object> image_item : group) {
				image_content = new HashMap<>();
				image_content.put("UserID", (String)image_item.get(0));
				image_content.put("AlbumName", (String)image_item.get(1));
				image_content.put("Filename", (String)image_item.get(3));
				image_content.put("Addr", ImageLoader.genAddrOfPicture((String)image_item.get(0), (String)image_item.get(2),
						(String)image_item.get(3)));
				image_content.put("Username", UserManager.getUsernameByID((String)image_item.get(0)));
				image_content.put("Description", (String)image_item.get(5));
				image_content.put("UploadTime", sdf.format((Timestamp)image_item.get(6)));
				result.put(image_content);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("User  failed to search picture since "+e.getMessage()+". Parameters: user_id=<"+user_id+">, "
					+ "keyword=<"+keyword+">");
			return e.getMessage();
		}
		logger.info("User searched pictures. Parameters: user_id=<"+user_id+">, keyword=<"+keyword+">");
		return result.toString();
	}
}
