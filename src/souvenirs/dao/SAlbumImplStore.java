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
 * ʵ��Store�ӿڶ�SharedAlbum������и�ֵ����
 * @author Chenghong Li
 */
public class SAlbumImplStore implements Store<SharedAlbum> {

	/**
	 * ʵ��Store�ӿڵ�format��������һ������ת��ΪPersonalAlbum����
	 * @param list ͨ����ѯ�õ���һ������
	 * @return ���ɵ�PersonalAlbum����
	 * @throws Exception ��ֵʧ�ܻ򷵻�����������PersonalAlbum��Ҫ������������������׳��쳣
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
