/**
 * 
 */
package souvenirs;

import java.sql.Timestamp;

/**
 * @author Chenghong Li
 *
 */
public class SharedAlbum {
	private String group_id;
	private String shared_album_name;
	private String intro;
	private String album_cover;
	private Timestamp create_timestamp;
	final private static int MEMBER_COUNT = 5;
	
	public static int getMemberCount() {
		return MEMBER_COUNT;
	}
	public String getGroupId() {
		return group_id;
	}
	public void setGroupId(String group_id) {
		this.group_id = group_id;
	}
	public String getSharedAlbumName() {
		return shared_album_name;
	}
	public void setSharedAlbumName(String shared_album_name) {
		this.shared_album_name = shared_album_name;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getAlbumCover() {
		return album_cover;
	}
	public void setAlbumCover(String album_cover) {
		this.album_cover = album_cover;
	}
	public Timestamp getCreateTimestamp() {
		return create_timestamp;
	}
	public void setCreateTimestamp(Timestamp create_timestamp) {
		this.create_timestamp = create_timestamp;
	}
	
	
}
