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
	 */
	public abstract T format(List<Object> list);
}
