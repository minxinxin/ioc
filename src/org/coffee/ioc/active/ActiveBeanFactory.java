package org.coffee.ioc.active;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.coffee.ioc.core.bean.BeanFactory;


public class ActiveBeanFactory implements BeanFactory{

	Map<String,Object> singletos = new HashMap<String,Object>(20);
	
	ActiveConfig config = null;
	
	private ActiveBeanFactory(ActiveConfig config){
		if(config == null)
			throw new RuntimeException("config 不能为null");
		this.config = config;
	}
	ActiveBeanFactory build(ActiveConfig config){
		ActiveBeanFactory  abf = new  ActiveBeanFactory(config);
		//初始化
		return abf;
	}
	public Object getBean(String name) {
		
		return null;
	}

	public Object getBean(Class<?> class0) {
		
		return null;
	}

	public boolean containsBean(String name) {
		return false;
	}

	public boolean isSingleton(String name) {
		return false;
	}
}
