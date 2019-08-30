package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.UserRole;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface UserRoleJpaRepository extends BaseJpaRepository<UserRole, Long> {
	
}
