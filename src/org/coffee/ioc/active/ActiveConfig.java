package org.coffee.ioc.active;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.coffee.ioc.core.annotation.Autowired;
import org.coffee.ioc.core.annotation.Component;
import org.coffee.ioc.core.bean.Bean;
import org.coffee.ioc.core.bean.Config;
import org.coffee.ioc.core.processor.Processor;

public class ActiveConfig implements Config{

	Map<String,Bean> beans = new HashMap<String,Bean>(10);
	
	Set<Processor> processors = new HashSet<Processor>(5);
	
	Pattern pattern = Pattern.compile("([a-zA-Z_]\\w*\\.)*[a-zA-Z_]\\w*");
	
	public Bean addBean(String name, Class<?> class0) {
		if(class0 == null)
			throw new RuntimeException("class 参数不能为空");

		if(name == null)
			name = class0.getName();
		
		if(name.trim().equals(""))
			throw new RuntimeException("name 不能为空字符串");
		if(beans.get(name) != null)
			throw new RuntimeException("Config 中已经存在同名Bean");
		
		ActiveBean ab = new ActiveBean(name,class0);
		beans.put(name, ab);
		return ab;
	}

	public Bean addBean(Class<?> class0) {
		return addBean(null,class0);
	}

	public Config addAutoScanPath(String packpath) {
		if(packpath == null || packpath.trim().equals(""))
			throw new RuntimeException("路径不能为空");
		Matcher  m = pattern.matcher(packpath);
		if(!m.matches())
			throw new RuntimeException("路径不符合规范, 应该为org.coffee格式");
		//对路径中的Class对象进行填写到Bean中
		for(Class<?> class0  : getClasses(packpath)){
			//忽略接口类型Class
			if(!class0.isInterface()){
				Component compoent = class0.getAnnotation(Component.class);
				if(compoent != null){
					Bean bean = null;
					if(compoent.value().equals("")){
						//默认使用class类名
						bean = addBean(class0);
					}else{
						bean = addBean(compoent.value(),class0);
					}
					//配置属性
					bean.setLazy(compoent.lazy());
					bean.setSingleton(compoent.singleton());
					//配置Autowired注解
					for(Class<?> cls = class0 ; cls != Object.class;
							cls = cls.getSuperclass()){

						for(Field field : cls.getDeclaredFields()){
							Autowired autowired  = field.getAnnotation(Autowired.class);
							if(autowired.value().equals("")){
								//byType
								bean.setPropertyByType(field.getName());
							}else{
								//byName
								bean.setPropertyByName(field.getName(), autowired.value());
							}
						}
					}
				}
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

	/**
	 * 从包package中获取所有的Class(不能从jar等介质中获取)
	 * @param pack 路径名(org.coffee)
	 * @return
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