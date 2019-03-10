package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Navigation;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.NavigationJpaRepository;

@Service
public class NavigationJpaService implements NavigationService, JpaOrder {
	
	@Autowired
	private NavigationJpaRepository navigationJpaRepository;

	@Override
	public BaseRepository<Navigation, Long> getRepository() {
		return navigationJpaRepository;
	}
	
}
