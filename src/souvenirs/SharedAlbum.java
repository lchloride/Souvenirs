/**
 * 
 */
package souvenirs;

import java.sql.Timestamp;

/**
 * 共享相册(本质上是实体，虽然它存在Group表中)的Bean类
 * @author Chenghong Li
 */
public class SharedAlbum {
	/**
	 * 共享相册所属的小组ID
	 */
	private String group_id;
	/**
	 * 共享相册的名称
	 */
	private String shared_album_name;
	/**
	 * 共享相册的封面
	 */
	private String album_cover;
	/**
	 * 相册的创建时间
	 */
	private Timestamp create_timestamp;
	/**
	 * SharedAlbum成员数量
	 */
	final private static int MEMBER_COUNT = 4;
	
	/**
	 * 获得SharedAlbum成员数量
	 * @return SharedAlbum成员数量
	 */
	public static int getMemberCount() {
		return MEMBER_COUNT;
	}
	
	/**
	 * 获取共享相册所属的小组ID
	 * @return 共享相册所属的小组ID
	 */
	public String getGroupId() {
		return group_id;
	}
	
	/**
	 * 对共享相册所属的小组ID赋值
	 * @param group_id 共享相册所属的小组ID
	 */
	public void setGroupId(String group_id) {
		this.group_id = group_id;
	}
	
	/**
	 * 获取共享相册的名称
	 * @return 共享相册的名称
	 */
	public String getSharedAlbumName() {
		return shared_album_name;
	}
	
	/**
	 * 对共享相册的名称赋值
	 * @param shared_album_name 共享相册的名称
	 */
	public void setSharedAlbumName(String shared_album_name) {
		this.shared_album_name = shared_album_name;
	}
/*	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}*/
	
	/**
	 * 获取共享相册的封面
	 * @return 共享相册的封面
	 */
	public String getAlbumCover() {
		return album_cover;
	}
	
	/**
	 * 对共享相册的封面赋值
	 * @param album_cover 共享相册的封面
	 */
	public void setAlbumCover(String album_cover) {
		this.album_cover = album_cover;
	}
	
	/**
	 * 获取相册的创建时间
	 * @return 相册的创建时间
	 */
	public Timestamp getCreateTimestamp() {
		return create_timestamp;
	}
	
	/**
	 * 设置相册的创建时间
	 * @param create_timestamp 相册的创建时间
	 */
	public void setCreateTimestamp(Timestamp create_timestamp) {
		this.create_timestamp = create_timestamp;
	}
	
	
}
