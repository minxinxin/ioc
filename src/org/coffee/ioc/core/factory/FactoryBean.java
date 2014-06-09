package org.coffee.ioc.core.factory;
/**
 * Bean如果继承了该接口, 则在生产实例的时候, 会调用getInstance
 * */
public interface FactoryBean {
	/**
	 * 函数获得的对象不受容器的生命周期管理
	 * */
	Object getObject();
	
	public Class<?> getObjectType();
	
	public boolean isSingleton();
}
