package net.saisimon.agtms.mongodb.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.Operation;
import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepository;

@Repository
public interface OperationMongodbRepository extends BaseMongodbRepository<Operation, Long> {
	
}
