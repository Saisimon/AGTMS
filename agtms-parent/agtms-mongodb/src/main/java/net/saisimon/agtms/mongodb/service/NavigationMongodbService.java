package net.saisimon.agtms.mongodb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.mongodb.order.AbstractMongodbOrder;
import net.saisimon.agtms.mongodb.repository.NavigationMongodbRepository;

@Service
public class NavigationMongodbService extends AbstractMongodbOrder implements NavigationService {

	@Autowired
	private NavigationMongodbRepository navigationMongodbRepository;
	@Autowired
	private SequenceService sequenceService;
	
	@Override
	public BaseRepository<Navigation, Long> getRepository() {
		return navigationMongodbRepository;
	}
	
	@Override
	public boolean existNavigation(String title, Long userId) {
		return navigationMongodbRepository.existsByTitleAndBelong(title, userId);
	}
	
	@Override
	public Navigation getNavigation(Long id, Long userId) {
		return navigationMongodbRepository.findByIdAndBelong(id, userId);
	}
	
	@Override
	public List<Navigation> getNavigations(List<Long> ids, Long userId) {
		return navigationMongodbRepository.findByIdInAndBelong(ids, userId);
	}
	
	@Override
	public List<Navigation> getChildrenNavigations(Long parentId, Long userId) {
		return navigationMongodbRepository.findByParentIdAndBelong(parentId, userId);
	}
	
	@Override
	public List<Navigation> getNavigations(Long userId) {
		return navigationMongodbRepository.findByBelong(userId);
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
