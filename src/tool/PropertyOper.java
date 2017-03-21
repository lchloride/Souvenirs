package tool;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * ��ȡ/д��properties�ļ��Ĺ�����
 */
public class PropertyOper {
	private static Logger logger = Logger.getLogger(PropertyOper.class);
	/**
	 *  ����Key���ƶ���properties�ļ��ж�ȡValue
	 *  @param filePath properties�ļ�·��
	 *  @param key Ҫ��ȡvalue��key
	 *  @return key��Ӧ��value
	 */
	public static String GetValueByKey(String filePath, String key) {
		Properties pps = new Properties();
		String value = null;
		try {
			//InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			InputStream in = PropertyOper.class.getClassLoader().getResourceAsStream(filePath );
			pps.load(in);
			value = pps.getProperty(key);
			//System.out.println(key + " = " + value);
		} catch (IOException e) {
			logger.warn("Cannot open property file <"+filePath+">", e);
			return null;
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn("Finding property value of key <"+key+">in file <"+filePath+"> failed.", e);
			return null;
		}
		return value;
	}

	/**
	 *  ��ȡProperties��ȫ����Ϣ
	 *  @param filePath properties�ļ�·��
	 *  @return �洢����key-value�Ե�map
	 *  @throws IOException �ļ�IO����ʧ�ܻ��׳��쳣
	 */
	public static Map<String, String> GetAllProperties(String filePath) throws IOException {
		Properties pps = new Properties();
		InputStream in = PropertyOper.class.getClassLoader().getResourceAsStream(filePath );
		pps.load(in);
		Enumeration<?> en = pps.propertyNames(); // �õ������ļ�������
		Map<String, String> result = new HashMap<>();
		while (en.hasMoreElements()) {
			String strKey = (String) en.nextElement();
			String strValue = pps.getProperty(strKey);
			result.put(strKey, strValue);
			//System.out.println(strKey + "=" + strValue);
		}
		return result;
	}

	/**
	 *  д��Properties��Ϣ
	 *  @param filePath properties�ļ�·��
	 *  @param pKey Ҫд���key
	 *  @param pValue key��Ӧ��value
	 *  @throws IOException д��IO����ʱ�׳��쳣
	 */
	public static void WriteProperties(String filePath, String pKey, String pValue) throws IOException {
		Properties pps = new Properties();

		InputStream in = new FileInputStream(filePath);
		// ���������ж�ȡ�����б�����Ԫ�ضԣ�
		pps.load(in);
		// ���� Hashtable �ķ��� put��ʹ�� getProperty �����ṩ�����ԡ�
		// ǿ��Ҫ��Ϊ���Եļ���ֵʹ���ַ���������ֵ�� Hashtable ���� put �Ľ����
		OutputStream out = new FileOutputStream(filePath);
		pps.setProperty(pKey, pValue);
		// ���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ��
		// ���� Properties ���е������б�����Ԫ�ضԣ�д�������
		pps.store(out, "Update " + pKey + " name");
	}

	/**
	 *  ͨ��HashMap����д��Properties��Ϣ
	 *  @param  filePath properties�ļ�·��
	 *  @param map �洢Ҫд���ļ���key-value�Ե�Map
	 *  @throws IOException ��д��IO���ִ���ʱ�׳��쳣
	 */
	public static void WriteProperties(String filePath, Map<String, String> map) throws IOException {
		Properties pps = new Properties();

		InputStream in = new FileInputStream(filePath);
		// ���������ж�ȡ�����б�����Ԫ�ضԣ�
		pps.load(in);
//        pps.load(PropertyOper.class.getClassLoader().getResourceAsStream(filename));  
		// ���� Hashtable �ķ��� put��ʹ�� getProperty �����ṩ�����ԡ�
		// ǿ��Ҫ��Ϊ���Եļ���ֵʹ���ַ���������ֵ�� Hashtable ���� put �Ľ����
		OutputStream out = new FileOutputStream(filePath);
		for (Entry<String, String> entry : map.entrySet()) {
			pps.setProperty(entry.getKey(), entry.getValue());

		}
		// ���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ��
		// ���� Properties ���е������б�����Ԫ�ضԣ�д�������
		pps.store(out, "Update " + "" + " name");		
		GetAllProperties(filePath);
	}
}