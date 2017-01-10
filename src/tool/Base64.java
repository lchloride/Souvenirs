package tool;

import java.io.UnsupportedEncodingException;

import sun.misc.*;

/**
 * Base64编码解码工具类
 * @author Chenghong Li
 */
public class Base64 {
    /**
     * 对传入的字符串进行Base64编码
     * @param str 待编码的字符串，<strong>传入的字符串需按照utf-8编码</strong>
     * @return 编码好的Base64字符串
     */
    public static String encode(String str) {  
        byte[] b = null;  
        String s = null;  
        try {  
            b = str.getBytes("utf-8");  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        if (b != null) {  
            s = new BASE64Encoder().encode(b);  
        }  
        return s;  
    }  
  
    /**
     * 对base64字符串解码
     * @param s base64编码的字符串
     * @return 解码后的字符串，按utf-8编码<strong>注意：如果解析的字符中含有不可见字符，可能会解析出错。上述情况应使用decodeBytes方法</strong>
     * @see #decodeBytes(String)
     */
    public static String decode(String s) {  
    	s = s.replace(' ', '+');
        byte[] b = null;  
        String result = null;  
        if (s != null) {  
            BASE64Decoder decoder = new BASE64Decoder();  
            try {  
                b = decoder.decodeBuffer(s);  
                result = new String(b, "utf-8"); 
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return result;  
    }  
    
    /**
     * 对base64字符串解码
     * @param s base64编码的字符串
     * @return 解码后的字节数组
     */
    public static byte[] decodeBytes(String s) {  
        byte[] b = null;  
        if (s != null) {  
            BASE64Decoder decoder = new BASE64Decoder();  
            try {  
                b = decoder.decodeBuffer(s);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return b;  
    }  
}
