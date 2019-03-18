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
	
	/**
	 * 操作记录处理
	 * 
	 * @param controllerInfo 控制器信息
	 * @param operate 操作信息
	 * @param result 方法返回值
	 * @return 操作记录实体对象
	 * 
	 * @See net.saisimon.agtms.core.annotation.ControllerInfo
	 */
	Operation handle(String controllerInfo, Operate operate, Object result);
	
	/**
	 * 操作记录处理器对应的操作类型
	 * 
	 * @return
	 */
	OperateTypes[] keys();
	
}
