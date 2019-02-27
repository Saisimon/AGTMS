package net.saisimon.agtms.mongodb.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.saisimon.agtms.core.constant.Constant;
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
	public Domain saveDomain(Domain domain) {
		Assert.notNull(domain, "domain can not be null");
		Long id = sequenceService.nextId(TemplateUtils.getTableName(template()));
		domain.setField(Constant.ID, id, Long.class);
		return GenerateService.super.saveDomain(domain);
	}

	@Override
	public Domain findById(Long id, Long operatorId) {
		if (id == null || operatorId == null) {
			return null;
		}
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		FilterRequest filter = FilterRequest.build().and("_id", id);
		Optional<Domain> optional = repository.findOne(filter, null);
		if (optional.isPresent()) {
			Domain domain = optional.get();
			Object obj = domain.getField(Constant.OPERATORID);
			String creator = obj == null ? "" : obj.toString();
			if (operatorId.toString().equals(creator)) {
				return domain;
			}
		}
		return null;
	}
	
}
