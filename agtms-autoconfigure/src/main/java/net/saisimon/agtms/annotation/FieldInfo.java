package net.saisimon.agtms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.selection.EmptySelection;
import net.saisimon.agtms.core.selection.Selection;

/**
 * 模板属性注解
 * 
 * @author saisimon
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldInfo {
	
	/**
	 * 列的标题名称，当标题为空时，默认取排序第一的属性标题名称。一列可以包含多个属性
	 */
	String columnTitle() default "";
	
	/**
	 * 列的排序序号
	 */
	int columnOrdered();
	
	/**
	 * 属性的标题名称
	 */
	String fieldTitle();
	
	/**
	 * 属性的类型，默认为字符串类型
	 * 
	 * @see net.saisimon.agtms.core.enums.Classes
	 */
	Classes fieldType() default Classes.STRING;
	
	/**
	 * 属性的展现类型，默认为文本展示
	 * 
	 * @see net.saisimon.agtms.core.enums.Views
	 */
	Views view() default Views.TEXT;
	
	/**
	 * 属性的展现类型为下拉列表时，对应的下拉列表类型
	 * 
	 * @see net.saisimon.agtms.core.enums.Views#SELECTION
	 * @see net.saisimon.agtms.core.selection.Selection
	 */
	Class<? extends Selection<?>> selection() default EmptySelection.class;
	
	/**
	 * 属性是否支持过滤，默认不支持
	 */
	boolean filter() default false;
	
	/**
	 * 属性是否支持排序，默认不支持
	 */
	boolean sorted() default false;
	
	/**
	 * 属性是否必填，默认不必填
	 */
	boolean required() default false;
	
	/**
	 * 属性是否唯一，默认不唯一
	 */
	boolean uniqued() default false;
	
	/**
	 * 属性在列表页是否隐藏，默认不隐藏
	 */
	boolean hidden() default false;
	
	/**
	 * 属性默认值，默认为空
	 */
	String defaultValue() default "";
	
//	int width() default 100;
	
	/**
	 * 属性的排序序号
	 */
	int fieldOrdered();
	
}
