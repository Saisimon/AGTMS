package net.saisimon.agtms.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.mongodb.order.MongodbOrder;
import net.saisimon.agtms.mongodb.repository.TemplateMongodbRepository;

@Service
public class TemplateMongodbService implements TemplateService, MongodbOrder {
	
	@Autowired
	private TemplateMongodbRepository templateMongodbRepository;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private SequenceService sequenceService;

	@Override
	public BaseRepository<Template, Long> getRepository() {
		return templateMongodbRepository;
	}
	
	@Override
	public Template saveOrUpdate(Template entity) {
		if (entity.getId() == null) {
			Long id = sequenceService.nextId(templateMongodbRepository.getCollectionName());
			entity.setId(id);
		}
		return TemplateService.super.saveOrUpdate(entity);
	}

	@Override
	public boolean dropTable(Template template) {
		mongoTemplate.dropCollection(TemplateUtils.getTableName(template));
		return true;
	}
	
}
