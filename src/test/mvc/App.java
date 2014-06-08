package test.mvc;

import org.coffee.ioc.active.ActiveBeanFactory;
import org.coffee.ioc.active.ActiveConfig;
import org.coffee.ioc.core.bean.BeanFactory;
import org.coffee.ioc.core.bean.Config;

import test.dao.Dao;
import test.processor.A;
import test.ser.Hello;
import test.ser.IHello;


public class App {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Config config  = new ActiveConfig();
		//config.addBean("hello",Hello.class).setPropertyByType("dao");
		//config.addBean("dao",Dao.class).setProperty("str","江南花落");
		
		config.addProcessor(new A());
		config.addAutoScanPath("test.dao");
		config.addAutoScanPath("test.ser");
		BeanFactory bf = ActiveBeanFactory.build(config);
		IHello h = bf.getBean(IHello.class);
		h.say();
		
	}

}
