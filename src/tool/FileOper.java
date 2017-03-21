/**
 * 
 */
package tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

/**
 * �ļ����������࣬�������ơ�ɾ����������
 */
public class FileOper {
	private final static Logger logger = Logger.getLogger(FileOper.class);
	/**
	 * �������ļ�(�ļ���)
	 * @param old_name ԭʼ�ļ�(��)·��
	 * @param new_name �µ��ļ�(��)·��
	 * @return ����ִ�н����true����ִ�гɹ���false����ִ��ʧ��
	 */
	public static boolean rename(String old_name, String new_name) {
		// Rename folder name
		File file1 = new File(old_name);
		return file1.renameTo(new File(new_name));
	}

	/**
	 * ɾ���ļ�(�ļ���)
	 * @param path ��ɾ�����ļ�(��)����
	 * @return ����ִ�н����true����ִ�гɹ���false����ִ��ʧ��
	 */
	public static boolean deleteFile(String path) {
		File file = new File(path);
		// Check existence of file
		if (file.exists()) {
			// Check whether it id file or not
			if (file.isFile()) {
				// For file, delete it directly
				return file.delete();
			} else if (file.isDirectory()) {// �����������һ��Ŀ¼
				// For folder, delete all its contents first and then delete folder
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (!deleteFile(files[i].getPath()))
						return false;
				}
				return file.delete();
			}
		} else {
			// Variant file cannot be found.
			logger.warn("File/folder to be deleted does not exist. File/folder path=<" + path + ">");
			return false;
		}
		return true;
	}

	/**
	 * �����ļ���<strong>ע�⣺���Ŀ���ļ��Ѵ��ڣ��ò����Ḳ��Ŀ���ļ���</strong>
	 * @param f1 Դ�ļ�
	 * @param f2 Ŀ���ļ�
	 * @return ����ִ�н����true����ִ�гɹ���false����ִ��ʧ��
	 */
	public static boolean copyFile(String f1, String f2) {
		try {
			// length is the memory threshold of copying file 
			int length = 0;
			try {
				length = Integer.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MEMORY_THRESHOLD"), 16);
			} catch (NumberFormatException e) {
				// TODO: handle exception
				length = 2097152;// default is 2097152
			}		
			FileInputStream in = new FileInputStream(new File(f1));
			FileOutputStream out = new FileOutputStream(new File(f2));
			byte[] buffer = new byte[length];
			while (true) {
				int ins = in.read(buffer);
				if (ins == -1) {
					in.close();
					out.flush();
					out.close();
					return true;
				} else
					out.write(buffer, 0, ins);
			}
		} catch (Exception e) {
			logger.error("Copying file from <"+f1+"> to <"+f2+"> failed. Error: "+e.getMessage());
			return false;
		}
		
	}
}
