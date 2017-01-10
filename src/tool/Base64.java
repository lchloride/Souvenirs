package tool;

import java.io.UnsupportedEncodingException;

import sun.misc.*;

/**
 * Base64������빤����
 * @author Chenghong Li
 */
public class Base64 {
    /**
     * �Դ�����ַ�������Base64����
     * @param str ��������ַ�����<strong>������ַ����谴��utf-8����</strong>
     * @return ����õ�Base64�ַ���
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
     * ��base64�ַ�������
     * @param s base64������ַ���
     * @return �������ַ�������utf-8����<strong>ע�⣺����������ַ��к��в��ɼ��ַ������ܻ���������������Ӧʹ��decodeBytes����</strong>
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
     * ��base64�ַ�������
     * @param s base64������ַ���
     * @return �������ֽ�����
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
