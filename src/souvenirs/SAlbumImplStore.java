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
public class SAlbumImplStore implements Store<SharedAlbum> {

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see tool.Store#format(java.util.List)
	 */
	@Override
	public SharedAlbum format(List<Object> list) throws Exception {
		// TODO Auto-generated method stub
		SharedAlbum sAlbum = new SharedAlbum();
		try {
			if (list.size() != PersonalAlbum.para_count)
				throw new Exception("Cannot transform result set from database to PersonalAlbum object!");
			else {
				sAlbum.setGroupId((String)list.get(0));
				sAlbum.setSharedAlbumName((String)list.get(1));
				sAlbum.setIntro((String)list.get(2));
				sAlbum.setAlbumCover((String)list.get(3));
				sAlbum.setCreateTimestamp((Timestamp)list.get(4));
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		return sAlbum;
	}
}
