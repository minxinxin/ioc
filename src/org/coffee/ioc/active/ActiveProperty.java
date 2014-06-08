package org.coffee.ioc.active;

import org.coffee.ioc.core.bean.Property;

public class ActiveProperty implements Property{
	
	
	Type type;
	/**
	 * <pre>
	 * byManual : 给定数值
	 * byType : null
	 * byName : 给定字符串
	 * </pre>
	 * */
	Object value;
	
	public ActiveProperty(Type type, Object value){
		this.type = type;
		this.value = value;
	}
	/**
	 * @see org.coffee.ioc.core.bean.Property#getType()
	 * */
	public Type getType() {
		return type;
	}
	/**
	 * @see org.coffee.ioc.core.bean.Property#getValue()
	 * */
	public Object getValue() {
		return value;
	}
	
}
