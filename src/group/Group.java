/**
 * 
 */
package group;

import java.sql.Timestamp;

/**
 * @author Chenghong Li
 *
 */
public class Group {
	private String group_id;
	private String group_name;
	private String shared_album_name;
	private String intro;
	private String album_cover;
	private Timestamp create_timestamp;
	final private static int MEMBER_COUNT = 6;
	/**
	 * @return the group_id
	 */
	public String getGroupId() {
		return group_id;
	}
	/**
	 * @param group_id the group_id to set
	 */
	public void setGroupId(String group_id) {
		this.group_id = group_id;
	}
	/**
	 * @return the group_name
	 */
	public String getGroupName() {
		return group_name;
	}
	/**
	 * @param group_name the group_name to set
	 */
	public void setGroupName(String group_name) {
		this.group_name = group_name;
	}
	/**
	 * @return the shared_album_name
	 */
	public String getSharedAlbumName() {
		return shared_album_name;
	}
	/**
	 * @param shared_album_name the shared_album_name to set
	 */
	public void setSharedAlbumName(String shared_album_name) {
		this.shared_album_name = shared_album_name;
	}
	/**
	 * @return the intro
	 */
	public String getIntro() {
		return intro;
	}
	/**
	 * @param intro the intro to set
	 */
	public void setIntro(String intro) {
		this.intro = intro;
	}
	/**
	 * @return the album_cover
	 */
	public String getAlbumCover() {
		return album_cover;
	}
	/**
	 * @param album_cover the album_cover to set
	 */
	public void setAlbumCover(String album_cover) {
		this.album_cover = album_cover;
	}
	/**
	 * @return the create_timestamp
	 */
	public Timestamp getCreateTimestamp() {
		return create_timestamp;
	}
	/**
	 * @param create_timestamp the create_timestamp to set
	 */
	public void setCreateTimestamp(Timestamp create_timestamp) {
		this.create_timestamp = create_timestamp;
	}
	/**
	 * @return the memberCount
	 */
	public static int getMemberCount() {
		return MEMBER_COUNT;
	}
	
	
}
