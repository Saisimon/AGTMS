package net.saisimon.agtms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.repository.BaseRepository;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateInfo {
	
	String key();
	
	String title();
	
	long navigation() default -1;
	
	Functions[] functions() default {};
	
	Class<? extends BaseRepository<?, ?>> repository();
	
}
