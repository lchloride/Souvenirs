package tool;

import org.apache.log4j.Logger;

public class VerifyCode {
	private static Logger logger = Logger.getLogger(VerifyCode.class);
	
	public static String getVerifyCode() {
		int verifycode_num=1;
		try {
			verifycode_num=Integer.valueOf(PropertyOper.GetValueByKey("souvenirs.properties", "verifycode_number"));
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("verifycode_number in souvernir.properties is invalid.");
		}
		int idx = (int)Math.random()*(verifycode_num-1)+1;
		return new String("verifycode"+idx);
	}
	
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
