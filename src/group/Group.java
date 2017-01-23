/**
 * 
 */
package group;

import java.sql.Timestamp;

/**
 *	С��ʵ��(Group)��Bean�� 
 */
public class Group {
	/**
	 * С���ID
	 */
	private String group_id;
	/**
	 * С����
	 */
	private String group_name;
	/**
	 * ���ڸ�С��Ĺ�����������
	 */
	private String shared_album_name;
	/**
	 * С����
	 */
	private String intro;
	/**
	 * ������ͼƬ�����·��
	 */
	private String album_cover;
	/**
	 * ����С���ʱ��
	 */
	private Timestamp create_timestamp;
	/**
	 * Group��ĳ�Ա����
	 */
	final private static int MEMBER_COUNT = 6;

	/**
	 * С��ID�Ļ�ȡ����
	 * @return the group_id
	 */
	public String getGroupId() {
		return group_id;
	}
	/**
	 * С��ID�ĸ�ֵ����
	 * @param group_id the group_id to set
	 */
	public void setGroupId(String group_id) {
		this.group_id = group_id;
	}
	/**
	 * С�����ƵĻ�ȡ����
	 * @return the group_name
	 */
	public String getGroupName() {
		return group_name;
	}
	/**
	 * С�����Ƶĸ�ֵ����
	 * @param group_name the group_name to set
	 */
	public void setGroupName(String group_name) {
		this.group_name = group_name;
	}
	/**
	 * ������С��Ĺ���������ƵĻ�ȡ����
	 * @return the shared_album_name
	 */
	public String getSharedAlbumName() {
		return shared_album_name;
	}
	/**
	 * ������С��Ĺ���������Ƶĸ�ֵ����
	 * @param shared_album_name the shared_album_name to set
	 */
	public void setSharedAlbumName(String shared_album_name) {
		this.shared_album_name = shared_album_name;
	}
	/**
	 * С����Ļ�ȡ����
	 * @return the intro
	 */
	public String getIntro() {
		return intro;
	}
	/**
	 * С����ĸ�ֵ����
	 * @param intro the intro to set
	 */
	public void setIntro(String intro) {
		this.intro = intro;
	}
	/**
	 * �������ַ�Ļ�ȡ����
	 * @return the album_cover
	 */
	public String getAlbumCover() {
		return album_cover;
	}
	/**
	 * �������ַ�ĸ�ֵ����
	 * @param album_cover the album_cover to set
	 */
	public void setAlbumCover(String album_cover) {
		this.album_cover = album_cover;
	}
	/**
	 * ����ʱ��Ļ�ȡ����
	 * @return the create_timestamp
	 */
	public Timestamp getCreateTimestamp() {
		return create_timestamp;
	}
	/**
	 * ����ʱ��ĸ�ֵ����
	 * @param create_timestamp the create_timestamp to set
	 */
	public void setCreateTimestamp(Timestamp create_timestamp) {
		this.create_timestamp = create_timestamp;
	}
	/**
	 * Group���Ա�����Ļ�ȡ����
	 * @return the memberCount
	 */
	public static int getMemberCount() {
		return MEMBER_COUNT;
	}
	
	
}
