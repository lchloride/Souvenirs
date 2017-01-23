package tool.exception;
/**
 * 自定义异常，在请求错误时发生，对应了HTTP 400 Bad Request错误
 */
public class BadRequestException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * 带参数的构造函数
	 * @param msg 错误信息
	 */
	public BadRequestException(String msg) {
		super(msg);
	}
	
	/**
	 * 默认构造函数
	 */
	public BadRequestException() {
		super("Bad Request");
	}
}
