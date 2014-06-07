package org.coffee.ioc.core.bean;

public interface Bean {
	/**
	 * 设置属性
	 * */
	Bean setProperty(String name,Object value);
	/**
	 * 设置引用属性类型
	 * @param pName 将要被设置的属性
	 * @param rName 引用的bean名字
	 * */
	Bean setPropertyRef(String pName,String rName);
	/**
	 * 是否为单例类, 默认为true
	 * */
	Bean setSingleton(boolean isSingleton);
	
}
