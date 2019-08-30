package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.RoleResource;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.RoleResourceService;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.RoleResourceJpaRepository;

@Service
public class RoleResourceJpaService implements RoleResourceService, JpaOrder {
	
	@Autowired
	private RoleResourceJpaRepository roleResourceJpaRepository;
	
	@Override
	public BaseRepository<RoleResource, Long> getRepository() {
		return roleResourceJpaRepository;
	}
	
}
