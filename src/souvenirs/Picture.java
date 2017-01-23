/**
 * 
 */
package souvenirs;

import java.sql.Timestamp;

/**
 * ��Ƭʵ���Ӧ��Bean��
 * @author Chenghong Li
 *
 */
public class Picture {
	/**
	 * ��Ƭ�����û�ID
	 */
	private String user_id;
	/**
	 * ��Ƭ�����������
	 */
	private String album_name;
	/**
	 * ��Ƭ��
	 */
	private String filename;
	/**
	 * ��Ƭ���ļ���ʽ(��׺��)
	 */
	private String format;
	/**
	 * ��Ƭ�ļ��
	 */
	private String description;
	/**
	 * ��Ƭ�ϴ���ʱ��
	 */
	private Timestamp upload_timestamp;
	/**
	 * Picture��ĳ�Ա����
	 */
	final static private int MEMBER_COUNT = 6;
	
	/**
	 * ��ȡPicture��ĳ�Ա���� 
	 * @return Picture��ĳ�Ա����
	 */
	public static int getMemberCount() {
		return MEMBER_COUNT;
	}
	
	/**
	 * ��ȡ��Ƭ�����û�ID
	 * @return ��Ƭ�����û�ID
	 */
	public String getUserId() {
		return user_id;
	}
	
	/**
	 * ����Ƭ�����û�ID��ֵ
	 * @param user_id ��Ƭ�����û�ID
	 */
	public void setUserId(String user_id) {
		this.user_id = user_id;
	}
	
	/**
	 * ��ȡ��Ƭ�����������
	 * @return ��Ƭ�����������
	 */
	public String getAlbumName() {
		return album_name;
	}
	
	/**
	 * ����Ƭ�������������ֵ
	 * @param album_name ��Ƭ�����������
	 */
	public void setAlbumName(String album_name) {
		this.album_name = album_name;
	}
	
	/**
	 * ��ȡ��Ƭ��
	 * @return ��Ƭ��
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * ����Ƭ�����и�ֵ
	 * @param filename ��Ƭ��
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * ��ȡ��Ƭ���ļ���ʽ(��׺��)
	 * @return ��Ƭ���ļ���ʽ(��׺��)
	 */
	public String getFormat() {
		return format;
	}
	
	/**
	 * ����Ƭ���ļ���ʽ(��׺��)���и�ֵ
	 * <strong>������Ӧ��ֻ��Store�ӿڵ�format�����б����ã�����������ֵû������</strong>
	 * @param format ��Ƭ���ļ���ʽ(��׺��)
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
	/**
	 * ��ȡ��Ƭ�ļ��
	 * @return ��Ƭ�ļ��
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * ����Ƭ�ļ�鸳ֵ
	 * @param description ��Ƭ�ļ��
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * ��ȡ��Ƭ�ϴ���ʱ��
	 * @return ��Ƭ�ϴ���ʱ���
	 */
	public Timestamp getUploadTimestamp() {
		return upload_timestamp;
	}
	
	/**
	 * ����Ƭ�ϴ���ʱ�����ֵ
	 * @param upload_timestamp ��Ƭ�ϴ���ʱ��
	 */
	public void setUploadTimestamp(Timestamp upload_timestamp) {
		this.upload_timestamp = upload_timestamp;
	}
	
	/**
	 * ����Picture����ı���ʾ
	 * @return ��ʾPicture����ı�
	 */
	@Override
	public String toString() {
		return "Picture [user_id=" + user_id + ", album_name=" + album_name + ", filename=" + filename + ", format="
				+ format + ", description=" + description + ", upload_timestamp=" + upload_timestamp + "]\n";
	}
}
