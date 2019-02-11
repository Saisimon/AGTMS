package net.saisimon.agtms.mongodb.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.Operation;
import net.saisimon.agtms.mongodb.repository.base.BaseMongoRepository;

@Repository
public interface OperationMongodbRepository extends BaseMongoRepository<Operation, Long> {
	
}
