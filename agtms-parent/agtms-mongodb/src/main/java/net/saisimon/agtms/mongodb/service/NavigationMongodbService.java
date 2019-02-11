package net.saisimon.agtms.mongodb.service;

import java.util.List;

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
	public boolean existNavigation(String title, Long operatorId) {
		return navigationMongodbRepository.existsByTitleAndOperatorId(title, operatorId);
	}
	
	@Override
	public Navigation getNavigation(Long id, Long operatorId) {
		return navigationMongodbRepository.findByIdAndOperatorId(id, operatorId);
	}
	
	@Override
	public List<Navigation> getNavigations(List<Long> ids, Long operatorId) {
		return navigationMongodbRepository.findByIdInAndOperatorId(ids, operatorId);
	}
	
	@Override
	public List<Navigation> getChildrenNavigations(Long parentId, Long operatorId) {
		return navigationMongodbRepository.findByParentIdAndOperatorId(parentId, operatorId);
	}
	
	@Override
	public List<Navigation> getNavigations(Long operatorId) {
		return navigationMongodbRepository.findByOperatorId(operatorId);
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
