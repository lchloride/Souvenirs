/**
 * 
 */
package group.dao;

import java.sql.Timestamp;
import java.util.List;

import group.Group;
import tool.Store;

/**
 * 实现Group类对应的Store接口的实现类
 */
public class GroupImplStore implements Store<Group> {
	final public static int GROUP_ID_IDX = 0;
	final public static int GROUP_NAME_IDX = 1;
	final public static int INTRO_IDX = 2;
	final public static int SALBUM_NAME_IDX = 3;
	final public static int ALBUM_COVER_IDX = 4;
	final public static int CREATE_TIMESTAMP_IDX = 5;
	
	/**
	 * Store接口的format方法的实现
	 * @param list 数据库查询结果中的一行数据
	 * @return 根据数据生成的Group对象
	 * @see tool.Store#format(List)
	 */
	@Override
	public Group format(List<Object> list) throws Exception {
		// TODO Auto-generated method stub
		Group group = new Group();
		try {
			if (list.size() != Group.getMemberCount())
				throw new Exception("Cannot transform result set from database to Group object!");
			else {
				group.setGroupId((String)list.get(GROUP_ID_IDX));
				group.setGroupName((String)list.get(GROUP_NAME_IDX));
				group.setSharedAlbumName((String)list.get(SALBUM_NAME_IDX));
				group.setIntro((String)list.get(INTRO_IDX));
				group.setAlbumCover((String)list.get(ALBUM_COVER_IDX));
				group.setCreateTimestamp((Timestamp)list.get(CREATE_TIMESTAMP_IDX));
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		return group;
	}

}
