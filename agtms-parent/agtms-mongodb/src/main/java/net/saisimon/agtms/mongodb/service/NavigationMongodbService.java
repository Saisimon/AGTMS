package net.saisimon.agtms.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Navigation;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.mongodb.order.MongodbOrder;
import net.saisimon.agtms.mongodb.repository.NavigationMongodbRepository;
import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepository;

@Service
public class NavigationMongodbService implements NavigationService, MongodbOrder {

	@Autowired
	private NavigationMongodbRepository navigationMongodbRepository;
	@Autowired
	private SequenceService sequenceService;
	
	@Override
	public BaseMongodbRepository<Navigation, Long> getRepository() {
		return navigationMongodbRepository;
	}
	
	@Override
	public Navigation saveOrUpdate(Navigation entity) {
		if (entity == null) {
			return entity;
		}
		if (entity.getId() == null) {
			Long id = sequenceService.nextId(navigationMongodbRepository.getCollectionName());
			entity.setId(id);
		}
		return NavigationService.super.saveOrUpdate(entity);
	}

}
