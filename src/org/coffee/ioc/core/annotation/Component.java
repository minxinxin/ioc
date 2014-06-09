package org.coffee.ioc.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 使用的格式有两种:
 * @Component("beanName") : 使用指定beanname作为id
 * @Component() : 使用类的路径名作为id如: org.coffee.annotation.bean
 * 如果指定为不为单例, 则配置为
 * @Component(singleton=false)
 * 如果指定不延迟初始化, 即随容器启动马上初始化对象
 * @Component(lazy=false)
 * 如果存在同名Bean则抛出运行时异常
 * </pre>
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {
	String value() default "";
	boolean singleton() default true;
	boolean lazy() default false;
}
