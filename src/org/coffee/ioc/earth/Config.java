package org.coffee.ioc.earth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Config {

	Map<String,Bean> beanMap = new HashMap<String,Bean>();
	Set<String> scanPackageSet = new HashSet<String>();
	
	/**
	 * 添加一个Bean到配置中
	 * */
	public Bean addBean(Bean bean){
		beanMap.put(bean.getBeanName(), bean);
		return bean;
	}
	/**
	 * 添加需要自动扫描的路径
	 * */
	public void addScanPackage(String pack){
		scanPackageSet.add(pack);
	}
	public Map<String, Bean> getBeanMap() {
		return beanMap;
	}
	public Set<String> getScanPackageSet() {
		return scanPackageSet;
	}

	
	
	
}
