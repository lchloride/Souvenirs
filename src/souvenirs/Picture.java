/**
 * 
 */
package souvenirs;

import java.sql.Timestamp;

/**
 * @author Chenghong Li
 *
 */
public class Picture {

	private String user_id;
	private String album_name;
	private String filename;
	private String format;
	private String description;
	private Timestamp upload_timestamp;
	final static private int MEMBER_COUNT = 6;
	
	public static int getMemberCount() {
		return MEMBER_COUNT;
	}
	
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
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getUploadTimestamp() {
		return upload_timestamp;
	}
	public void setUploadTimestamp(Timestamp upload_timestamp) {
		this.upload_timestamp = upload_timestamp;
	}
	
	@Override
	public String toString() {
		return "Picture [user_id=" + user_id + ", album_name=" + album_name + ", filename=" + filename + ", format="
				+ format + ", description=" + description + ", upload_timestamp=" + upload_timestamp + "]\n";
	}
}
