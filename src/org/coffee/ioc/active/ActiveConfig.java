package org.coffee.ioc.active;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.coffee.ioc.core.annotation.Autowired;
import org.coffee.ioc.core.annotation.Component;
import org.coffee.ioc.core.bean.Bean;
import org.coffee.ioc.core.bean.Config;
import org.coffee.ioc.core.processor.Processor;
import org.coffee.util.StringUtil;

public class ActiveConfig implements Config{

	Map<String,Bean> beans = new HashMap<String,Bean>(10);
	
	Set<Processor> processors = new HashSet<Processor>(5);
		
	public Bean addBean(String name, Class<?> class0) {
		return addBean(name,class0,false);
	}
	public Bean addBean(Class<?> class0) {
		return addBean(null,class0,false);
	}
	/**
	 * 添加Bean到beans中
	 * */
	private Bean addBean(String name,Class<?> class0, boolean useComponent){
		if(class0 == null)
			throw new RuntimeException("class 参数不能为空:"+name);
		boolean lazy = false;
		boolean singleton = true;
		//正确的beanName
		String beanName = null;
		if(useComponent){
			if(name != null){
				throw new RuntimeException(name+" 已经使用了Component,则不需要使用name属性");
			}
			Component component = class0.getAnnotation(Component.class);
			if(component == null)
				throw new RuntimeException("指定的Class: "+class0+"并不存在@Component");
			beanName = component.value();
			//如果没有配置beanName ,则使用默认类名作为Bean名字,注默认@Component的value = ""
			if(beanName.equals(""))
				beanName = class0.getName();
			lazy = component.lazy();
			singleton = component.singleton();
		}else{
			if(class0.getAnnotation(Component.class) != null){
				throw new RuntimeException(class0+"指定的Bean中存在@Component");
			}
			beanName = name;
			//如果没有配置beanName ,则使用默认类名作为Bean名字
			if(beanName == null )
				beanName = class0.getName();
		}
		if(StringUtil.isInvaildString(beanName, "[a-zA-Z].*")){
			throw new RuntimeException(name+" 不符合规则");
		}
		//检测是否已经存在同名字的bean
		if(beans.get(beanName) != null)
			throw new RuntimeException("Config 中已经存在同名Bean:"+name);
		Bean bean = new ActiveBean(beanName,class0);
		bean.setLazy(lazy);
		bean.setSingleton(singleton);
		//对属性名进行@Autowired进行配置
		//配置Autowired注解
		autowired(bean);
		//添加到beans中
		beans.put(beanName, bean);
		return bean;
	}
	//根据类中的@Autowired注解添加属性注入标记都Bean中
	private void autowired(Bean bean){
		for(Class<?> cls = bean.getClass0() ; cls != Object.class;
				cls = cls.getSuperclass()){
			for(Field field : cls.getDeclaredFields()){
				Autowired autowired  = field.getAnnotation(Autowired.class);
				//忽略没有标记的属性
				if(autowired == null)
					continue;
				if(StringUtil.isInvaildString(autowired.value(), null)){
					//byType
					bean.setPropertyByType(field.getName());
				}else{
					//byName
					bean.setPropertyByName(field.getName(), autowired.value());
				}
			}
		}
	}
	public Config addAutoScanPath(String packpath) {
		if(StringUtil.isInvaildString(packpath, "([a-zA-Z_]\\w*\\.)*[a-zA-Z_]\\w*"))
			throw new RuntimeException("扫描路径不可用");
		//对路径中的Class对象进行填写到Bean中
		for(Class<?> class0  : getClasses(packpath)){
			
			//忽略没有@Component的类
			if(class0.getAnnotation(Component.class) != null){
				int mod = class0.getModifiers();
				if(Modifier.isInterface(mod) || Modifier.isAbstract(mod))
					throw new RuntimeException("@Component所在的类不能为接口或者抽象类");
				addBean(null,class0,true);
			}
		}
		return this;
	}
	public Config addProcessor(Processor processor) {
		if(processor==null)
			throw new RuntimeException("processor 不能为null");
		processors.add(processor);
		return this;
	}

	
	public Map<String, Bean> getBeans() {
		return beans;
	}

	public Set<Processor> getProcessors() {
		return processors;
	}

	//-----------------------------------------------------------
	/***
	 * 
	 *<pre>
	 * 扫描包的下的类来自于http://guoliangqi.iteye.com/blog/644876, 
	 * 感谢@author:yznxing
	 * 注意, 这个类库去除了扫描jar包.
	 * 
	 * 从包package中获取所有的Class(不能从jar等介质中获取)
	 * @param pack 路径名(org.coffee)
	 * @return ClassSet
	 * </pre>
	 */
	static Set <Class<?>> getClasses(String pack){
	
	    // 第一个class类的集合
	    Set <Class<?>> classes = new LinkedHashSet <Class<?>> ();
	    // 获取包的名字 并进行替换
	    String packageName = pack;
	    String packageDirName = packageName.replace('.', '/');
	    // 定义一个枚举的集合 并进行循环来处理这个目录下的things
	    Enumeration<URL> dirs;
	    try{
	        dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
	        
	        while (dirs.hasMoreElements()){
	           
	            URL url = dirs.nextElement();
	            String protocol = url.getProtocol();
	            // 如果是以文件的形式保存在服务器上
	            if ("file".equals(protocol)){
	                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
	                findAndAddClassesInPackageByFile(packageName, filePath, classes);
	            }
	        }
	    }
	    catch (IOException e){
	    	throw new RuntimeException(e);
	    }
	    return classes;
	}
	/**
	 * 以文件的形式来获取包下的所有Class
	 *
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	public static void findAndAddClassesInPackageByFile(String packageName,
	        String packagePath, Set <Class<?>> classes){
		
	    File dir = new File(packagePath);
	    // 如果不存在或者 也不是目录就直接返回
	    if (!dir.exists() || !dir.isDirectory())
	        return;
	    
	    // 如果存在 就获取包下的所有文件 包括目录
	    File[] dirfiles = dir.listFiles(new FileFilter(){
	        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
	        public boolean accept(File file){
	            return (file.isDirectory())|| (file.getName().endsWith(".class"));
	        }
	    });
	    // 循环所有文件
	    for (File file : dirfiles){
	        // 如果是目录 则继续扫描
	        if (file.isDirectory()){
	            findAndAddClassesInPackageByFile(packageName + "."+ file.getName(), 
	            		file.getAbsolutePath(),classes);
	        }else{
	            // 如果是java类文件 去掉后面的.class 只留下类名
	            String className = file.getName().substring(0, file.getName().length() - 6);
	            try{
	            	//使用SystemClassLoader加载方式加载Class
	                classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
	            }
	            catch (ClassNotFoundException e){
	                throw new RuntimeException(e);
	            }
	        }
	    }
	}
}