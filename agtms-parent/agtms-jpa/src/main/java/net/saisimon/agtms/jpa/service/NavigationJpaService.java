package net.saisimon.agtms.jpa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.core.order.AbstractOrder;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.jpa.repository.NavigationJpaRepository;

@Service
public class NavigationJpaService extends AbstractOrder implements NavigationService {
	
	@Autowired
	private NavigationJpaRepository navigationJpaRepository;

	@Override
	public BaseRepository<Navigation, Long> getRepository() {
		return navigationJpaRepository;
	}
	
	@Override
	public boolean existNavigation(String title, Long userId) {
		return navigationJpaRepository.existsByTitleAndBelong(title, userId);
	}
	
	@Override
	public Navigation getNavigation(Long id, Long userId) {
		return navigationJpaRepository.findByIdAndBelong(id, userId);
	}
	
	@Override
	public List<Navigation> getNavigations(List<Long> ids, Long userId) {
		return navigationJpaRepository.findByIdInAndBelong(ids, userId);
	}
	
	@Override
	public List<Navigation> getChildrenNavigations(Long parentId, Long userId) {
		return navigationJpaRepository.findByParentIdAndBelong(parentId, userId);
	}

	@Override
	public List<Navigation> getNavigations(Long userId) {
		return navigationJpaRepository.findByBelong(userId);
	}
	
}
