package org.coffee.ioc.core.bean;


public interface BeanFactory {
	
	/**
	 * 通过名字获得Bean对象
	 * */
	Object getBean(String name);
	/**
	 * 通过指定类型获取Bean对象
	 * */
	Object getBean(Class<?> class0);
	/**
	 * 判断是否包含指定Bean名字的Bean对象
	 * */
	boolean containsBean(String name);
	/**
	 * 判断指定名字的Bean是否为单例类型
	 * */
	boolean isSingleton(String name);
	
}