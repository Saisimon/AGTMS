package net.saisimon.agtms.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.mongodb.repository.GenerateMongodbRepository;

@Service
public class GenerateMongodbService implements GenerateService {
	
	private static final Sign MONGODB_SIGN = Sign.builder().name("mongodb").text("MONGODB").order(Ordered.HIGHEST_PRECEDENCE).build();
	
	@Autowired
	private GenerateMongodbRepository generateMongoRepository;
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
	public Domain saveDomain(Domain domain, Long operatorId) {
		Assert.notNull(domain, "domain can not be null");
		Long id = sequenceService.nextId(TemplateUtils.getTableName(template()));
		domain.setField(Constant.ID, id, Long.class);
		return GenerateService.super.saveDomain(domain, operatorId);
	}

}
