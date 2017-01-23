package tool;

import org.apache.log4j.Logger;

/**
 * ��֤����ز���(��ȡ����֤)�Ĺ�����<br>
 * ��֤��ͼƬ���ڷ�������resĿ¼�У���Ӧ�Ĵ𰸴洢�������ļ��С���֤�뼰��ͼƬ�ļ�����������verifycode_XXX������XXX�Ǹ���֤��ı�š�
 * ͨ����֤������ƾͿ����ҵ���֤��ͼƬ���������ļ����ҵ���𰸡�
 * <strong>ע�⣺��֤���ڷ������ж���png��ʽ</strong>
 */
public class VerifyCode {
	private static Logger logger = Logger.getLogger(VerifyCode.class);
	
	/**
	 * �����ȡһ����֤�����ƣ���ʽΪverifycode_XXX������XXX�Ǹ���֤��ı�š�
	 * @return ��ȡ����֤������
	 */
	public static String getVerifyCode() {
		int verifycode_num=1;
		try {
			verifycode_num=Integer.valueOf(PropertyOper.GetValueByKey("souvenirs.properties", "verifycode_number"));
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("verifycode_number in souvernir.properties is invalid.");
		}
		int idx = (int)(Math.random()*verifycode_num)+1;
		logger.debug(idx+", "+verifycode_num);
		return new String("verifycode"+idx);
	}
	
	/**
	 * ����������֤���Ƿ���ȷ
	 * @param verifycode_name �������֤������֣�����verifycode_XXX
	 * @param checkcode �������֤��
	 * @return �������true��ʾ��ȷ��false��ʾ����
	 */
	public static boolean checkVerifyCodeAns(String verifycode_name, String checkcode) {
		String ans = PropertyOper.GetValueByKey("souvenirs.properties", verifycode_name);
		if (ans == null ) {
			logger.error("Cannot find the value for property--"+verifycode_name);
			return false;
		}else {
			if (ans.equalsIgnoreCase(checkcode)) 
				return true;
			else {
				logger.info("User input verifycode--"+verifycode_name+" wrongly");
				return false;
			}
		}
	}
}
