/**
 * 
 */
package tool.exception;

/**
 *
 */
public class RenameFolderErrorException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public RenameFolderErrorException() {
		// TODO Auto-generated constructor stub
		super("Rename folder failed.");
	}
	
	public RenameFolderErrorException(String msg) {
		// TODO Auto-generated constructor stub
		super(msg);
	}
}
