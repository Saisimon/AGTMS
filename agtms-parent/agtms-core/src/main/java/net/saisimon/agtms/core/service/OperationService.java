package net.saisimon.agtms.core.service;

import org.springframework.core.Ordered;

import net.saisimon.agtms.core.domain.Operation;

/**
 * 操作记录服务接口
 * 
 * @author saisimon
 *
 */
public interface OperationService extends BaseService<Operation, Long>, Ordered {
	
}
