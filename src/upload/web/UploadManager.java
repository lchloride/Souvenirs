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
	 * ��ȡUploadManager��������
	 * @return UploadManager����
	 */
	public static UploadManager getInstance() {
		return upload_manager;
	}

	/**
	 * �ϴ�һ����Ƭ����������Ӳ�����ɵ�ԭ�Ӳ����������ݿ������һ����¼�����ļ�д�����<br>
	 * �����ݿ����ʧ���򲻿�д�ļ����ļ�д��ʧ����Ҫ����ӵ�������¼ɾ��
	 * @param parameter ��Servlet������ǰ�˱�ֵ��key����login_user_id(�û�ID)��select_album_name(Ҫ�ϴ��������)��
	 * origin_filename(������Ƭ��·��)��pic_name(���浽�������ϵ���Ƭ��)��img_description(��Ƭ������)
	 * @param file_handle �ļ��������
	 * @return ���͸�ǰ�˵Ĳ���key-value�Լ��ϣ��������û�������б�(��ʾ��ҳ���У�key=Album_list)��
	 * �ϴ����(key=Upload_result)��������Ϣ(���������key=Error_msg)���ϴ��������(�����ϴ��ɹ������ʾ��key=Album_name)��
	 * �ϴ��������(�����ϴ��ɹ������ʾ��key=Filename)
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
	 * ���״δ�uploadҳ���ʱ�򣬷��ظ��û���������б�
	 * @param parameter ǰ�˴����Ĳ�����key����login_user_id(��¼���û�ID)��album_name(Ҫ�ϴ��������������������ҳ��������������)
	 * @return ���͸�ǰ�˵Ĳ����������Ƿ�ָ��������ı�־(key=Is_specified)�����û�ӵ�е�����б�(key=Album_list��δָ��album_name���������)��
	 * 				ָ���������(key=Album_name��ָ�������������´��ڣ�������û�û�����ָ������ᣬ����ֵΪInvalid Album Name)
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
	 * ɾ��һ����Ƭ
	 * @param parameter Ҫɾ������Ƭ������(Map�е�keyΪlogin_user_id, select_album_name, filename)��ɵĲ���Map
	 * @return ɾ��������ο�tool.DB��execSQLUpdate����
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
