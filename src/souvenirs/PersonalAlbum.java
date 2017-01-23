package souvenirs;

import java.sql.Timestamp;
/**
 * �������ʵ���Bean��
 *
 */
public class PersonalAlbum {
	/**
	 * ��������û�ID
	 */
	private String user_id;
	/**
	 * ��������
	 */
	private String album_name;
	/**
	 * �����
	 */
	private String intro;
	/**
	 * ����������·��
	 */
	private String album_cover;
	/**
	 * ���Ĵ���ʱ��
	 */
	private Timestamp create_timestamp;
	/**
	 * PersonalAlbum��ĳ�Ա����
	 */
	final private static int MEMBER_COUNT = 5;
	
	/**
	 * ��ȡPersonalAlbum��ĳ�Ա�����ķ���
	 * @return һ��������PersonalAlbum��ĳ�Ա����
	 */
	public static int getMemberCount() {
		return MEMBER_COUNT;
	}
	
	/**
	 * ��ȡ����������û�ID
	 * @return һ���ַ������Ǹ��û���ID
	 */
	public String getUserId() {
		return user_id;
	}
	
	/**
	 * ������������û�ID���и�ֵ
	 * @param user_id ����ֵ���û����ַ���
	 */
	public void setUserId(String user_id) {
		this.user_id = user_id;
	}
	
	/**
	 * ��ȡ�����
	 * @return һ���ַ�����Ϊ�����
	 */
	public String getAlbumName() {
		return album_name;
	}
	
	/**
	 * ���������ֵ
	 * @param album_name ����ֵ��������ַ���
	 */
	public void setAlbumName(String album_name) {
		this.album_name = album_name;
	}
	
	/**
	 * ��ȡ���ļ��
	 * @return һ���ַ�����Ϊ���ļ�飬������200���ַ�
	 */
	public String getIntro() {
		return intro;
	}
	
	/**
	 * �����ļ�鸳ֵ
	 * @param intro ��ż����ַ���
	 */
	public void setIntro(String intro) {
		this.intro = intro;
	}
	
	/**
	 * ��ȡ����������·��
	 * @return һ���ַ�����Ϊ����������·��
	 */
	public String getAlbumCover() {
		return album_cover;
	}
	
	/**
	 * ��������·�����и�ֵ
	 * @param album_cover ����·��
	 */
	public void setAlbumCover(String album_cover) {
		this.album_cover = album_cover;
	}
	
	/**
	 * ��ȡ��ᴴ����ʱ���
	 * @return һ��Timestamp���󣬴����ʱ���
	 */
	public Timestamp getCreateTimestamp() {
		return create_timestamp;
	}
	
	/**
	 * ��ʱ������и�ֵ
	 * @param create_timestamp ����ֵ��ʱ���
	 */
	public void setCreateTimestamp(Timestamp create_timestamp) {
		this.create_timestamp = create_timestamp;
	}
	
	
}
