/**
 * 
 */
package souvenirs.dao;

import java.sql.Timestamp;
import java.util.List;

import souvenirs.PersonalAlbum;
import souvenirs.SharedAlbum;
import tool.Store;

/**
 * 实现Store接口对SharedAlbum对象进行赋值的类
 * @author Chenghong Li
 */
public class SAlbumImplStore implements Store<SharedAlbum> {

	/**
	 * 实现Store接口的format方法，将一行数据转换为PersonalAlbum对象
	 * @param list 通过查询得到的一行数据
	 * @return 生成的PersonalAlbum对象
	 * @throws Exception 赋值失败或返回数据数量与PersonalAlbum需要的数量不符的情况会抛出异常
	 * @see tool.Store#format(java.util.List)
	 */
	@Override
	public SharedAlbum format(List<Object> list) throws Exception {
		// TODO Auto-generated method stub
		SharedAlbum sAlbum = new SharedAlbum();
		try {
			if (list.size() != SharedAlbum.getMemberCount())
				throw new Exception("Cannot transform result set from database to PersonalAlbum object!");
			else {
				sAlbum.setGroupId((String)list.get(0));
				sAlbum.setSharedAlbumName((String)list.get(1));
				//sAlbum.setIntro((String)list.get(2));
				sAlbum.setAlbumCover((String)list.get(2));
				sAlbum.setCreateTimestamp((Timestamp)list.get(3));
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		return sAlbum;
	}
}
