package tool;

import java.util.List;

/**
 * 存储JavaBean数据的接口，用于在DAO中对对应的类调用set方法进行赋值，赋值的结果被DB的execSQL方法所引用
 */
public interface Store<T> {
	/**
	 * 抽象方法format，用于对Bean类的成员进行赋值
	 * @param list 要存入Bean类的数据。顺序由提供者决定
	 * @return 生成的Bean对象 
	 * @throws Exception 无法将数据库记录转换成T类型的对象时抛出异常。异常的抛出在实现接口时指定
	 */
	public abstract T format(List<Object> list) throws Exception;
}
