package org.coffee.ioc.active;

import java.util.HashMap;
import java.util.Map;

import org.coffee.ioc.core.bean.Bean;
import org.coffee.ioc.core.bean.Property;

public class ActiveBean implements Bean{
	
	Map<String,Property> properties = new HashMap<String,Property>(4);
	
	boolean singleton = true;
	
	boolean lazy = true;
	
	Class<?> class0;
	
	String name;
	
	public  ActiveBean(String name,Class<?> class0){
		if(class0 == null)
			throw new RuntimeException("class 参数不能为空");
		this.class0 = class0;
		if(name == null){
			//使用 class0的类名
			this.name = class0.getName();
		}else{
			if(name.trim().equals(""))
				throw new RuntimeException("名字不能为空字符串");
			this.name = name;
		}
	}
	
	public Bean setProperty(String name, Object value) {
		if(name == null || name.trim().equals(""))
			throw new RuntimeException("属性名为null或者为空字符串!");
		if(properties.get(name)!=null)
			throw new RuntimeException("已经存在同属性名!");
		properties.put(name, new ActiveProperty(ActiveProperty.Type.byManual,value));
		return this;
	}
	public Bean setPropertyByName(String pName, String rName) {
		if(pName == null || pName.trim().equals("") 
				|| rName == null || rName.trim().equals(""))
			throw new RuntimeException("属性名为null或者为空字符串!");
		if(properties.get(pName)!=null)
			throw new RuntimeException("已经存在同属性名!");
		properties.put(pName,new ActiveProperty(ActiveProperty.Type.byName,rName));
		return this;
	}

	public Bean setPropertyByType(String name) {
		if(name == null || name.trim().equals(""))
			throw new RuntimeException("属性名为null或者为空字符串!");
		if(properties.get(name)!=null)
			throw new RuntimeException("已经存在同属性名!");
		properties.put(name,new ActiveProperty(ActiveProperty.Type.byType,null));
		return this;
	}
	
	public Bean setSingleton(boolean isSingleton) {
		this.singleton = isSingleton;
		return this;
	}

	public Bean setLazy(boolean lazy) {
		this.lazy = lazy;
		return this;
	}

	
	//------------------------------------------------------------
	public Map<String, Property> getProperties() {
		return properties;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public boolean isLazy() {
		return lazy;
	}

	public Class<?> getClass0() {
		return class0;
	}

	public String getName() {
		return name;
	}
	
	
}
