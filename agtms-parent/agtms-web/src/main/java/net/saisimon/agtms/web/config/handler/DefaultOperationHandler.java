package net.saisimon.agtms.web.config.handler;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.domain.entity.Operation;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.handler.OperationHandler;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 默认操作处理器
 * 
 * @author saisimon
 *
 */
@Component
public class DefaultOperationHandler implements OperationHandler {

	@Override
	public Operation handle(String controllerInfo, Operate operate, Object result) {
		Operation operation = new Operation();
		Long userId = AuthUtils.getUid();
		if (userId == null) {
			return null;
		}
		operation.setOperatorId(userId);
		operation.setOperateType(operate.type().getType());
		operation.setOperateTime(new Date());
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
			operation.setOperateUrl(request.getRequestURI());
			operation.setOperateIp(SystemUtils.getIPAddress(request));
			if (response != null) {
				operation.setOperateStatus(response.getStatus());
			}
		}
		if (controllerInfo != null) {
			String value = operate.value();
			if (SystemUtils.isBlank(value)) {
				value = operate.type().getName();
			}
			operation.setOperateContent(controllerInfo + "," + value);
		}
		return operation;
	}

	@Override
	public OperateTypes[] keys() {
		return null;
	}

}
