package tool;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 读取/写入properties文件的工具类
 */
public class PropertyOper {
	private static Logger logger = Logger.getLogger(PropertyOper.class);
	/**
	 *  根据Key在制定的properties文件中读取Value
	 *  @param filePath properties文件路径
	 *  @param key 要获取value的key
	 *  @return key对应的value
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
	 *  读取Properties的全部信息
	 *  @param filePath properties文件路径
	 *  @return 存储所有key-value对的map
	 *  @throws IOException 文件IO操作失败会抛出异常
	 */
	public static Map<String, String> GetAllProperties(String filePath) throws IOException {
		Properties pps = new Properties();
		InputStream in = PropertyOper.class.getClassLoader().getResourceAsStream(filePath );
		pps.load(in);
		Enumeration<?> en = pps.propertyNames(); // 得到配置文件的名字
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
	 *  写入Properties信息
	 *  @param filePath properties文件路径
	 *  @param pKey 要写入的key
	 *  @param pValue key对应的value
	 *  @throws IOException 写入IO错误时抛出异常
	 */
	public static void WriteProperties(String filePath, String pKey, String pValue) throws IOException {
		Properties pps = new Properties();

		InputStream in = new FileInputStream(filePath);
		// 从输入流中读取属性列表（键和元素对）
		pps.load(in);
		// 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
		// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
		OutputStream out = new FileOutputStream(filePath);
		pps.setProperty(pKey, pValue);
		// 以适合使用 load 方法加载到 Properties 表中的格式，
		// 将此 Properties 表中的属性列表（键和元素对）写入输出流
		pps.store(out, "Update " + pKey + " name");
	}

	/**
	 *  通过HashMap批量写入Properties信息
	 *  @param  filePath properties文件路径
	 *  @param map 存储要写入文件的key-value对的Map
	 *  @throws IOException 当写入IO出现错误时抛出异常
	 */
	public static void WriteProperties(String filePath, Map<String, String> map) throws IOException {
		Properties pps = new Properties();

		InputStream in = new FileInputStream(filePath);
		// 从输入流中读取属性列表（键和元素对）
		pps.load(in);
//        pps.load(PropertyOper.class.getClassLoader().getResourceAsStream(filename));  
		// 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
		// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
		OutputStream out = new FileOutputStream(filePath);
		for (Entry<String, String> entry : map.entrySet()) {
			pps.setProperty(entry.getKey(), entry.getValue());

		}
		// 以适合使用 load 方法加载到 Properties 表中的格式，
		// 将此 Properties 表中的属性列表（键和元素对）写入输出流
		pps.store(out, "Update " + "" + " name");		
		GetAllProperties(filePath);
	}
}