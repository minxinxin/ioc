package test.mvc;

import org.coffee.ioc.active.ActiveBeanFactory;
import org.coffee.ioc.active.ActiveConfig;
import org.coffee.ioc.core.bean.BeanFactory;
import org.coffee.ioc.core.bean.Config;

import test.dao.Dao;
import test.ser.Hello;
import test.ser.IHello;


public class App {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Config config  = new ActiveConfig();
		config.addBean(Hello.class).setPropertyByType("dao");
		config.addBean(Dao.class).setProperty("str","江南花落");
		BeanFactory bf = ActiveBeanFactory.build(config);
		IHello h = bf.getBean(IHello.class);
		h.say();
		
	}

}
