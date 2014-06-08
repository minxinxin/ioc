package org.coffee.ioc.active;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coffee.ioc.core.bean.Bean;
import org.coffee.ioc.core.bean.BeanFactory;
import org.coffee.ioc.core.bean.Config;
import org.coffee.ioc.core.bean.Property;
import org.coffee.ioc.core.factory.FactoryBean;
import org.coffee.ioc.core.lifecycle.BeanLifeCycle;
import org.coffee.ioc.core.processor.Processor;


public class ActiveBeanFactory implements BeanFactory{
	//单例容器
	Map<String,Object> singletos ;
	//类型缓存
	Map<Class<?>,String[]> typeCache;
	//Bean原信息
	Map<String,Bean> beans ;
	//处理器
	Set<Processor> processors ;
	//prototype检测容器, 用于循环生成Prototype类型的bean检错
	ThreadLocal<Set<String>> checkPrototypes;
	
	private ActiveBeanFactory(Config config){
		if(config == null)
			throw new RuntimeException("config 不能为null");
		this.singletos = new HashMap<String,Object>(20);
		this.beans = config.getBeans();
		this.processors = config.getProcessors();
		this.typeCache = new HashMap<Class<?>,String[]>(10);
		this.checkPrototypes = new ThreadLocal<Set<String>>();
		if(this.beans == null || this.processors == null)
			throw new RuntimeException("beans 或者 processors 不能为null, 初始化BeanFactory失败");
		
	}
	/**
	 * @param config 配置Factory的Config对象
	 * @param lazy 是否延迟初始化BeanFactory
	 * */
	public static ActiveBeanFactory build(Config config,boolean lazy){
		ActiveBeanFactory  abf = new  ActiveBeanFactory(config);
		if(!lazy){
			//初始化BeanFactory
			for(String key : abf.beans.keySet()){
				Bean bean = abf.beans.get(key);
				//如果bean指定延迟初始化, 或者不是单例则不进行初始化该Bean
				if(bean.isLazy() || !bean.isSingleton())
					continue;
				//调用getBean进行初始化指定Bean
				abf.getBean(key);
			}
			
		}
		return abf;
	}
	/**
	 * @param config 配置Factory的Config对象
	 * */
	public static ActiveBeanFactory build(Config config){
		//立马初始化BeanFactory中的对象
		return build(config,false);
	}
	public Object getBean(String name) {
		if(name == null || name.trim().equals(""))
			throw new RuntimeException("name不能为null或者为空");
		String beanName = name;
		//是否是获取FactoryBean类型的name
		boolean isGetFactoryBean = false;
		//去除&符号
		while (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
			isGetFactoryBean = true;
			beanName = beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
		}
		Bean bean = this.beans.get(beanName);
		if(bean == null)
			return null;
		//检查单例缓存是否存在该对象
		Object o = this.singletos.get(beanName);
		//如果没有则直接构建一个新的Object
		if(o == null)
			o = createBean(bean);
		if(isGetFactoryBean){
			if(o instanceof FactoryBean){
				return o;
			}else{
				throw new RuntimeException(o.toString()+"不是FactoryBean类型的Bean");
			}
		}
		if(o instanceof FactoryBean){
			FactoryBean fb = (FactoryBean)o;
			if(bean.isSingleton() && fb.isSingleton()){
				o = this.singletos.get(beanName+"#FactoryBean");
				if(o  == null){
					o = fb.getObject();
					this.singletos.put(beanName+"#FactoryBean", o);
				}
			}else{
				o = fb.getObject();
			}
			
		}
		return o;
	}

