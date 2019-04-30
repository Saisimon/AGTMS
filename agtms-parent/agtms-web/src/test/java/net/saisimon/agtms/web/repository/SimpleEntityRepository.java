package net.saisimon.agtms.web.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;
import net.saisimon.agtms.web.domain.SimpleEntity;

@Repository
public interface SimpleEntityRepository extends BaseJpaRepository<SimpleEntity, Integer> {
	
}
