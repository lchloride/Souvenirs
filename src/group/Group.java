/**
 * 
 */
package group;

import java.sql.Timestamp;

/**
 *	小组实体(Group)的Bean类 
 */
public class Group {
	/**
	 * 小组的ID
	 */
	private String group_id;
	/**
	 * 小组名
	 */
	private String group_name;
	/**
	 * 属于该小组的共享相册的名称
	 */
	private String shared_album_name;
	/**
	 * 小组简介
	 */
	private String intro;
	/**
	 * 相册封面图片的相对路径
	 */
	private String album_cover;
	/**
	 * 创建小组的时间
	 */
	private Timestamp create_timestamp;
	/**
	 * Group类的成员数量
	 */
	final private static int MEMBER_COUNT = 6;

	/**
	 * 小组ID的获取方法
	 * @return the group_id
	 */
	public String getGroupId() {
		return group_id;
	}
	/**
	 * 小组ID的赋值方法
	 * @param group_id the group_id to set
	 */
	public void setGroupId(String group_id) {
		this.group_id = group_id;
	}
	/**
	 * 小组名称的获取方法
	 * @return the group_name
	 */
	public String getGroupName() {
		return group_name;
	}
	/**
	 * 小组名称的赋值方法
	 * @param group_name the group_name to set
	 */
	public void setGroupName(String group_name) {
		this.group_name = group_name;
	}
	/**
	 * 隶属该小组的共享相册名称的获取方法
	 * @return the shared_album_name
	 */
	public String getSharedAlbumName() {
		return shared_album_name;
	}
	/**
	 * 隶属该小组的共享相册名称的赋值方法
	 * @param shared_album_name the shared_album_name to set
	 */
	public void setSharedAlbumName(String shared_album_name) {
		this.shared_album_name = shared_album_name;
	}
	/**
	 * 小组简介的获取方法
	 * @return the intro
	 */
	public String getIntro() {
		return intro;
	}
	/**
	 * 小组简介的赋值方法
	 * @param intro the intro to set
	 */
	public void setIntro(String intro) {
		this.intro = intro;
	}
	/**
	 * 相册封面地址的获取方法
	 * @return the album_cover
	 */
	public String getAlbumCover() {
		return album_cover;
	}
	/**
	 * 相册封面地址的赋值方法
	 * @param album_cover the album_cover to set
	 */
	public void setAlbumCover(String album_cover) {
		this.album_cover = album_cover;
	}
	/**
	 * 创建时间的获取方法
	 * @return the create_timestamp
	 */
	public Timestamp getCreateTimestamp() {
		return create_timestamp;
	}
	/**
	 * 创建时间的赋值方法
	 * @param create_timestamp the create_timestamp to set
	 */
	public void setCreateTimestamp(Timestamp create_timestamp) {
		this.create_timestamp = create_timestamp;
	}
	/**
	 * Group类成员数量的获取方法
	 * @return the memberCount
	 */
	public static int getMemberCount() {
		return MEMBER_COUNT;
	}
	
	
}
