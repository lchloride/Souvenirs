/**
 * 
 */
package souvenirs;

import java.sql.Timestamp;

import org.json.JSONObject;

/**
 * @author Chenghong Li
 *
 */
public class Comment {
	private String user_id;
	private String album_name;
	private String picture_name;
	private String comment_user_id;
	private String comment_content;
	private Timestamp time;
	private String reply_user_id;
	private String reply_content;
	private final static int MEMBER_COUNT = 8;
	/**
	 * @return the user_id
	 */
	public String getUserId() {
		return user_id;
	}
	/**
	 * @param user_id the user_id to set
	 */
	public void setUserId(String user_id) {
		this.user_id = user_id;
	}
	/**
	 * @return the album_name
	 */
	public String getAlbumName() {
		return album_name;
	}
	/**
	 * @param album_name the album_name to set
	 */
	public void setAlbumName(String album_name) {
		this.album_name = album_name;
	}
	/**
	 * @return the picture_name
	 */
	public String getPictureName() {
		return picture_name;
	}
	/**
	 * @param picture_name the picture_name to set
	 */
	public void setPictureName(String picture_name) {
		this.picture_name = picture_name;
	}
	/**
	 * @return the comment_user_id
	 */
	public String getCommentUserId() {
		return comment_user_id;
	}
	/**
	 * @param comment_user_id the comment_user_id to set
	 */
	public void setCommentUserId(String comment_user_id) {
		this.comment_user_id = comment_user_id;
	}
	/**
	 * @return the comment_content
	 */
	public String getCommentContent() {
		return comment_content;
	}
	/**
	 * @param comment_content the comment_content to set
	 */
	public void setCommentContent(String comment_content) {
		this.comment_content = comment_content;
	}
	/**
	 * @return the time
	 */
	public Timestamp getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Timestamp time) {
		this.time = time;
	}
	/**
	 * @return the reply_user_id
	 */
	public String getReplyUserId() {
		return reply_user_id;
	}
	/**
	 * @param reply_user_id the reply_user_id to set
	 */
	public void setReplyUserId(String reply_user_id) {
		this.reply_user_id = reply_user_id;
	}
	/**
	 * @return the reply_content
	 */
	public String getReplyContent() {
		return reply_content;
	}
	/**
	 * @param reply_content the reply_content to set
	 */
	public void setReplyContent(String reply_content) {
		this.reply_content = reply_content;
	}
	/**
	 * @return the memberCount
	 */
	public static int getMemberCount() {
		return MEMBER_COUNT;
	}
	
	/**
	 * Form JSON string in object format that is used to display on the page 
	 * @return the formatted JSON object string
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private String formatJSONObjectForDisplay() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("comment_user_id", comment_user_id);
		jsonObject.put("comment_content", comment_content);
		jsonObject.put("time", time);
		jsonObject.put("reply_user_id", reply_user_id);
		jsonObject.put("reply_content", reply_content);
		return jsonObject.toString();
	}
	/* Format a string to show its content
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Comment [user_id=" + user_id + ", album_name=" + album_name + ", picture_name=" + picture_name
				+ ", comment_user_id=" + comment_user_id + ", comment_content=" + comment_content + ", time=" + time
				+ ", reply_user_id=" + reply_user_id + ", reply_content=" + reply_content + "]\n";
	}
	
}
