package test.dao;

import org.coffee.ioc.core.annotation.Autowired;
import org.coffee.ioc.core.annotation.Component;

import test.ser.IHello;
@Component()
public class Dao implements IDao{
	@Autowired
	IHello h;
	String str = "江南花落";
	public String getString() {
		return str;
	}
	

}
