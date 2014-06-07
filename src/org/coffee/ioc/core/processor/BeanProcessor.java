package org.coffee.ioc.core.processor;
/**
 * <pre>
 * 如果某个Class实现该接口, 并且注册到Config中,则在
 * BeanFactory 初始化/析构 Bean的时候,对容器内每个Bean都使用
 * 该接口方法进行配置
 * </pre>
 * */
public interface BeanProcessor {
	/**
	 * 在调用Bean的init方法之前
	 * @param instance Bean对象
	 * */
	void beforeInit(Object instance);
	/**
	 * 在调用Bean的init方法之后
	 * @param instance Bean对象
	 * */
	void afterInit(Object instance);
	/**
	 * 在调用Bean的destroy方法之前
	 * @param instance Bean对象
	 * */
	void destroy(Object instance);
}