	private Object createBean(Bean bean) {
		if(bean == null)
			throw new RuntimeException("createBean: bean 不能为null");
		//Prototype 检测开启标记
		boolean beginePrototype = false;
		if(!bean.isSingleton()){
			if(this.checkPrototypes.get() == null){
				beginePrototype = true;
				this.checkPrototypes.set(new HashSet<String>(5));
			}
			//检测是否循环生成Prototype对象
			if(this.checkPrototypes.get().contains(bean.getName())){
				throw new RuntimeException("该Bean的生成为循环生成:"+bean.getName());
			}
			//添加一个prototype 类型的 bean到检测容器中
			this.checkPrototypes.get().add(bean.getName());
		}
		//生成新的Bean
		Object inst  = null;
		
		try {

			Constructor<?> c = bean.getClass0().getDeclaredConstructor();
			c.setAccessible(true);
			inst = c.newInstance();
			//如果是单例类型的Bean
			if(bean.isSingleton())
				this.singletos.put(bean.getName(), inst);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//配置属性
		for(String pKey : bean.getProperties().keySet()){
			Property p = bean.getProperties().get(pKey);
			Field field = null;
			for(Class<?> cls = bean.getClass0(); cls != Object.class ; cls = cls.getSuperclass()){
				try {
					field = cls.getDeclaredField(pKey);
				} catch (NoSuchFieldException e) {
					//继续查询父类
					continue;
				} catch (SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			if(field == null)
				throw new RuntimeException(bean.getClass0().getName() +"没有属性: "+ pKey);

			Object value = null;
			//根据Property类型配置属性
			switch(p.getType()){
				case byManual:{
					value = p.getValue();
					break;
				}
				case byType:{
					value = getBean(field.getType());
					if(value == null)
						throw new RuntimeException("不存在这种类型的bean :" + field.getType());
					break;
				}
				case byName:{
					value = getBean((String)p.getValue());
					if(value == null)
						throw new RuntimeException("不存在bean :" + p.getValue());
					break;
				}
			}
			try {
				field.setAccessible(true);
				field.set(inst, value);
			}catch(Throwable e){
				throw new RuntimeException(e);
			}
		}
		//生命周期处理
		if(inst instanceof BeanLifeCycle){
			BeanLifeCycle blc = (BeanLifeCycle)inst;
			blc.setBeanName(bean.getName());
			blc.setBeanFactory(this);
		}
		for(Processor p : this.processors){
			p.beforeInit(inst);
		}
		if(inst instanceof BeanLifeCycle){
			BeanLifeCycle blc = (BeanLifeCycle)inst;
			blc.init();
		}
		for(Processor p : this.processors){
			p.afterInit(inst);
		}
		//结束生成
		if(beginePrototype)
			this.checkPrototypes.remove();
		return inst;
	}
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> class0) {
		if(class0 == null)
			throw new RuntimeException("class0 不能为空");
		String[] names = getBeanNameByType(class0);
		if(names == null)
			return null;
		if(names.length != 1){
			throw new RuntimeException("容器中有多个同类型或者子类型的Bean对象"+class0);
		}
		String name = names[0];
		return (T) getBean(name);
	}
	/**
	 * 查询指定类型的类或者子类的bean名字
	 * @param class0 查询指定的class0类型的子类
	 * @return null 表示没有查询到
	 * */
	private String[] getBeanNameByType(Class<?> class0){
		String[] names = this.typeCache.get(class0);
		if(names != null)
			return names;
		List<String> list = new LinkedList<String>();
		for(String key : this.beans.keySet()){
			Bean bean = this.beans.get(key);
			Class<?> cls = bean.getClass0();
			//如果可以被赋值
			if(class0.isAssignableFrom(cls))
				list.add(key);
		}
		if(list.size() == 0)
			return null;
		
		this.typeCache.put(class0, list.toArray(new String[list.size()]));
		return this.typeCache.get(class0);
	}
	public boolean containsBean(String name) {
		return this.beans.get(name) == null? false : true;
	}

	public boolean isSingleton(String name) {
		Bean bean = this.beans.get(name);
		if(bean == null)
			throw new RuntimeException("没有指定名字的bean");
		return bean.isSingleton();
	}
	public void closeBeanFactory() {
		for(String key : this.singletos.keySet()){
			Object o = this.singletos.get(key);
			for(Processor p : this.processors){
				p.destroy(o);
			}
			if(o instanceof BeanLifeCycle){
				BeanLifeCycle blc = (BeanLifeCycle)o;
				blc.destroy();
			}
		}
		this.beans = null;
		this.checkPrototypes = null;
		this.processors = null;
		this.singletos = null;
		this.typeCache = null;
		
	}
}
