package net.saisimon.agtms.web.config.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.annotation.Admin;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;

/**
 * 管理员注解切面
 * 
 * @author saisimon
 *
 */
@Aspect
@Component
@ConditionalOnClass(JoinPoint.class)
public class AdminAspect {
	
	@Pointcut("execution(public * net.saisimon.agtms.web.controller..*.*(..))")
	public void controllerPointcut() {}
	
	@Around("controllerPointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		Signature signature = joinPoint.getSignature();
		if (signature instanceof MethodSignature) {
			MethodSignature methodSignature = (MethodSignature) signature;
			Method method = methodSignature.getMethod();
			Admin admin = method.getDeclaredAnnotation(Admin.class);
			if (admin == null) {
				admin = method.getDeclaringClass().getDeclaredAnnotation(Admin.class);
			}
			if (admin != null) {
				UserToken userToken = TokenFactory.get().getToken(AuthUtils.getUid(), false);
				if (!userToken.getAdmin()) {
					return ErrorMessage.Common.PERMISSION_DENIED;
				}
			}
		}
		return joinPoint.proceed();
	}
	
}
