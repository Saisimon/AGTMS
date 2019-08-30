package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.RoleResource;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface RoleResourceJpaRepository extends BaseJpaRepository<RoleResource, Long> {
	
}
