package net.saisimon.agtms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.repository.BaseRepository;

/**
 * 模板信息注解
 * 
 * @author saisimon
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateInfo {
	
	/**
	 * 模板唯一标识
	 */
	String key();
	
	/**
	 * 模板标题名称
	 */
	String title() default "";
	
	/**
	 * 模板支持的功能数组
	 */
	Functions[] functions() default {};
	
	/**
	 * 查询对象的 repository 类型，需要实现 BaseRepository 接口
	 * 
	 * @see net.saisimon.agtms.core.repository.BaseRepository
	 */
	Class<? extends BaseRepository<?, ?>> repository();
	
}
