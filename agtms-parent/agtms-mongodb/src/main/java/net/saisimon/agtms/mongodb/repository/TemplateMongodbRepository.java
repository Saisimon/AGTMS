package net.saisimon.agtms.mongodb.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.mongodb.repository.base.BaseMongoRepository;

@Repository
public interface TemplateMongodbRepository extends BaseMongoRepository<Template, Long> {
	
	List<Template> findByNavigationIdAndOperatorId(Long navigationId, Long operatorId);
	
	boolean existsByTitleAndOperatorId(String title, Long operatorId);
	
}
