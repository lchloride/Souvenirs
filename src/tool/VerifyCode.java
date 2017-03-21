package tool;

import org.apache.log4j.Logger;

/**
 * 验证码相关操作(获取、验证)的工具类<br>
 * 验证码图片存在服务器的res目录中，对应的答案存储在配置文件中。验证码及其图片文件的名字形如verifycode_XXX，其中XXX是该验证码的编号。
 * 通过验证码的名称就可以找到验证码图片，在配置文件中找到其答案。
 * <strong>注意：验证码在服务器中都是png格式</strong>
 */
public class VerifyCode {
	private static Logger logger = Logger.getLogger(VerifyCode.class);
	
	/**
	 * 随机获取一个验证码名称，格式为verifycode_XXX，其中XXX是该验证码的编号。
	 * @return 获取的验证码名称
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
	 * 检查输入的验证码是否正确
	 * @param verifycode_name 待检测验证码的名字，形如verifycode_XXX
	 * @param checkcode 输入的验证码
	 * @return 检查结果。true表示正确；false表示错误
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
