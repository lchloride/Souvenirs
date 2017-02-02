/**
 * 
 */
package souvenirs.web;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

import souvenirs.PersonalAlbum;
import souvenirs.dao.SouvenirsDAO;
import tool.FileOper;
import tool.ImageLoader;
import tool.PropertyOper;
import tool.exception.RenameFolderErrorException;

/**
 *
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

	public String updateAlbumName(Map<String, String> parameter) throws Exception {
		checkValidDAO();
		String result = new String();
		String original_album_name = parameter.get("old_name");
		String new_album_name = parameter.get("new_name");
		String user_id = parameter.get("login_user_id");
		try {
			//Rename folder name
	        if (!FileOper.rename(BASE_PATH+File.separator+user_id+File.separator+original_album_name ,
	        		BASE_PATH+File.separator+user_id+File.separator+new_album_name))
	        	throw new RenameFolderErrorException("Rename folder failed. original path:<"+BASE_PATH+File.separator+user_id+File.separator+original_album_name+"> new_path<"+
	        										BASE_PATH+File.separator+user_id+File.separator+new_album_name+">");  
	        
			int rs = dao.updateAlbumName(user_id, original_album_name, new_album_name);
			logger.debug(rs);
			if (rs != DEFAULT_AFFECTED_ROW)
				throw new Exception("Updating album name failed.  Parameters:<"
						+ Arrays.asList(user_id, original_album_name, new_album_name) + ">");
			result = "true";

			logger.info("Updating album name succeed. User_id<"+user_id+">, original_album_name<"+
							original_album_name+">, new_album_name<"+new_album_name+">");
		} catch(RenameFolderErrorException e) {
        	throw e;
		} catch (Exception e) {
	        if (!FileOper.rename(BASE_PATH+File.separator+user_id+File.separator+new_album_name ,
	        		BASE_PATH+File.separator+user_id+File.separator+original_album_name)) {
	        	logger.error("Cannot rollback when updating album name failed, which cause inconsistency between file system and database."+
	        						"Rename from <"+BASE_PATH+File.separator+user_id+File.separator+new_album_name+"> to <"+BASE_PATH+File.separator+user_id+File.separator+original_album_name+">");
	        }
			throw e;
		}
		return result;
	}
	
	public String updateDescription(Map<String, String> parameter) throws Exception {
		 checkValidDAO();
		String user_id = parameter.get("login_user_id");
		String album_name = parameter.get("album_name");
		String new_description = parameter.get("new_description");
		String result = new String();
		try {
			int rs = dao.updateAlbumDescription(user_id, album_name, new_description);
			if (rs != DEFAULT_AFFECTED_ROW)
				throw new Exception("Updating album description failed.  Parameters:<"
						+ Arrays.asList(user_id, album_name, new_description) + ">");
			result = "true";
		} catch (Exception e) {
			throw e;
		}
		logger.info("Updating description succeeded. ");
		return result;
	}
	
	/**
	 * 
	 * @param parameter
	 * @return
	 * @throws Exception
	 */
	public String deletePicture(Map<String, String>parameter) throws Exception {
		checkValidDAO();
		String result = "true";
		String user_id = parameter.get("login_user_id");
		String album_name = parameter.get("album_name");
		String filename = parameter.get("filename");
		try {
			int rs = dao.deletePicture(user_id, album_name, filename);
			//If rs equals to zero, it means delete operation failed; if rs is larger than one, there must be something wrong with rs.
			if (rs != DEFAULT_AFFECTED_ROW)
				throw new Exception("Cannot delete picture item in database. parameter: user_id=<"+
										user_id+">, album_name=<"+album_name+">, filename=<"+filename+">, sql affecting rows=<"+rs+">");
			if (!FileOper.deleteFile(BASE_PATH+File.separator+user_id+File.separator+album_name+File.separator+filename)) {
				logger.error("Cannot delete picture on the disk. User_id=<"+user_id+">, Album_name=<"+album_name+">, File name=<"+filename+">");
				throw new Exception("Cannot delete picture on the disk. User_id=<"+user_id+">, Album_name=<"+album_name+">, File name=<"+filename+">");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		logger.info("Deleting picture succeeded. User_id=<"+user_id+">, Album_name=<"+album_name+">, File name=<"+filename+">");
		return result;
	}
	
	public String updateAlbumCover(Map<String, String> parameter) throws Exception {
		checkValidDAO();
		String user_id = parameter.get("login_user_id");
		String album_name = parameter.get("album_name");
		String new_album_cover = parameter.get("new_cover");
		String new_album_cover_addr = File.separator+user_id+File.separator+album_name+File.separator+new_album_cover;
		new_album_cover_addr = new_album_cover_addr.replaceAll("\\\\", "\\\\\\\\");
		logger.debug("new album cover address "+new_album_cover_addr);
		String result = "true";
		try {
			PersonalAlbum pAlbum = dao.getPAlbumInfo(user_id, album_name);
			int rs = dao.updateAlbumCover(user_id, album_name, new_album_cover_addr);
			if (rs != DEFAULT_AFFECTED_ROW)
				throw new Exception("Updating address of  album cover failed. parameters: user_id=<"+user_id+">, album_name=<"+album_name+">, "
						+ "to be updated description=<"+new_album_cover+">, sql affecting rows=<"+rs+">");
			else
				logger.info("Updating address of  album cover succeeded. parameters: user_id=<"+user_id+">, album_name=<"+album_name+">, "
						+ "original album cover=<"+pAlbum.getAlbumCover()+">, current album cover=<"+new_album_cover_addr+">");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("false,"+e.getMessage(), e);
		}
		return result;
	}
	
	public String queryAlbumCover(Map<String, String> parameter)  {
		checkValidDAO();
		String user_id = parameter.get("login_user_id");
		String album_name = parameter.get("album_name");
		String result =  ImageLoader.genAddrOfPAlbumCover(user_id, album_name);
		return result;
	}
}
