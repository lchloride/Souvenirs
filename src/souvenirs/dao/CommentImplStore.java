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
 * @author Chenghong Li
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
			if (list.size() != Comment.getMemberCount())
				throw new Exception("Cannot transform result set from database to Comment object!");
			else {
				comment.setUserId((String)list.get(0));
				comment.setAlbumName((String)list.get(1));
				comment.setPictureName((String)list.get(2));
				comment.setCommentUserId((String)list.get(3));
				comment.setCommentContent((String)list.get(4));
				comment.setTime((Timestamp)list.get(5));
				comment.setReplyUserId(list.get(6)==null?"":(String)list.get(6));
				comment.setReplyContent(list.get(7)==null?"":(String)list.get(7));
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}		
		return comment;
	}

}
