/**
 * 
 */
package souvenirs;

import java.sql.Timestamp;

import org.json.JSONObject;

/**
 * 评论(Comment)实体的Bean类
 */
public class Comment {
	/**
	 * 照片所属用户的ID
	 */
	private String user_id;
	/**
	 * 照片所属的相册名
	 */
	private String album_name;
	/**
	 * 照片名，实质是存储在服务器上的文件名
	 */
	private String picture_name;
	/**
	 * 在一张照片中该条评论的ID
	 */
	private int comment_id;
	/**
	 * 发表本条评论的用户ID
	 */
	private String comment_user_id;
	/**
	 * 本条评论的内容
	 */
	private String comment_content;
	/**
	 * 评论是否可见的标志
	 */
	private int is_valid;
	/**
	 * 发表评论的时间
	 */
	private Timestamp time;
	/**
	 * 在一张照片中，本条评论所回复的评论的ID。如果不是任何一条评论的回复则为空字符串
	 */
	private int replied_comment_id;
	/**
	 * Comment类的成员数量
	 */
	private final static int MEMBER_COUNT = 9;
	
	/**
	 * 照片所属用户的ID的获取方法
	 * @return the user_id
	 */
	public String getUserId() {
		return user_id;
	}
	/**
	 * 照片所属用户的ID的赋值方法
	 * @param user_id the user_id to set
	 */
	public void setUserId(String user_id) {
		this.user_id = user_id;
	}
	/**
	 * 照片所属的相册名的获取方法
	 * @return the album_name
	 */
	public String getAlbumName() {
		return album_name;
	}
	/**
	 *照片所属的相册名的赋值方法
	 * @param album_name the album_name to set
	 */
	public void setAlbumName(String album_name) {
		this.album_name = album_name;
	}
	/**
	 * 照片名的获取方法
	 * @return the picture_name
	 */
	public String getPictureName() {
		return picture_name;
	}
	/**
	 * 照片名的赋值方法的赋值方法
	 * @param picture_name the picture_name to set
	 */
	public void setPictureName(String picture_name) {
		this.picture_name = picture_name;
	}
	/**
	 * 发表本条评论的用户ID的获取方法
	 * @return the comment_user_id
	 */
	public String getCommentUserId() {
		return comment_user_id;
	}
	/**
	 * 发表本条评论的用户ID的赋值方法
	 * @param comment_user_id the comment_user_id to set
	 */
	public void setCommentUserId(String comment_user_id) {
		this.comment_user_id = comment_user_id;
	}
	/**
	 * 本条评论的内容的获取方法
	 * @return the comment_content
	 */
	public String getCommentContent() {
		return comment_content;
	}
	/**
	 * 本条评论的内容的赋值方法
	 * @param comment_content the comment_content to set
	 */
	public void setCommentContent(String comment_content) {
		this.comment_content = comment_content;
	}
	/**
	 * 发表评论的时间的获取方法
	 * @return the time
	 */
	public Timestamp getTime() {
		return time;
	}
	/**
	 * 发表评论的时间的赋值方法
	 * @param time the time to set
	 */
	public void setTime(Timestamp time) {
		this.time = time;
	}

	/**
	 * 一张照片中，评论ID的获取方法
	 * @return 评论ID
	 */
	public int getCommentId() {
		return comment_id;
	}
	/**
	 * 一张照片中，评论ID的赋值方法
	 * @param comment_id the comment ID to be set
	 */
	public void setCommentId(int comment_id) {
		this.comment_id = comment_id;
	}
	/**
	 * 评论可见标志的获取方法
	 * @return a boolean flag
	 */
	public int getIsValid() {
		return is_valid;
	}
	/**
	 * 评论可见标志的赋值方法
	 * @param is_valid the flag indicating validation of this comment
	 */
	public void setIsValid(int is_valid) {
		this.is_valid = is_valid;
	}
	/**
	 * 该评论所回复的那条评论的ID的获取方法
	 * @return replied comment ID
	 */
	public int getRepliedCommentId() {
		return replied_comment_id;
	}
	/**
	 * 该评论所回复的那条评论的ID的赋值方法
	 * @param replied_comment_id replied comment ID to set
	 */
	public void setRepliedCommentId(int replied_comment_id) {
		this.replied_comment_id = replied_comment_id;
	}
	/**
	 * Comment类的成员数量的获取方法
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
		return jsonObject.toString();
	}
	
	@Override
	public String toString() {
		return "Comment [user_id=" + user_id + ", album_name=" + album_name + ", picture_name=" + picture_name
				+ ", comment_id=" + comment_id + ", comment_user_id=" + comment_user_id + ", comment_content="
				+ comment_content + ", is_valid=" + is_valid + ", time=" + time + ", replied_comment_id="
				+ replied_comment_id + "]";
	}
	
}
