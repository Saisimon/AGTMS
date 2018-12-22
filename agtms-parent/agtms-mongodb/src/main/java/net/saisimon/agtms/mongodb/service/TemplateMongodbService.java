package net.saisimon.agtms.mongodb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.mongodb.order.AbstractMongodbOrder;
import net.saisimon.agtms.mongodb.repository.TemplateMongodbRepository;

@Service
public class TemplateMongodbService extends AbstractMongodbOrder implements TemplateService {
	
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
	public boolean exists(String title, Long userId) {
		return templateMongodbRepository.existsByTitleAndUserId(title, userId);
	}

	@Override
	public List<Template> getTemplates(Long navigationId, Long userId) {
		return templateMongodbRepository.findByNavigationIdAndUserId(navigationId, userId);
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
	public void dropTable(Template template) {
		mongoTemplate.dropCollection(TemplateUtils.getTableName(template));
	}
	
}
