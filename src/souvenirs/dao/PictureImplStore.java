/**
 * 
 */
package souvenirs.dao;

import java.sql.Timestamp;
import java.util.List;

import souvenirs.Picture;
import tool.Store;

/**
 * @author Chenghong Li
 *
 */
public class PictureImplStore implements Store<Picture> {

	@Override
	public Picture format(List<Object> list) throws Exception {
		// TODO Auto-generated method stub
		Picture pic = new Picture();
		try {
			if (list.size() != Picture.getMemberCount())
				throw new Exception("Cannot transform result set from database to PersonalAlbum object!");
			else {
				pic.setUserId((String)list.get(0));
				pic.setAlbumName((String)list.get(1));
				pic.setFilename((String)list.get(2));
				pic.setFormat((String)list.get(3));
				pic.setDescription((String)list.get(4));
				pic.setUploadTimestamp((Timestamp)list.get(5));
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		return pic;
	}

}
