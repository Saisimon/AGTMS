package net.saisimon.agtms.core.service;

import org.springframework.core.Ordered;

import net.saisimon.agtms.core.domain.Task;

public interface TaskService extends BaseService<Task, Long>, Ordered {
	
	Task getTask(Long id, Long operatorId);
	
}
