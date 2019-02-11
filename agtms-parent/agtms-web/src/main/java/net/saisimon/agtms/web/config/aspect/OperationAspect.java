package net.saisimon.agtms.web.config.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OperationAspect {
	
	@Pointcut("execution(public * net.saisimon.agtms.web.controller.*.Management*Controller.*(..))")
	public void managementPointcut() {}
	
	@Before("managementPointcut()")
	public void before(JoinPoint joinPoint) {
		// TODO
		
	}
	
}
