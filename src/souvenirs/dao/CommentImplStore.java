/**
 * 
 */
package souvenirs.dao;

import java.sql.Timestamp;
import java.util.List;

import souvenirs.Comment;
import tool.Store;

/**
 * Comment类实现Store接口进行数据库操作的实现类
 *
 */
public class CommentImplStore implements Store<Comment> {

	/**
	 * @see tool.Store#format(List)
	 */
	@Override
	public Comment format(List<Object> list) throws Exception {
		// TODO Auto-generated method stub
		Comment comment = new Comment();
		try {
			if (list.size() != Comment.getMemberCount())// Comment.getMemberCount() should be 9
				throw new Exception("Cannot transform result set from database to Comment object!");
			else {
				comment.setUserId((String)list.get(0));
				comment.setAlbumName((String)list.get(1));
				comment.setPictureName((String)list.get(2));
				comment.setCommentId((int)list.get(3));
				comment.setCommentUserId((String)list.get(4));
				comment.setCommentContent((String)list.get(5));
				comment.setIsValid((int)list.get(6));
				comment.setTime((Timestamp)list.get(7));
				comment.setRepliedCommentId(list.get(8)==null?0:(int)list.get(8));// list.get(8) should be a positive integer form 1 or null
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}		
		return comment;
	}

}
