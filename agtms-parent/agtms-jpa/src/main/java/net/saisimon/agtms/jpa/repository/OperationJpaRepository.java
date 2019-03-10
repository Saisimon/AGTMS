package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.Operation;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface OperationJpaRepository extends BaseJpaRepository<Operation, Long> {
	
}
