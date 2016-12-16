package tool;

import java.util.List;

public interface Store<T> {
	//接口，用于在DAO中对对应的类调用set方法进行赋值，赋值的结果被DB的execSQL方法所引用
	public abstract T format(List<Object> list);
}
