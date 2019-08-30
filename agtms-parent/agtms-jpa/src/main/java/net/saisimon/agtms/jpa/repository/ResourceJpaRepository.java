package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface ResourceJpaRepository extends BaseJpaRepository<Resource, Long> {
	
}
