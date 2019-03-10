package net.saisimon.agtms.mongodb.repository;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepository;

@Repository
public interface SelectionTemplateMongodbRepository extends BaseMongodbRepository<SelectionTemplate, Long> {
	
	@Nullable
	SelectionTemplate findBySelectionId(Long selectionId);
	
	long deleteBySelectionId(Long selectionId);
}
