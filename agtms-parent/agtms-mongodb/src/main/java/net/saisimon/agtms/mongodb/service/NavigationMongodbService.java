package net.saisimon.agtms.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.mongodb.order.MongodbOrder;
import net.saisimon.agtms.mongodb.repository.NavigationMongodbRepository;

@Service
public class NavigationMongodbService implements NavigationService, MongodbOrder {

	@Autowired
	private NavigationMongodbRepository navigationMongodbRepository;
	@Autowired
	private SequenceService sequenceService;
	
	@Override
	public BaseRepository<Navigation, Long> getRepository() {
		return navigationMongodbRepository;
	}
	
	@Override
	public Navigation saveOrUpdate(Navigation entity) {
		if (entity.getId() == null) {
			Long id = sequenceService.nextId(navigationMongodbRepository.getCollectionName());
			entity.setId(id);
		}
		return NavigationService.super.saveOrUpdate(entity);
	}

}
