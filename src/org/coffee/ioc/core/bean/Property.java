package org.coffee.ioc.core.bean;

public interface Property {
	/**
	 * <pre>
	 * 属性类型
	 * byManual : 通过setProperty配置
	 * byType : 通过setPropertyByType(a) | @Autowired
	 * byName : 通过setPropertyByName(a,b) | @Autowired("name")
	 * </pre>
	 * */
	public enum Type{
		byManual,
		byType,
		byName
	};
	/**
	 * 获得该Property的类型
	 * */
	public Type getType();
	/**
	 * 获得具体的value值
	 * */
	public Object getValue();
	
}
