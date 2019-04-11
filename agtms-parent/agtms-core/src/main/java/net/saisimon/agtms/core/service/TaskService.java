package net.saisimon.agtms.core.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.Ordered;

import net.saisimon.agtms.core.domain.entity.Task;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.factory.TokenFactory;

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
		if (!optional.isPresent()) {
			return null;
		}
		Task task = optional.get();
		UserToken userToken = TokenFactory.get().getToken(operatorId, false);
		if (userToken != null && userToken.isAdmin()) {
			return task;
		}
		if (operatorId.equals(task.getOperatorId())) {
			return task;
		}
		return null;
	}
	
	@Override
	@Cacheable(cacheNames="task", key="#p0")
	default Optional<Task> findById(Long id) {
		return BaseService.super.findById(id);
	}

	@Override
	@CacheEvict(cacheNames="task", key="#p0.id")
	default void delete(Task entity) {
		BaseService.super.delete(entity);
	}

	@Override
	@CachePut(cacheNames="task", key="#p0.id")
	default Task saveOrUpdate(Task entity) {
		return BaseService.super.saveOrUpdate(entity);
	}

	@Override
	@CacheEvict(cacheNames="selection", key="#p0")
	default void update(Long id, Map<String, Object> updateMap) {
		BaseService.super.update(id, updateMap);
	}

	@Override
	@CacheEvict(cacheNames="selection", allEntries=true)
	default void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		BaseService.super.batchUpdate(filter, updateMap);
	}
	
	@Override
	@CacheEvict(cacheNames="selection", allEntries=true)
	default Long delete(FilterRequest filter) {
		return BaseService.super.delete(filter);
	}
	
}
