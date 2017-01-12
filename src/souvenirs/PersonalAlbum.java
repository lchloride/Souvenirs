package souvenirs;

import java.sql.Timestamp;

public class PersonalAlbum {
	private String user_id;
	private String album_name;
	private String intro;
	private String album_cover;
	private Timestamp create_timestamp;
	final public static int para_count = 5;
	
	public String getUserId() {
		return user_id;
	}
	public void setUserId(String user_id) {
		this.user_id = user_id;
	}
	public String getAlbumName() {
		return album_name;
	}
	public void setAlbumName(String album_name) {
		this.album_name = album_name;
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
