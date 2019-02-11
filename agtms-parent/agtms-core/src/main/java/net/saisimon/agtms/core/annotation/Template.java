package net.saisimon.agtms.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.saisimon.agtms.core.enums.Functions;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Template {
	
	String id();
	
	String title();
	
	long navigation() default -1;
	
	Functions[] functions() default {};
	
	String[] cssPath() default {};
	
	String[] jsPath() default {};
	
	String source() default "jpa";
	
	String sourceUrl() default "";
	
}
