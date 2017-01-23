package souvenirs;

import java.sql.Timestamp;
/**
 * 个人相册实体的Bean类
 *
 */
public class PersonalAlbum {
	/**
	 * 相册所属用户ID
	 */
	private String user_id;
	/**
	 * 相册的名称
	 */
	private String album_name;
	/**
	 * 相册简介
	 */
	private String intro;
	/**
	 * 相册封面的相对路径
	 */
	private String album_cover;
	/**
	 * 相册的创建时间
	 */
	private Timestamp create_timestamp;
	/**
	 * PersonalAlbum类的成员数量
	 */
	final private static int MEMBER_COUNT = 5;
	
	/**
	 * 获取PersonalAlbum类的成员数量的方法
	 * @return 一个整数，PersonalAlbum类的成员数量
	 */
	public static int getMemberCount() {
		return MEMBER_COUNT;
	}
	
	/**
	 * 获取相册所属的用户ID
	 * @return 一个字符串，是该用户的ID
	 */
	public String getUserId() {
		return user_id;
	}
	
	/**
	 * 对相册所属的用户ID进行赋值
	 * @param user_id 待赋值的用户名字符串
	 */
	public void setUserId(String user_id) {
		this.user_id = user_id;
	}
	
	/**
	 * 获取相册名
	 * @return 一个字符串，为相册名
	 */
	public String getAlbumName() {
		return album_name;
	}
	
	/**
	 * 对相册名赋值
	 * @param album_name 待赋值的相册名字符串
	 */
	public void setAlbumName(String album_name) {
		this.album_name = album_name;
	}
	
	/**
	 * 获取相册的简介
	 * @return 一个字符串，为相册的简介，不超过200个字符
	 */
	public String getIntro() {
		return intro;
	}
	
	/**
	 * 对相册的简介赋值
	 * @param intro 存放简介的字符串
	 */
	public void setIntro(String intro) {
		this.intro = intro;
	}
	
	/**
	 * 获取相册封面的相对路径
	 * @return 一个字符串，为相册封面的相对路径
	 */
	public String getAlbumCover() {
		return album_cover;
	}
	
	/**
	 * 对相册封面路径进行赋值
	 * @param album_cover 封面路径
	 */
	public void setAlbumCover(String album_cover) {
		this.album_cover = album_cover;
	}
	
	/**
	 * 获取相册创建的时间戳
	 * @return 一个Timestamp对象，存放了时间戳
	 */
	public Timestamp getCreateTimestamp() {
		return create_timestamp;
	}
	
	/**
	 * 对时间戳进行赋值
	 * @param create_timestamp 待赋值的时间戳
	 */
	public void setCreateTimestamp(Timestamp create_timestamp) {
		this.create_timestamp = create_timestamp;
	}
	
	
}
