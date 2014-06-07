package org.coffee.ioc.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 使用的格式有两种:
 * @Autowired("beanName") : byName
 * @Autowired() : byType
 * 
 * byName, 通过指定beanName进行配置, 如果没有则抛出异常
 * byType, 通过类型匹配进行, 如果有多个匹配到的类, 则抛出异常
 * 
 * </pre>
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Autowired {
	String value() default "";
}
