package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.ResourceService;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.ResourceJpaRepository;

@Service
public class ResourceJpaService implements ResourceService, JpaOrder {
	
	@Autowired
	private ResourceJpaRepository resourceJpaRepository;
	
	@Override
	public BaseRepository<Resource, Long> getRepository() {
		return resourceJpaRepository;
	}
	
}
