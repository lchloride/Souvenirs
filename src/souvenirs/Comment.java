/**
 * 
 */
package souvenirs;

import java.sql.Timestamp;

import org.json.JSONObject;

/**
 * ����(Comment)ʵ���Bean��
 */
public class Comment {
	/**
	 * ��Ƭ�����û���ID
	 */
	private String user_id;
	/**
	 * ��Ƭ�����������
	 */
	private String album_name;
	/**
	 * ��Ƭ����ʵ���Ǵ洢�ڷ������ϵ��ļ���
	 */
	private String picture_name;
	/**
	 * ��һ����Ƭ�и������۵�ID
	 */
	private int comment_id;
	/**
	 * ���������۵��û�ID
	 */
	private String comment_user_id;
	/**
	 * �������۵�����
	 */
	private String comment_content;
	/**
	 * �����Ƿ�ɼ��ı�־
	 */
	private int is_valid;
	/**
	 * �������۵�ʱ��
	 */
	private Timestamp time;
	/**
	 * ��һ����Ƭ�У������������ظ������۵�ID����������κ�һ�����۵Ļظ���Ϊ���ַ���
	 */
	private int replied_comment_id;
	/**
	 * Comment��ĳ�Ա����
	 */
	private final static int MEMBER_COUNT = 9;
	
	/**
	 * ��Ƭ�����û���ID�Ļ�ȡ����
	 * @return the user_id
	 */
	public String getUserId() {
		return user_id;
	}
	/**
	 * ��Ƭ�����û���ID�ĸ�ֵ����
	 * @param user_id the user_id to set
	 */
	public void setUserId(String user_id) {
		this.user_id = user_id;
	}
	/**
	 * ��Ƭ������������Ļ�ȡ����
	 * @return the album_name
	 */
	public String getAlbumName() {
		return album_name;
	}
	/**
	 *��Ƭ������������ĸ�ֵ����
	 * @param album_name the album_name to set
	 */
	public void setAlbumName(String album_name) {
		this.album_name = album_name;
	}
	/**
	 * ��Ƭ���Ļ�ȡ����
	 * @return the picture_name
	 */
	public String getPictureName() {
		return picture_name;
	}
	/**
	 * ��Ƭ���ĸ�ֵ�����ĸ�ֵ����
	 * @param picture_name the picture_name to set
	 */
	public void setPictureName(String picture_name) {
		this.picture_name = picture_name;
	}
	/**
	 * ���������۵��û�ID�Ļ�ȡ����
	 * @return the comment_user_id
	 */
	public String getCommentUserId() {
		return comment_user_id;
	}
	/**
	 * ���������۵��û�ID�ĸ�ֵ����
	 * @param comment_user_id the comment_user_id to set
	 */
	public void setCommentUserId(String comment_user_id) {
		this.comment_user_id = comment_user_id;
	}
	/**
	 * �������۵����ݵĻ�ȡ����
	 * @return the comment_content
	 */
	public String getCommentContent() {
		return comment_content;
	}
	/**
	 * �������۵����ݵĸ�ֵ����
	 * @param comment_content the comment_content to set
	 */
	public void setCommentContent(String comment_content) {
		this.comment_content = comment_content;
	}
	/**
	 * �������۵�ʱ��Ļ�ȡ����
	 * @return the time
	 */
	public Timestamp getTime() {
		return time;
	}
	/**
	 * �������۵�ʱ��ĸ�ֵ����
	 * @param time the time to set
	 */
	public void setTime(Timestamp time) {
		this.time = time;
	}

	/**
	 * һ����Ƭ�У�����ID�Ļ�ȡ����
	 * @return ����ID
	 */
	public int getCommentId() {
		return comment_id;
	}
	/**
	 * һ����Ƭ�У�����ID�ĸ�ֵ����
	 * @param comment_id the comment ID to be set
	 */
	public void setCommentId(int comment_id) {
		this.comment_id = comment_id;
	}
	/**
	 * ���ۿɼ���־�Ļ�ȡ����
	 * @return a boolean flag
	 */
	public int getIsValid() {
		return is_valid;
	}
	/**
	 * ���ۿɼ���־�ĸ�ֵ����
	 * @param is_valid the flag indicating validation of this comment
	 */
	public void setIsValid(int is_valid) {
		this.is_valid = is_valid;
	}
	/**
	 * ���������ظ����������۵�ID�Ļ�ȡ����
	 * @return replied comment ID
	 */
	public int getRepliedCommentId() {
		return replied_comment_id;
	}
	/**
	 * ���������ظ����������۵�ID�ĸ�ֵ����
	 * @param replied_comment_id replied comment ID to set
	 */
	public void setRepliedCommentId(int replied_comment_id) {
		this.replied_comment_id = replied_comment_id;
	}
	/**
	 * Comment��ĳ�Ա�����Ļ�ȡ����
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
