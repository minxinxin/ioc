package test.processor;

import org.coffee.ioc.core.processor.Processor;

import test.ser.IHello;

public class A implements Processor{

	public void beforeInit(Object instance) {
		if(instance instanceof IHello){
			System.out.println("Before");
			IHello i = (IHello)instance;
			i.say();
		}
		
	}

	public void afterInit(Object instance) {
		if(instance instanceof IHello){
			System.out.println("After");
			IHello i = (IHello)instance;
			i.say();
		}
		
	}

	public void destroy(Object instance) {
		// TODO Auto-generated method stub
		
	}


}
