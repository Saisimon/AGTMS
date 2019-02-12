package net.saisimon.agtms.mongodb.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.Task;
import net.saisimon.agtms.mongodb.repository.base.BaseMongoRepository;

@Repository
public interface TaskMongodbRepository extends BaseMongoRepository<Task, Long> {

	Task findByIdAndOperatorId(Long id, Long operatorId);
	
}
