package net.saisimon.agtms.jpa.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.SelectionOption;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface SelectionOptionJpaRepository extends BaseJpaRepository<SelectionOption, Long> {
	
	List<SelectionOption> findBySelectionId(Long selectionId, Pageable pageable);
	
	List<SelectionOption> findBySelectionIdAndValueIn(Long selectionId, Set<String> values);
	
	List<SelectionOption> findBySelectionIdAndTextIn(Long selectionId, Set<String> texts);
	
	List<SelectionOption> findBySelectionIdAndTextContaining(Long selectionId, String text, Pageable pageable);
	
	long deleteBySelectionId(Long selectionId);

}
