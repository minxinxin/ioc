package test.dao;

import org.coffee.ioc.core.annotation.Autowired;
import org.coffee.ioc.core.annotation.Component;

import test.ser.IHello;

@Component(singleton=false)
public class Dao implements IDao{
	@Autowired
	IHello h;
	String str = "null";
	public String getString() {
		return str;
	}
	

}
