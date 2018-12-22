package net.saisimon.agtms.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.enums.DataSources;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.mongodb.repository.GenerateMongoRepository;

@Service
public class GenerateMongoService implements GenerateService {
	
	@Autowired
	private GenerateMongoRepository generateMongoRepository;
	@Autowired
	private SequenceService sequenceService;
	
	@Override
	public DataSources key() {
		return DataSources.MONGO;
	}

	@Override
	public AbstractGenerateRepository getRepository() {
		return generateMongoRepository;
	}
	
	@Override
	public Boolean saveOrUpdate(Domain domain) {
		Assert.notNull(domain, "domain can not be null");
		if (domain.getField("id") == null) {
			Long id = sequenceService.nextId(TemplateUtils.getTableName(template()));
			domain.setField("id", id, Long.class);
		}
		return generateMongoRepository.saveOrUpdate(domain) != null;
	}
	
}
