package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.UserRole;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.UserRoleService;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.UserRoleJpaRepository;

@Service
public class UserRoleJpaService implements UserRoleService, JpaOrder {
	
	@Autowired
	private UserRoleJpaRepository userRoleJpaRepository;
	
	@Override
	public BaseRepository<UserRole, Long> getRepository() {
		return userRoleJpaRepository;
	}
	
}
