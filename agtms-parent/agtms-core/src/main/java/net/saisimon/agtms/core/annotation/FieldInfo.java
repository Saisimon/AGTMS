package net.saisimon.agtms.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.expression.spel.ast.Selection;

import net.saisimon.agtms.core.enums.Views;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldInfo {
	
	byte columnId();
	
	String columnTitle() default "";
	
	String columnWidth() default "";
	
	byte fieldId();
	
	String fieldTitle();
	
	boolean required() default false;
	
	String defaultValue() default "";
	
	boolean uniqued() default false;
	
	boolean sorted() default false;
	
	boolean filter() default false;
	
	boolean hidden() default false;
	
	Class<? extends Selection> filterClass() default Selection.class;
	
	Views view() default Views.TEXT;
	
	int width() default 100;
	
}
