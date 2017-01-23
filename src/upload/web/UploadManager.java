package upload.web;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import tool.PropertyOper;
import upload.dao.UploadDAO;

/**
 * Upload Image Operator
 */
public class UploadManager {
	private static UploadManager upload_manager = new UploadManager();
	private static Logger logger = Logger.getLogger(UploadManager.class);
	private UploadDAO dao = null;
	final int OWNER_ID = 0;
	final int OWNER_ALBUM_NAME = 1;
	final int OWNER_FILENAME = 2;

	public UploadManager() {
		
	}

	/**
	 * 获取UploadManager单例对象
	 * @return UploadManager对象
	 */
	public static UploadManager getInstance() {
		return upload_manager;
	}

	/**
	 * 上传一张照片，完成两个子操作组成的原子操作：向数据库中添加一条记录；将文件写入磁盘<br>
	 * 向数据库添加失败则不可写文件；文件写入失败需要将添加的那条记录删除
	 * @param parameter 从Servlet传来的前端表单值，key包含login_user_id(用户ID)，select_album_name(要上传到的相册)，
	 * origin_filename(本地照片的路径)，pic_name(保存到服务器上的照片名)，img_description(照片的描述)
	 * @param file_handle 文件操作句柄
	 * @return 发送给前端的参数key-value对集合，包括该用户的相册列表(显示新页面中，key=Album_list)，
	 * 上传结果(key=Upload_result)，错误信息(部分情况，key=Error_msg)，上传的相册名(用在上传成功后的显示，key=Album_name)，
	 * 上传的相册名(用在上传成功后的显示，key=Filename)
	 */
	public Map<String, Object> uploadPicture(Map<String, String> parameter, FileItem file_handle) {
		// TODO Auto-generated method stub
		if (dao == null)
			dao = UploadDAO.getInstance();
		
		Map<String, String> para = new HashMap<>();
		Map<String, Object> result = new HashMap<>();
		
		//Prepare parameters for querying album name and adding image 
		para.put("user_id", parameter.get("login_user_id"));
		para.put("album_name", parameter.get("select_album_name"));
		String origin_filename = parameter.get("origin_filename");
		String format = origin_filename.substring(origin_filename.lastIndexOf(".") + 1);
		para.put("format", format);
		para.put("filename", parameter.get("pic_name") + "." + format);
		para.put("img_description", parameter.get("img_description"));
		
		//Query album_name of specific user
		List<Object> album_name_list = dao.getAlbumName(parameter.get("login_user_id"));
		result.put("Album_list", album_name_list);
		
		//Add a row of image into DB 
		Map<String, Object> sql_exec_result = dao.addPicture(para);
		logger.debug(sql_exec_result.get("process_state")+", "+sql_exec_result.get("affect_row_count"));
		
		//Check result of adding operation
		if (!(boolean) sql_exec_result.get("process_state")) {
			//Adding failed
			result.put("Upload_result", false);
			result.put("Error_msg", sql_exec_result.get("error_msg"));
			logger.info("User failed to upload picture, error:<" + sql_exec_result.get("error_msg")
					+ "> with parameters:<" + parameter + ">");
		} else {
			// Adding succeeded
			// Form absolute file path 
			String uploadPath = PropertyOper.GetValueByKey("souvenirs.properties", "data_path") + File.separator
					+ para.get("user_id") + File.separator + para.get("album_name");

			// Create path if it does not exist
			File uploadDir = new File(uploadPath);
			logger.debug("upload_path:"+uploadDir.getPath()+" "+uploadDir.exists());
			if (!uploadDir.exists()) {
				logger.debug(uploadDir.mkdirs());
			}
			
			//Obtain filename and generate whole file path for writting
			String fileName = new File(para.get("filename")).getName();
			System.out.println("filename:"+fileName);
			String filePath = null;
			filePath = (uploadPath + File.separator + fileName);
			
			//System.getProperties().list(System.out);
			//Create new file
			File storeFile = new File(filePath);
			logger.debug(filePath);

			// Save image to disk
			try {
				file_handle.write(storeFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//Delete added row of image if there is exception when writing image
				parameter.put("filename", para.get("filename"));
				if ((boolean) delPicture(parameter).get("Delete_result")) {
					logger.info("User(id=<" + parameter.get("login_user_id")
							+ ">) deleted the uploaded picture since there are something wrong when writing files.");
				} else {
					// Deletion failed, there is uniformity between database and file data
					logger.error("User(id=<" + parameter.get("login_user_id")
							+ ">) failed to delete the uploaded picture although there are something wrong when writing files, which leads to inconsistency in database!");
				}
				// Set error message and quit
				result.put("Upload_result", false);
				result.put("Error_msg", e.getMessage());
				logger.info("User failed to write uploading picture, error:<" + e.getMessage() + "> with parameters:<"
						+ parameter + ">");
				return result;
			}
			//Set success message
			result.put("Album_name", para.get("album_name"));
			result.put("Filename", para.get("filename"));
			result.put("Upload_result", true);
			logger.info("User(id="+parameter.get("login_user_id")+") uploaded a picture. Parameters:<"+parameter+">");
		}
		return result;
	}
	
	/**
	 * 在首次打开upload页面的时候，返回该用户的相册名列表
	 * @param parameter 前端传来的参数，key包含login_user_id(登录的用户ID)、album_name(要上传到的相册名，从相册管理页面点进来情况会存在)
	 * @return 发送给前端的参数表，包含是否指定相册名的标志(key=Is_specified)、该用户拥有的相册列表(key=Album_list，未指定album_name的情况存在)、
	 * 				指定的相册名(key=Album_name，指定相册名的情况下存在；如果该用户没有这个指定的相册，它的值为Invalid Album Name)
	 */
	public Map<String, Object> displayContent(Map<String, String> parameter) {
		if (dao == null)
			dao = UploadDAO.getInstance();
		Map<String, Object> result = new HashMap<>();
		String album_name = parameter.get("album_name");
		List<Object> album_name_list = dao.getAlbumName(parameter.get("login_user_id"));
		if (album_name == null || album_name.isEmpty()) {
			// Obtain album list
			result.put("Is_specified", false);
			result.put("Album_list", album_name_list);
		} else {
			//Check invalidation of specified album name
			result.put("Is_specified", true);
			if (album_name_list.contains(album_name))
				result.put("Album_name", album_name);
			else
				result.put("Album_name", "Invalid Album Name");
		}
		return result;
	}

	/**
	 * 删除一张照片
	 * @param parameter 要删除的相片的主键(Map中的key为login_user_id, select_album_name, filename)组成的参数Map
	 * @return 删除结果，参考tool.DB的execSQLUpdate方法
	 * @see tool.DB#execSQLUpdate(String, List)
	 */
	public Map<String, Object> delPicture(Map<String, String> parameter) {
		if (dao == null)
			dao = UploadDAO.getInstance();
		Map<String, String> para = new HashMap<>();
		Map<String, Object> result = new HashMap<>();
		para.put("user_id", parameter.get("login_user_id"));
		para.put("album_name", parameter.get("select_album_name"));
		para.put("filename", parameter.get("filename"));
		Map<String, Object> sql_exec_result = dao.delPicture(para);
		if ((boolean) sql_exec_result.get("process_state")) {
			result.put("Delete_result", true);
			logger.info("User(id=<" + parameter.get("login_user_id") + ">) deleted picture<" + para + "> ");
		} else {
			result.put("Delete_result", false);
			result.put("Error_msg", sql_exec_result.get("error_msg"));
			logger.info("User(id=<" + parameter.get("login_user_id") + ">) failed to delete picture, error:<"
					+ sql_exec_result.get("error_msg") + "> with parameters:<" + parameter + ">");
		}
		return result;
	}
}
