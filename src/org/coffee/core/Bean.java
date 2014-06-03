package org.coffee.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 记录Bean的配置.
 * 该类通过Config的addBean方法获得, 
 * 注入的方式都为直接对属性进行操作,
 * 而不调用getXXX/setXXX方法进行操作.
 * */
public class Bean {
	
	//bean指向的class
	Class<?> cls ;
	//需要注入的属性登记, 同名属性则进行覆盖
	Map<String,Object> propertys = new HashMap<String,Object>();
	
	//引用类型属性登记
	Map<String,String> propertyRefs = new HashMap<String,String>();
	//是否是单例, false为每次getBean都会进行重新构造
	boolean single = true;
	//bean的名字
	String beanName;
	
	public Bean(Config config, Class<?> cls){
		config.addBean(this);
		this.cls = cls;
		this.beanName = cls.getName();
	}
	/**
	 * 添加K-V对
	 * */
	public Bean setProperty(String name,Object value){
		propertys.put(name, value);
		return this;
	}
	/**
	 * 设置引用IoC容器中的对象, name为属性名字, beanName为将要被引用注入的Bean名字
	 * */
	public Bean setPropertyRef(String pName,String rName){
		propertyRefs.put(pName, rName);
		return this;
	}
	public Class<?> getCls() {
		return cls;
	}
	public void setCls(Class<?> cls) {
		this.cls = cls;
	}
	public Map<String, Object> getPropertys() {
		return propertys;
	}
	public void setPropertys(Map<String, Object> propertys) {
		this.propertys = propertys;
	}
	public Map<String, String> getPropertyRefs() {
		return propertyRefs;
	}
	public void setPropertyRefs(Map<String, String> propertyRefs) {
		this.propertyRefs = propertyRefs;
	}
	public boolean isSingle() {
		return single;
	}
	public void setSingle(boolean single) {
		this.single = single;
	}
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	
}
