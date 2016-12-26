package upload.web;

import java.util.Map;

import org.apache.log4j.Logger;

import upload.dao.UploadDAO;

public class UploadManager {
	private static UploadManager upload_manager = new UploadManager();
	private Map<String, String> parameter = null;
	private static Logger logger = Logger.getLogger(UploadManager.class);
	UploadDAO dao = null;
	final int OWNER_ID = 0;
	final int OWNER_ALBUM_NAME = 1;
	final int OWNER_FILENAME = 2;
	
	public UploadManager() {
		dao = new UploadDAO();
	}
	
	public void setParameter(Map<String, String> parameter) {
		this.parameter = parameter;
	}
	
	public static UploadManager getInstance() {
		return upload_manager;
	}

	public Map<String, Object> uploadImg() {
		// TODO Auto-generated method stub
		
		return null;
	}
}
