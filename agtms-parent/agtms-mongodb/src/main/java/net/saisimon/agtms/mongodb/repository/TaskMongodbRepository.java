package net.saisimon.agtms.mongodb.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.Task;
import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepository;

@Repository
public interface TaskMongodbRepository extends BaseMongodbRepository<Task, Long> {

}
