package org.coffee.ioc.core.bean;

import java.util.Map;
import java.util.Set;

import org.coffee.ioc.core.processor.Processor;


public interface Config {

	/**
	 * 构建一个bean 到 Config中, 如果已经存在同名bean, 则抛出运行时异常
	 * @param name bean名字
	 * @param cls bean指向的class对象
	 * @return 通过该对象继续配置bean的相关属性
	 * */
	Bean addBean(String name,Class<?> class0);
	/**
	 * 构建一个bean 到 Config中并且以class0的类名为BeanName 
	 * 如果已经存在同名bean, 则抛出运行时异常
	 * */
	Bean addBean(Class<?> class0);
	
	/**
	 * 自动扫描的包路径路径格式为 org.coffee, 之后会自动扫描所有该包和子包下的类
	 * @param packpath 扫描路径, 非空(所以不能直接从root路径扫描)
	 * @throws packpath 为空, 运行时错误
	 * */
	Config addAutoScanPath(String packpath);
	/**
	 * 添加Bean处理器
	 * @param processor 非空参数
	 * @throws processor 为空或者
	 * */
	Config addProcessor(Processor processor);
	/**
	 * <pre>
	 * 获得Config中配置完的Bean
	 * String->beanName
	 * Bean->具体的Bean
	 * </pre>
	 * */
	public Map<String, Bean> getBeans();
	/**
	 * 获得Config中配置的处理器
	 * */
	public Set<Processor> getProcessors();
}
