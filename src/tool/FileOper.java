/**
 * 
 */
package tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

/**
 *
 */
public class FileOper {
	private final static Logger logger = Logger.getLogger(FileOper.class);

	public static boolean rename(String old_name, String new_name) {
		// Rename folder name
		File file1 = new File(old_name);
		return file1.renameTo(new File(new_name));
	}

	// �ݹ�ɾ���ļ���
	public static boolean deleteFile(String path) {
		File file = new File(path);
		if (file.exists()) {// �ж��ļ��Ƿ����
			if (file.isFile()) {// �ж��Ƿ����ļ�
				return file.delete();// ɾ���ļ�
			} else if (file.isDirectory()) {// �����������һ��Ŀ¼
				File[] files = file.listFiles();// ����Ŀ¼�����е��ļ� files[];
				for (int i = 0; i < files.length; i++) {// ����Ŀ¼�����е��ļ�
					if (!deleteFile(files[i].getPath()))
						return false;// ��ÿ���ļ�������������е���
				}
				return file.delete();// ɾ���ļ���
			}
		} else {
			logger.warn("Deleted file does not exist. File path=<" + path + ">");
			return false;
		}
		return true;
	}

	public static boolean copyFile(String f1, String f2) {
		try {
			int length = Integer.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MEMORY_THRESHOLD"), 16);// default is 2097152
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
