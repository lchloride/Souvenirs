/**
 * 
 */
package tool.exception;

/**
 * ������ʧ��ʱ�׳����쳣
 */
public class RenameFolderErrorException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Ĭ�Ϲ��캯��
	 */
	public RenameFolderErrorException() {
		// TODO Auto-generated constructor stub
		super("Rename folder failed.");
	}
	
	/**
	 * ���Զ�����Ϣ�Ĺ��캯��
	 * @param msg �Զ�����Ϣ
	 */
	public RenameFolderErrorException(String msg) {
		// TODO Auto-generated constructor stub
		super(msg);
	}
}
