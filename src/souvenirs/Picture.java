/**
 * 
 */
package souvenirs;

import java.sql.Timestamp;

/**
 * 照片实体对应的Bean类
 * @author Chenghong Li
 *
 */
public class Picture {
	/**
	 * 照片所属用户ID
	 */
	private String user_id;
	/**
	 * 照片所属的相册名
	 */
	private String album_name;
	/**
	 * 照片名
	 */
	private String filename;
	/**
	 * 照片的文件格式(后缀名)
	 */
	private String format;
	/**
	 * 照片的简介
	 */
	private String description;
	/**
	 * 照片上传的时间
	 */
	private Timestamp upload_timestamp;
	/**
	 * Picture类的成员数量
	 */
	final static private int MEMBER_COUNT = 6;
	
	/**
	 * 获取Picture类的成员数量 
	 * @return Picture类的成员数量
	 */
	public static int getMemberCount() {
		return MEMBER_COUNT;
	}
	
	/**
	 * 获取照片所属用户ID
	 * @return 照片所属用户ID
	 */
	public String getUserId() {
		return user_id;
	}
	
	/**
	 * 对照片所属用户ID赋值
	 * @param user_id 照片所属用户ID
	 */
	public void setUserId(String user_id) {
		this.user_id = user_id;
	}
	
	/**
	 * 获取照片所属的相册名
	 * @return 照片所属的相册名
	 */
	public String getAlbumName() {
		return album_name;
	}
	
	/**
	 * 对照片所属的相册名赋值
	 * @param album_name 照片所属的相册名
	 */
	public void setAlbumName(String album_name) {
		this.album_name = album_name;
	}
	
	/**
	 * 获取照片名
	 * @return 照片名
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * 对照片名进行赋值
	 * @param filename 照片名
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * 获取照片的文件格式(后缀名)
	 * @return 照片的文件格式(后缀名)
	 */
	public String getFormat() {
		return format;
	}
	
	/**
	 * 对照片的文件格式(后缀名)进行赋值
	 * <strong>本方法应该只再Store接口的format方法中被调用，单独给它赋值没有意义</strong>
	 * @param format 照片的文件格式(后缀名)
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
	/**
	 * 获取照片的简介
	 * @return 照片的简介
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * 对照片的简介赋值
	 * @param description 照片的简介
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * 获取照片上传的时间
	 * @return 照片上传的时间戳
	 */
	public Timestamp getUploadTimestamp() {
		return upload_timestamp;
	}
	
	/**
	 * 对照片上传的时间戳赋值
	 * @param upload_timestamp 照片上传的时间
	 */
	public void setUploadTimestamp(Timestamp upload_timestamp) {
		this.upload_timestamp = upload_timestamp;
	}
	
	/**
	 * 构造Picture类的文本显示
	 * @return 显示Picture类的文本
	 */
	@Override
	public String toString() {
		return "Picture [user_id=" + user_id + ", album_name=" + album_name + ", filename=" + filename + ", format="
				+ format + ", description=" + description + ", upload_timestamp=" + upload_timestamp + "]\n";
	}
}
