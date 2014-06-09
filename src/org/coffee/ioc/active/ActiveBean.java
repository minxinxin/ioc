package org.coffee.ioc.active;

import java.util.HashMap;
import java.util.Map;

import org.coffee.ioc.core.bean.Bean;
import org.coffee.ioc.core.bean.Property;
import org.coffee.util.StringUtil;

public class ActiveBean implements Bean{
	
	Map<String,Property> properties = new HashMap<String,Property>(4);
	
	boolean singleton;
	
	boolean lazy;
	
	Class<?> class0;
	
	String name;
	
	public  ActiveBean(String name,Class<?> class0){
		if(name == null || class0 == null)
			throw new RuntimeException("name 或者 class0  不能为空");
		
		this.class0 = class0;
		this.name = name;
	}
	
	public Bean setProperty(String name, Object value) {
		if(StringUtil.isInvaildString(name, null))
			throw new RuntimeException("属性名为null或者为空字符串!");
		if(properties.get(name)!=null)
			throw new RuntimeException(this.name + "已经存在同属性名: "+name);
		properties.put(name, new ActiveProperty(Property.Type.byManual,value));
		return this;
	}
	public Bean setPropertyByName(String pName, String rName) {
		if(StringUtil.isInvaildString(pName,null) || StringUtil.isInvaildString(rName, null))
			throw new RuntimeException(this.name+" 属性名为null或者为空字符串  : setPropertyByName() ");
		if(properties.get(pName)!=null)
			throw new RuntimeException(this.name+"已经存在同属性名: "+pName);
		properties.put(pName,new ActiveProperty(Property.Type.byName,rName));
		return this;
	}

	public Bean setPropertyByType(String name) {
		if(StringUtil.isInvaildString(name, null))
			throw new RuntimeException(this.name + "属性名为null或者为空字符串");
		if(properties.get(name)!=null)
			throw new RuntimeException(this.name + "已经存在同属性名 : "+name);
		properties.put(name,new ActiveProperty(Property.Type.byType,null));
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
