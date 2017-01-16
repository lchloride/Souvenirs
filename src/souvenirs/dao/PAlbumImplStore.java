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
 * ʵ��Store�ӿڶ�PersonalAlbum������и�ֵ����
 */
public class PAlbumImplStore implements Store<PersonalAlbum> {

	/**
	 * ʵ��Store�ӿڵ�format��������һ������ת��ΪPersonalAlbum����
	 * @param list ͨ����ѯ�õ���һ������
	 * @return ���ɵ�PersonalAlbum����
	 * @throws Exception ��ֵʧ�ܻ򷵻�����������PersonalAlbum��Ҫ������������������׳��쳣
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
