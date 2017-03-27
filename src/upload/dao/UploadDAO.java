package upload.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tool.DB;

/*
 * Upload���ܵ����ݷ��ʲ㣬��ɶ�Ӧ���ܵ�sql���ģ������ɺ�ģ�������׼��
 * UploadDAOʵ�ֵ��ǵ���ģʽ
 */
public class UploadDAO {
	private static UploadDAO upload_dao = new UploadDAO();
	
	/**
	 * ����ģʽ��ȡ����ķ���
	 * @return UploadDAO��Ķ���
	 */	
	public static UploadDAO getInstance() {
		return upload_dao;
	}
	
	/**
	 * ��ȡuser_id������б�
	 * @param user_id �û�ID
	 * @return ������б�
	 */
	public List<Object> getAlbumName(String user_id) {
		List<List<Object>> rs = new ArrayList<List<Object>>();
		String sql = "SELECT album_name FROM album where user_id=? order by album_name asc";
		List<String> parameter = new ArrayList<>();
		parameter.add(user_id);
		rs = DB.execSQLQuery(sql, parameter);
		List<Object> result = new ArrayList<>();
		for (List<Object> list : rs) {
			result.add(list.get(0));
		}
		return result;
	}
	
	/**
	 * ɾ��user_id�û���album_name������Ϊfilename��һ����Ƭ
	 * @param para key����user_id(�û���), album_name(�����), filename(�ļ���)
	 * @return ִ�в�����Ӱ�������
	 * @throws Exception ���ݿ�ִ��ʧ��ʱ�׳��쳣
	 * @see tool.DB#execSQLUpdate(String, List)
	 */
	public int delPicture(Map<String, String>para) throws Exception {
		String sql = "DELETE FROM picture WHERE user_id = ? and album_name = ? and filename = ?";
		List<String> parameter = new ArrayList<>();
		parameter.add(para.get("user_id"));
		parameter.add(para.get("album_name"));
		parameter.add(para.get("filename"));
		return DB.execSQLUpdate(sql, parameter);
	}
	
	/**
	 * ���user_id��album_name�µ�һ����Ϊfilename����Ƭ
	 * @param para key����user_id(�û���), album_name(�����), filename(�ļ���), format(��ʽ), img_description(��Ƭ����)
	 * @return ִ�в�����Ӱ�������
	 * @throws Exception ���ݿ����ִ��ʧ��ʱ�׳��쳣
	 * @see tool.DB#execSQLUpdate(String, List)
	 */
	public int addPicture(Map<String, String>para) throws Exception {
		String sql = "INSERT INTO picture(user_id, album_name, filename, format, description) VALUES(?, ?, ?, ?, ?)";
		List<String> parameter = new ArrayList<>();
		parameter.add(para.get("user_id"));
		parameter.add(para.get("album_name").replaceAll("'", "&apos;"));
		parameter.add(para.get("filename").replaceAll("'", "&apos;"));
		parameter.add(para.get("format"));
		parameter.add(para.get("img_description").replaceAll("'", "&apos;"));
		return DB.execSQLUpdate(sql, parameter);
	}
}
