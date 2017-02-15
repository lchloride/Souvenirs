/**
 * 
 */
package tool.exception;

/**
 * 重命名失败时抛出的异常
 */
public class RenameFolderErrorException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 默认构造函数
	 */
	public RenameFolderErrorException() {
		// TODO Auto-generated constructor stub
		super("Rename folder failed.");
	}
	
	/**
	 * 带自定义消息的构造函数
	 * @param msg 自定义消息
	 */
	public RenameFolderErrorException(String msg) {
		// TODO Auto-generated constructor stub
		super(msg);
	}
}
