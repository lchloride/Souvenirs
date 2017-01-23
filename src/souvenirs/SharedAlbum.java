/**
 * 
 */
package souvenirs;

import java.sql.Timestamp;

/**
 * �������(��������ʵ�壬��Ȼ������Group����)��Bean��
 * @author Chenghong Li
 */
public class SharedAlbum {
	/**
	 * �������������С��ID
	 */
	private String group_id;
	/**
	 * ������������
	 */
	private String shared_album_name;
	/**
	 * �������ķ���
	 */
	private String album_cover;
	/**
	 * ���Ĵ���ʱ��
	 */
	private Timestamp create_timestamp;
	/**
	 * SharedAlbum��Ա����
	 */
	final private static int MEMBER_COUNT = 4;
	
	/**
	 * ���SharedAlbum��Ա����
	 * @return SharedAlbum��Ա����
	 */
	public static int getMemberCount() {
		return MEMBER_COUNT;
	}
	
	/**
	 * ��ȡ�������������С��ID
	 * @return �������������С��ID
	 */
	public String getGroupId() {
		return group_id;
	}
	
	/**
	 * �Թ������������С��ID��ֵ
	 * @param group_id �������������С��ID
	 */
	public void setGroupId(String group_id) {
		this.group_id = group_id;
	}
	
	/**
	 * ��ȡ������������
	 * @return ������������
	 */
	public String getSharedAlbumName() {
		return shared_album_name;
	}
	
	/**
	 * �Թ����������Ƹ�ֵ
	 * @param shared_album_name ������������
	 */
	public void setSharedAlbumName(String shared_album_name) {
		this.shared_album_name = shared_album_name;
	}
/*	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}*/
	
	/**
	 * ��ȡ�������ķ���
	 * @return �������ķ���
	 */
	public String getAlbumCover() {
		return album_cover;
	}
	
	/**
	 * �Թ������ķ��渳ֵ
	 * @param album_cover �������ķ���
	 */
	public void setAlbumCover(String album_cover) {
		this.album_cover = album_cover;
	}
	
	/**
	 * ��ȡ���Ĵ���ʱ��
	 * @return ���Ĵ���ʱ��
	 */
	public Timestamp getCreateTimestamp() {
		return create_timestamp;
	}
	
	/**
	 * �������Ĵ���ʱ��
	 * @param create_timestamp ���Ĵ���ʱ��
	 */
	public void setCreateTimestamp(Timestamp create_timestamp) {
		this.create_timestamp = create_timestamp;
	}
	
	
}
