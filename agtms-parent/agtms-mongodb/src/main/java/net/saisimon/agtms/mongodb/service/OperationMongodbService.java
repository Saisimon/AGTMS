package net.saisimon.agtms.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.Operation;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.OperationService;
import net.saisimon.agtms.mongodb.order.MongodbOrder;
import net.saisimon.agtms.mongodb.repository.OperationMongodbRepository;

@Service
public class OperationMongodbService implements OperationService, MongodbOrder {

	@Autowired
	private OperationMongodbRepository operationMongodbRepository;
	@Autowired
	private SequenceService sequenceService;
	
	@Override
	public BaseRepository<Operation, Long> getRepository() {
		return operationMongodbRepository;
	}
	
	@Override
	public Operation saveOrUpdate(Operation entity) {
		if (entity.getId() == null) {
			Long id = sequenceService.nextId(operationMongodbRepository.getCollectionName());
			entity.setId(id);
		}
		return OperationService.super.saveOrUpdate(entity);
	}

}
