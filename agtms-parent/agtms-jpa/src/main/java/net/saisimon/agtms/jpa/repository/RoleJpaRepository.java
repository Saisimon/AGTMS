package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.Role;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface RoleJpaRepository extends BaseJpaRepository<Role, Long> {
	
}
