package tool;

import java.util.List;

public interface Store<T> {
	//�ӿڣ�������DAO�жԶ�Ӧ�������set�������и�ֵ����ֵ�Ľ����DB��execSQL����������
	public abstract T format(List<Object> list);
}
