package org.coffee.ioc.core.lifecycle;

import org.coffee.ioc.core.bean.BeanFactory;

/**
 * <pre>
 * 		Bean的生命周期
 * 初始化阶段:
 * 		newInstance: 实例化一个对象
 * 		setProperties: 进行配置属性
 * 		setBeanName: 给Bean对象它自己的名字
 * 		setBeanFactory: 给Bean, 当前实例化它的Factory对象
 * 		Processor's beforeInit: 调用容器中所有Processor处理器(每个Bean对象都会被处理)
 * 		init:如果实现了BeanLifeCycle接口调用init
 * 		Processor's afterInit  :调用容器中所有Processor处理器(每个Bean对象都会被处理)
 * 进入可以使用状态:
 * 		可以使用getBean方法获得容器中的Bean实例
 * 容器关闭:
 * 		Processor's destroy : 调用容器中所有Processor处理器(每个Bean对象都会被处理)
 * 		destroy: 如果实现了BeanLifeCycle接口则调用destroy方法 .
 * </pre>
 * */
public interface BeanLifeCycle {
	
	 void setBeanName(String name);
	 void setBeanFactory(BeanFactory bf);
	 void init();
	 void destroy();
}
