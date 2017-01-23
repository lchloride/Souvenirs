package tool.exception;
/**
 * �Զ����쳣�����������ʱ��������Ӧ��HTTP 400 Bad Request����
 */
public class BadRequestException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * �������Ĺ��캯��
	 * @param msg ������Ϣ
	 */
	public BadRequestException(String msg) {
		super(msg);
	}
	
	/**
	 * Ĭ�Ϲ��캯��
	 */
	public BadRequestException() {
		super("Bad Request");
	}
}
