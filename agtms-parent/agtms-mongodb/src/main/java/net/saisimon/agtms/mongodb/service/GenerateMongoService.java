package net.saisimon.agtms.mongodb.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.mongodb.repository.GenerateMongoRepository;

@Service
public class GenerateMongoService implements GenerateService {
	
	private static final Sign MONGODB_SIGN = Sign.builder().name("mongodb").text("mongodb").build();
	
	@Autowired
	private GenerateMongoRepository generateMongoRepository;
	@Autowired
	private SequenceService sequenceService;
	
	@Override
	public Sign sign() {
		return MONGODB_SIGN;
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

	@Override
	public void updateDomain(Long id, Map<String, Object> updateMap) {
		FilterRequest filter = FilterRequest.build().and("_id", id);
		generateMongoRepository.batchUpdate(filter, updateMap);
	}
	
}
