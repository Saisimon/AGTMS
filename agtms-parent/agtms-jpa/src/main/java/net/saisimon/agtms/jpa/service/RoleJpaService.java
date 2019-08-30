package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Role;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.RoleService;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.RoleJpaRepository;

@Service
public class RoleJpaService implements RoleService, JpaOrder {
	
	@Autowired
	private RoleJpaRepository roleJpaRepository;
	
	@Override
	public BaseRepository<Role, Long> getRepository() {
		return roleJpaRepository;
	}
	
}
