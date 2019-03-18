package net.saisimon.agtms.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.saisimon.agtms.core.enums.OperateTypes;

/**
 * 操作信息注解
 * 
 * @author saisimon
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Operate {
	
	/**
	 * 操作类型
	 */
	OperateTypes type();
	
	/**
	 * 操作说明
	 */
	String value() default "";
	
}
