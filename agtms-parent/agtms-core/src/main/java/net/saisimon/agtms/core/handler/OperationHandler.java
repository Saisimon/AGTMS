package net.saisimon.agtms.core.handler;

import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.domain.entity.Operation;
import net.saisimon.agtms.core.enums.OperateTypes;

/**
 * 操作记录处理接口
 * 
 * @author saisimon
 *
 */
public interface OperationHandler {
	
	Operation handle(String controllerInfo, Operate operate, Object result);
	
	OperateTypes[] keys();
	
}
