package tool.exception;

public class BadRequestException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadRequestException(String msg) {
		super(msg);
	}
	
	public BadRequestException() {
		super("Bad Request");
	}
}
