package net.saisimon.agtms.mongodb.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepository;

@Repository
public interface TemplateMongodbRepository extends BaseMongodbRepository<Template, Long> {
	
}
