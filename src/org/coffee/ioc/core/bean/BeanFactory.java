package org.coffee.ioc.core.bean;


public interface BeanFactory {
	
	String FACTORY_BEAN_PREFIX = "&";
	/**
	 * 通过名字获得Bean对象
	 * */
	Object getBean(String name);
	/**
	 * 通过指定类型获取Bean对象
	 * */
	<T>T getBean(Class<T> class0);
	/**
	 * 判断是否包含指定Bean名字的Bean对象
	 * */
	boolean containsBean(String name);
	/**
	 * 判断指定名字的Bean是否为单例类型
	 * */
	boolean isSingleton(String name);
	
	/**
	 * 关闭BeanFactory
	 * */
	void closeBeanFactory();
	
}