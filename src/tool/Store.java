package tool;

import java.util.List;

/**
 * �洢JavaBean���ݵĽӿڣ�������DAO�жԶ�Ӧ�������set�������и�ֵ����ֵ�Ľ����DB��execSQL����������
 */
public interface Store<T> {
	/**
	 * ���󷽷�format�����ڶ�Bean��ĳ�Ա���и�ֵ
	 * @param list Ҫ����Bean������ݡ�˳�����ṩ�߾���
	 * @return ���ɵ�Bean���� 
	 * @throws Exception �޷������ݿ��¼ת����T���͵Ķ���ʱ�׳��쳣���쳣���׳���ʵ�ֽӿ�ʱָ��
	 */
	public abstract T format(List<Object> list) throws Exception;
}
