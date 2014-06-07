package org.coffee.ioc.active;

public class Property {
	
	/**
	 * <pre>
	 * 属性类型
	 * byManual : 通过setProperty配置
	 * byType : 通过
	 * byName : 通过setPropertyRef(a,b)配置
	 * </pre>
	 * */
	public enum Type{
		byManual,
		byType,
		byName
	};
	Type type;
	/**
	 * <pre>
	 * byManual : 给定数值
	 * byType : null
	 * byName : 给定字符串
	 * </pre>
	 * */
	public Object value;
	
	public Property(Type type, Object value){
		this.type = type;
		this.value = value;
	}
}
