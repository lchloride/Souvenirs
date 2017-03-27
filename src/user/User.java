package user;

import java.sql.Timestamp;
import java.io.*;
/**
 * 用户信息Bean类
 */
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String user_id;
	//private String password;
	private String username;
	private String avatar;
	private int reload_times_max;
	private int load_timeout;
	private Timestamp create_timestamp;
	
	private static final int MEMBER_COUNT = 6;

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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the avatar
	 */
	public String getAvatar() {
		return avatar;
	}

	/**
	 * @param avatar the avatar to set
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	/**
	 * @return the reload_time_max
	 */
	public int getReloadTimesMax() {
		return reload_times_max;
	}

	/**
	 * @param reload_time_max the reload_time_max to set
	 */
	public void setReloadTimesMax(int reload_time_max) {
		this.reload_times_max = reload_time_max;
	}

	/**
	 * @return the load_timeout
	 */
	public int getLoadTimeout() {
		return load_timeout;
	}

	/**
	 * @param load_timeout the load_timeout to set
	 */
	public void setLoadTimeout(int load_timeout) {
		this.load_timeout = load_timeout;
	}

	/**
	 * @return the memberCount
	 */
	public static int getMemberCount() {
		return MEMBER_COUNT;
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
	
}
