/**
 * 
 */
package souvenirs;

import java.sql.Timestamp;
import java.util.List;

import tool.Store;

/**
 * @author Chenghong Li
 *
 */
public class PAlbumImplStore implements Store<PersonalAlbum> {

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see tool.Store#format(java.util.List)
	 */
	@Override
	public PersonalAlbum format(List<Object> list) throws Exception {
		// TODO Auto-generated method stub
		PersonalAlbum pAlbum = new PersonalAlbum();
		try {
			if (list.size() != PersonalAlbum.para_count)
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
