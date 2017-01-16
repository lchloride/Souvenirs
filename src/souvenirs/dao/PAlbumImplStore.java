/**
 * 
 */
package souvenirs.dao;

import java.sql.Timestamp;
import java.util.List;

import souvenirs.PersonalAlbum;
import tool.Store;

/**
 * @author Chenghong Li
 * 实现Store接口对PersonalAlbum对象进行赋值的类
 */
public class PAlbumImplStore implements Store<PersonalAlbum> {

	/**
	 * 实现Store接口的format方法，将一行数据转换为PersonalAlbum对象
	 * @param list 通过查询得到的一行数据
	 * @return 生成的PersonalAlbum对象
	 * @throws Exception 赋值失败或返回数据数量与PersonalAlbum需要的数量不符的情况会抛出异常
	 * @see tool.Store#format(java.util.List)
	 */
	@Override
	public PersonalAlbum format(List<Object> list) throws Exception {
		// TODO Auto-generated method stub
		PersonalAlbum pAlbum = new PersonalAlbum();
		try {
			if (list.size() != PersonalAlbum.getMemberCount())
				throw new Exception("Cannot transform result set from database to PersonalAlbum object!");
			else {
				pAlbum.setUserId((String)list.get(0));
				pAlbum.setAlbumName((String)list.get(1));
				pAlbum.setIntro((String)list.get(2));
				pAlbum.setAlbumCover((String)list.get(3));
				pAlbum.setCreateTimestamp((Timestamp)list.get(4));
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		return pAlbum;
	}
}
