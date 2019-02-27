package net.saisimon.agtms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Views;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldInfo {
	
	String columnTitle() default "";
	
	int columnOrdered();
	
	String fieldTitle();
	
	Classes fieldType() default Classes.STRING;
	
	Views view() default Views.TEXT;
	
	boolean filter() default false;
	
	boolean sorted() default false;
	
	boolean required() default false;
	
	boolean uniqued() default false;
	
	boolean hidden() default false;
	
	String defaultValue() default "";
	
	int width() default 100;
	
	int fieldOrdered();
	
}
