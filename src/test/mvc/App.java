package test.mvc;

import java.lang.reflect.Field;

import org.coffee.core.Bean;
import org.coffee.core.BeanFactory;
import org.coffee.core.Config;

import test.dao.Dao;
import test.ser.Hello;
import test.ser.IHello;

public class App {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Config config = new Config();
		Bean bean = null;
		bean = config.addBean(new Bean(config,Dao.class));
		bean.setProperty("str","Hello Dao");
		bean.setBeanName("dao");
		bean = config.addBean(new Bean(config,Hello.class));
		bean.setPropertyRef("dao", "test.dao.Dao");
		BeanFactory bf = BeanFactory.build(config);
		IHello h = (IHello)bf.getBean("test.ser.Hello");
		h.say();
	}

}
