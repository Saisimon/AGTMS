package net.saisimon.agtms.web.config.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.domain.entity.Operation;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.factory.OperationHandlerFactory;
import net.saisimon.agtms.core.factory.OperationServiceFactory;
import net.saisimon.agtms.core.handler.DefaultOperationHandler;
import net.saisimon.agtms.core.handler.OperationHandler;
import net.saisimon.agtms.core.service.OperationService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.TemplateUtils;

/**
 * 操作记录切面
 * 
 * @author saisimon
 *
 */
@Aspect
@Component
@ConditionalOnClass(JoinPoint.class)
public class OperationAspect {
	
	@Autowired
	private SchedulingTaskExecutor operationThreadPool;
	@Autowired
	private DefaultOperationHandler defaultOperationHandler;
	
	@Pointcut("execution(public * net.saisimon.agtms.web.controller..*.*(..))")
	public void controllerPointcut() {}
	
	@AfterReturning(pointcut="controllerPointcut()", returning="result")
	public void after(JoinPoint joinPoint, Object result) {
		Signature signature = joinPoint.getSignature();
		if (signature instanceof MethodSignature) {
			MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
			Method method = methodSignature.getMethod();
			Operate operate = method.getDeclaredAnnotation(Operate.class);
			if (operate != null) {
				OperationHandler operationHandler = OperationHandlerFactory.getHandler(operate.type());
				if (operationHandler == null) {
					operationHandler = defaultOperationHandler;
				}
				String info = null;
				Object target = joinPoint.getTarget();
				ControllerInfo controllerInfo = target.getClass().getDeclaredAnnotation(ControllerInfo.class);
				if (controllerInfo != null) {
					info = controllerInfo.value();
					if ("management".equals(info)) {
						info = parseManagement(joinPoint.getArgs());
					}
				}
				Operation operation = operationHandler.handle(info, operate, result);
				if (operation == null || operation.getOperatorId() == null) {
					return;
				}
				operationThreadPool.execute(() -> {
					OperationService operationService = OperationServiceFactory.get();
					operationService.saveOrUpdate(operation);
				});
			}
		}
	}
	
	private String parseManagement(Object[] args) {
		if (args == null || args.length == 0) {
			return null;
		}
		Long userId = AuthUtils.getUid();
		if (userId == null) {
			return null;
		}
		Template template = TemplateUtils.getTemplate(args[0], userId);
		if (template == null) {
			return null;
		}
		return template.getTitle();
	}
	
}
