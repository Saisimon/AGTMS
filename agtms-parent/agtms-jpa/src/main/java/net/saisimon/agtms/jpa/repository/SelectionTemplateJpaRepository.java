package net.saisimon.agtms.jpa.repository;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface SelectionTemplateJpaRepository extends BaseJpaRepository<SelectionTemplate, Long> {
	
	@Nullable
	SelectionTemplate findBySelectionId(Long selectionId);
	
	long deleteBySelectionId(Long selectionId);
}
