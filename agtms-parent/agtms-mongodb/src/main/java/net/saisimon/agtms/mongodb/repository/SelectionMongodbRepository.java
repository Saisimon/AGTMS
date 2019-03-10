package net.saisimon.agtms.mongodb.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepository;

@Repository
public interface SelectionMongodbRepository extends BaseMongodbRepository<Selection, Long> {
	
}
