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
import org.coffee.util.StringUtil;


public class ActiveBeanFactory implements BeanFactory{
	
	//单例容器, BeanName:Object
	Map<String,Object> singletos ;
	//如果Bean继承FactoryBean 且 返回的getObject() 为单例的容器
	Map<String,Object> factoryBeanSingletons;
	//类型缓存
	Map<Class<?>,String[]> typeCache;
	//Bean原信息, BeanName:Bean
	Map<String,Bean> beans ;
	//处理器
	Set<Processor> processors ;
	//prototype检测容器, 用于循环生成Prototype类型的bean检错, 里面填写BeanName
	Set<String> circular;
	//容器是否可以使用
	boolean usable;
	
	private ActiveBeanFactory(Config config){
		if(config == null)
			throw new RuntimeException("config 不能为null");
		this.singletos = new HashMap<String,Object>(20);
		this.factoryBeanSingletons = new HashMap<String,Object>(20);
		this.beans = config.getBeans();
		this.processors = config.getProcessors();
		this.typeCache = new HashMap<Class<?>,String[]>(10);
		this.circular = null;
		this.usable = true;
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
			for(String beanName : abf.beans.keySet()){
				Bean bean = abf.beans.get(beanName);
				//如果bean指定延迟初始化, 或者不是单例则不进行初始化该Bean
				if(bean.isLazy() || !bean.isSingleton())
					continue;
				//调用getBean进行初始化指定Bean
				abf.getBean(beanName);
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
	synchronized public Object getBean(String name) {
		
		if(this.usable == false)
			throw new RuntimeException("容器处于不可用状态");
		if(StringUtil.isInvaildString(name, null))
			throw new RuntimeException("name不能为null或者为空");
		
		//是否拥有 FACTORY_BEAN_PREFIX 
		boolean haveFactoryBeanPrefix = false;
		String beanName = name;
		//去除&符号
		while (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
			haveFactoryBeanPrefix = true;
			beanName = beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
		}
		//获得具体的Bean生成对象
		//如果beanName指向的bean是单例且已经初始化, 则一定存在于singletons中
		Object o = this.singletos.get(beanName);
		if( o == null){
			Bean bean = this.beans.get(beanName);
			//如果没有beanName对应的Bean, 则返回null
			if(bean == null)
				throw new RuntimeException(beanName+"没有定义于容器中");
			o = createBean(bean);
		}
		if(haveFactoryBeanPrefix)
			return getFactoryBeanObject(beanName,o);
		else
			return getObject(beanName,o);

	}
	/**
	 * name 中带有&, 则返回FactoryBean
	 * */
	private Object getFactoryBeanObject(String beanName,Object o){
		if(o instanceof FactoryBean)
			return o;
		else
			//对象不为BeanFactory类型,则报错
			throw new RuntimeException("指定的"+beanName+"没有继承FactoryBean接口");
	}
	/**
	 * 获取Bean.class0指向的Class生成的对象,如果该对象继承FactoryBean接口, 则调用getObject
	 * */
	private Object getObject(String beanName,Object o){
		
		//如果该Object继承了FactoryBean接口, 则返回对象需要按照逻辑修改
		if(o instanceof FactoryBean){
			FactoryBean fb = (FactoryBean)o;

			if(fb.isSingleton()){
				//如果是单例类型, 则先去factorybean单例缓存中查询, 如果没有则新构造一个,然后
				//放置到缓存中
				o = this.factoryBeanSingletons.get(beanName);
				if(o == null){
					o = fb.getObject();
					this.factoryBeanSingletons.put(beanName, o);
				}
			}else{
				o = fb.getObject();
			}
		}
		return o;
	}

	private Object createBean(Bean bean) {
		if(bean == null)
			throw new RuntimeException("createBean: bean 为null");
		//Prototype 循环开启标记
		boolean beginePrototype = false;
		//如果是Prototype类型的Bean, 需要特殊处理
		if(!bean.isSingleton()){
			if(this.circular == null){
				//Prototype循环依赖检测从这里开始
				beginePrototype = true;
				this.circular = new HashSet<String>(5);
			}
			//检测是否循环生成Prototype对象
			if(this.circular.contains(bean.getName())){
				throw new RuntimeException("该Bean的生成为循环生成:"+bean.getName());
			}
			//添加一个prototype 类型的 bean到检测容器中
			this.circular.add(bean.getName());
		}
		/*生命周期处理*/
		Object inst = mockBean(bean);
		
		//prototype结束生成, 则清空依赖
		if(beginePrototype)
			this.circular = null;
		return inst;
	}
	/**
	 * 根据Bean,来生成一个对象,主要是生命周期
	 * */
	private Object mockBean(Bean bean) {
		Object inst = null;
		try {
			//支持私有类的无参数构造函数
			Constructor<?> c = bean.getClass0().getDeclaredConstructor();
			c.setAccessible(true);
			inst = c.newInstance();
			//有且只有单例类型的Bean可以使用singletons
			if(bean.isSingleton())
				this.singletos.put(bean.getName(), inst);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//配置属性
		for(String pKey : bean.getProperties().keySet()){
			Property p = bean.getProperties().get(pKey);
			Field field = null;
			for(Class<?> cls = bean.getClass0(); cls != Object.class ; 
					cls = cls.getSuperclass()){
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
		//BeanLifeCycle接口
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
		return inst;
	}
	@SuppressWarnings("unchecked")
	synchronized public <T> T getBean(Class<T> class0) {
		if(this.usable == false)
			throw new RuntimeException("容器处于不可用状态");
		if(class0 == null)
			throw new RuntimeException("class0 不能为空");
		String[] names = getBeanNameByType(class0);
		//没有查询到对应的Bean Type
		if(names.length == 0)
			throw new RuntimeException("容器中没有匹配的Bean: "+class0);
		if(names.length != 1){
			throw new RuntimeException("容器中有多个同类型或者子类型的Bean对象 "+class0);
		}
		String name = names[0];
		return (T) getBean(name);
	}
	/**
	 * <pre>
	 * 查询指定类型的类或者子类的bean名字, 且如果Bean继承于FactoryBean, 则名字前添加&
	 * @param class0 查询指定的class0类型的子类
	 * @return 如果array[] size = 0 ,则表示没有查询到
	 * </pre>
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
			if(class0.isAssignableFrom(cls)){
				//判断是否为FactoryBean类型
				if(FactoryBean.class.isAssignableFrom(bean.getClass0()))
					list.add(FACTORY_BEAN_PREFIX+key);
				else
					list.add(key);
			}
		}		
		this.typeCache.put(class0, list.toArray(new String[list.size()]));
		return this.typeCache.get(class0);
	}
	synchronized public boolean containsBean(String name) {
		if(this.usable == false)
			throw new RuntimeException("容器处于不可用状态");
		return this.beans.get(name) == null? false : true;
	}

	synchronized public boolean isSingleton(String name) {
		if(this.usable == false)
			throw new RuntimeException("容器处于不可用状态");
		Bean bean = this.beans.get(name);
		if(bean == null)
			throw new RuntimeException("没有指定名字的bean");
		return bean.isSingleton();
	}
	synchronized public void closeBeanFactory() {
		if(this.usable == false)
			throw new RuntimeException("容器处于不可用状态");
		//生命周期
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
		this.circular = null;
		this.processors = null;
		this.singletos = null;
		this.typeCache = null;
		//标记不可用
		this.usable = false;
		
	}
	
	
}
