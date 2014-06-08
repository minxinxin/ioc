package org.coffee.ioc.core.bean;

import java.util.Map;

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
	Bean setPropertyByName(String pName,String rName);
	/**
	 * 设置引用属性类型, 该属性按照byType方式进行配置
	 * @param pName 按照byType方式进行配置属性
	 * */
	Bean setPropertyByType(String pName);
	/**
	 * 是否为单例类, 默认为true
	 * */
	Bean setSingleton(boolean isSingleton);
	/**
	 * 是否延迟初始化, 默认为true
	 * */
	Bean setLazy(boolean lazy);
	
	/**
	 * 获取的当前Bean中所有关于属性的配置
	 * */
	public Map<String, Property> getProperties();
	/**
	 * 当前Bean是否为单例类型
	 * */
	public boolean isSingleton();
	/**
	 * 是否延迟初始化
	 * */
	public boolean isLazy();
	/**
	 * 获得该bean指向的Class
	 * */
	public Class<?> getClass0();
	/**
	 * 获得Bean的名字
	 * */
	public String getName();
}
