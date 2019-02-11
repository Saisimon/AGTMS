package net.saisimon.agtms.jpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface TemplateJapRepository extends BaseJpaRepository<Template, Long> {
	
	List<Template> findByNavigationIdAndOperatorId(Long navigationId, Long operatorId);
	
	boolean existsByTitleAndOperatorId(String title, Long operatorId);
	
}
