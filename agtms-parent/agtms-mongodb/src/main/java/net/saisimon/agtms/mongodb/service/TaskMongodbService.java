package net.saisimon.agtms.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Task;
import net.saisimon.agtms.core.service.TaskService;
import net.saisimon.agtms.mongodb.order.MongodbOrder;
import net.saisimon.agtms.mongodb.repository.TaskMongodbRepository;
import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepository;

@Service
public class TaskMongodbService implements TaskService, MongodbOrder {

	@Autowired
	private TaskMongodbRepository taskMongodbRepository;
	@Autowired
	private SequenceService sequenceService;
	
	@Override
	public BaseMongodbRepository<Task, Long> getRepository() {
		return taskMongodbRepository;
	}
	
	@Override
	public Task saveOrUpdate(Task entity) {
		if (entity == null) {
			return entity;
		}
		if (entity.getId() == null) {
			Long id = sequenceService.nextId(taskMongodbRepository.getCollectionName());
			entity.setId(id);
		}
		return taskMongodbRepository.saveOrUpdate(entity);
	}

}
