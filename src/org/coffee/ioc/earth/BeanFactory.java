package org.coffee.ioc.earth;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanFactory {

	Map<String,BeanInst> beanMetaMap = new HashMap<String,BeanInst>(10);
	BeanFactory(){
	}
	/**
	 * 根据给定Bean名称, 放回具体Bean
	 * 
	 * */
	public Object getBean(String name){
		BeanInst inst = this.beanMetaMap.get(name);
		if(inst!=null)
			return inst.getInstance();
		else
			return null;
	}
	/**
	 * 根据Config配置出BenaFactory
	 * */
	static public BeanFactory build(Config config){
		BeanFactory bf = new BeanFactory();
		//实例化bean
		Map<String,Bean> beanMap = config.getBeanMap();
		for(String beanName : beanMap.keySet()){
			
			Bean bean  = beanMap.get(beanName);
			try {
				Object obj = bean.getCls().newInstance();
				
				
				
				BeanInst inst = new BeanInst();
				inst.setInstance(obj);
				inst.setBeanName(beanName);
				inst.setSingle(bean.isSingle());
				
				bf.beanMetaMap.put(beanName, inst);
				
			} catch (Exception e) {
				throw new RuntimeException("实例化错误");
			}
		}
		//配置属性
		for(String beanName : beanMap.keySet()){
			
			Bean bean  = beanMap.get(beanName);
			BeanInst inst = bf.beanMetaMap.get(beanName);
			//普通属性名字
			for(String pName : bean.getPropertys().keySet()){
				Field field = null;
				for(Class<?> cls = bean.getCls();cls!= Object.class
						;cls = cls.getSuperclass()){
					try {
						field = cls.getDeclaredField(pName);
						if(field != null)
							break;
					} catch (NoSuchFieldException e) {
						continue;
					} catch (SecurityException e) {
						e.printStackTrace();
						throw new RuntimeException("安全问题");
					}
				}
				if(field == null)
					throw new RuntimeException(bean.getCls().getName()+"没有该属性:"+pName);
				
				try {
					//可以访问私有变量
					field.setAccessible(true);
					field.set(inst.getInstance(), bean.getPropertys().get(pName));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			//配置引用属性
			for(String pName : bean.getPropertyRefs().keySet()){
				String rName = bean.getPropertyRefs().get(pName);
				BeanInst rInst  = bf.beanMetaMap.get(rName);
				if(rInst == null)
					throw new RuntimeException("没有对应的Bean配置"+pName);
				
				Field field = null;
				for(Class<?> cls = bean.getCls();cls != Object.class
						;cls = cls.getSuperclass()){
					try {
						field = cls.getDeclaredField(pName);
						if(field != null)
							break;
					} catch (NoSuchFieldException e) {
						continue;
					} catch (SecurityException e) {
						e.printStackTrace();
						throw new RuntimeException("安全问题");
					}
				}
				if(field == null)
					throw new RuntimeException(bean.getCls().getName()+"没有该属性:"+pName);
				
				try {
					//可以访问私有变量
					field.setAccessible(true);
					field.set(inst.getInstance(), rInst.getInstance());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return bf;
	}
	
}
class BeanInst{
	
	Object instance;
	boolean single = true;
	String beanName;
	//是否已经初始化完属性
	boolean initProperty = false;
	
	public boolean isInitProperty() {
		return initProperty;
	}
	public void setInitProperty(boolean initProperty) {
		this.initProperty = initProperty;
	}
	public Object getInstance() {
		return instance;
	}
	public void setInstance(Object instance) {
		this.instance = instance;
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