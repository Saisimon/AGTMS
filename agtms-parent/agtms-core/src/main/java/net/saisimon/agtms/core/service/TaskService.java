package net.saisimon.agtms.core.service;

import java.util.Optional;

import org.springframework.core.Ordered;

import net.saisimon.agtms.core.domain.entity.Task;

/**
 * 任务服务接口
 * 
 * @author saisimon
 *
 */
public interface TaskService extends BaseService<Task, Long>, Ordered {
	
	default Task getTask(Long id, Long operatorId) {
		if (id == null || operatorId == null) {
			return null;
		}
		Optional<Task> optional = findById(id);
		if (optional.isPresent()) {
			Task task = optional.get();
			if (operatorId == task.getOperatorId()) {
				return task;
			}
		}
		return null;
	}
	
}
