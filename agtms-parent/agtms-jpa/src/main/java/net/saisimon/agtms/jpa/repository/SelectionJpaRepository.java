package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface SelectionJpaRepository extends BaseJpaRepository<Selection, Long> {
	
}
