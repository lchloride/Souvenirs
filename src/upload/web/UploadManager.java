package upload.web;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import tool.PropertyOper;
import upload.dao.UploadDAO;

public class UploadManager {
	private static UploadManager upload_manager = new UploadManager();
	private static Logger logger = Logger.getLogger(UploadManager.class);
	private UploadDAO dao = null;
	final int OWNER_ID = 0;
	final int OWNER_ALBUM_NAME = 1;
	final int OWNER_FILENAME = 2;

	public UploadManager() {
		dao = UploadDAO.getInstance();
	}

	public static UploadManager getInstance() {
		return upload_manager;
	}

	public Map<String, Object> uploadPicture(Map<String, String> parameter, FileItem file_handle) {
		// TODO Auto-generated method stub
		Map<String, String> para = new HashMap<>();
		Map<String, Object> result = new HashMap<>();
		para.put("user_id", parameter.get("login_user_id"));
		para.put("album_name", parameter.get("select_album_name"));
		String origin_filename = parameter.get("origin_filename");
		String format = origin_filename.substring(origin_filename.lastIndexOf(".") + 1);
		para.put("format", format);
		para.put("filename", parameter.get("pic_name") + "." + format);
		para.put("img_description", parameter.get("img_description"));
		List<Object> album_name_list = dao.getAlbumName(parameter.get("login_user_id"));
		result.put("Album_list", album_name_list);
		Map<String, Object> sql_exec_result = dao.addPicture(para);
		logger.debug(sql_exec_result.get("process_state")+", "+sql_exec_result.get("affect_row_count"));
		if (!(boolean) sql_exec_result.get("process_state")) {
			result.put("Upload_result", false);
			result.put("Error_msg", sql_exec_result.get("error_msg"));
			logger.info("User failed to upload picture, error:<" + sql_exec_result.get("error_msg")
					+ "> with parameters:<" + parameter + ">");
		} else {
			// 构造临时路径来存储上传的文件
			// 这个路径相对当前应用的目录
			String uploadPath = PropertyOper.GetValueByKey("souvenirs.properties", "data_path") + File.separator
					+ para.get("user_id") + File.separator + para.get("album_name");

			// 如果目录不存在则创建
			File uploadDir = new File(uploadPath);
			logger.debug("upload_path:"+uploadDir.getPath()+" "+uploadDir.exists());
			if (!uploadDir.exists()) {
				logger.debug(uploadDir.mkdirs());
			}

			String fileName = new File(para.get("filename")).getName();
			System.out.println("filename:"+fileName);
			String filePath = null;
			filePath = (uploadPath + File.separator + fileName);
			//System.getProperties().list(System.out);
			File storeFile = new File(filePath);
			// 在控制台输出文件的上传路径
			System.out.println(filePath);
			// 保存文件到硬盘
			try {
				file_handle.write(storeFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				parameter.put("filename", para.get("filename"));
				if ((boolean) delPicture(parameter).get("Delete_result")) {
					logger.info("User(id=<" + parameter.get("login_user_id")
							+ ">) deleted the uploaded picture since there are something wrong when writing files.");
				} else {
					logger.error("User(id=<" + parameter.get("login_user_id")
							+ ">) failed to delete the uploaded picture although there are something wrong when writing files, which leads to inconsistency in database!");
				}
				result.put("Upload_result", false);
				result.put("Error_msg", e.getMessage());
				logger.info("User failed to write uploading picture, error:<" + e.getMessage() + "> with parameters:<"
						+ parameter + ">");
				return result;
			}
			result.put("Album_name", para.get("album_name"));
			result.put("Filename", para.get("filename"));
			result.put("Upload_result", true);
			logger.info("User(id="+parameter.get("login_user_id")+") uploaded a picture. Parameters:<"+parameter+">");
			// request.setAttribute("message", "文件上传成功!");
		}
		return result;
	}

	public Map<String, Object> displayContent(Map<String, String> parameter) {
		Map<String, Object> result = new HashMap<>();
		// Obtain album list
		List<Object> album_name_list = dao.getAlbumName(parameter.get("login_user_id"));
		result.put("Album_list", album_name_list);
		return result;
	}

	public Map<String, Object> delPicture(Map<String, String> parameter) {
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
