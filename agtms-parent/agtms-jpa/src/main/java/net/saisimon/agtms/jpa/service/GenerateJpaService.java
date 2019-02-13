package net.saisimon.agtms.jpa.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.jpa.repository.GenerateJpaRepository;

@Service
public class GenerateJpaService implements GenerateService {
	
	private static final Sign JPA_SIGN = Sign.builder().name("jpa").text("jpa").build();
	
	@Autowired
	private GenerateJpaRepository generateJpaRepository;

	@Override
	public AbstractGenerateRepository getRepository() {
		return generateJpaRepository;
	}

	@Override
	public Sign sign() {
		return JPA_SIGN;
	}
	
	@Override
	public Boolean saveOrUpdate(Domain domain) {
		Assert.notNull(domain, "domain can not be null");
		return generateJpaRepository.saveOrUpdate(domain) != null;
	}

	@Override
	public void updateDomain(Long id, Map<String, Object> updateMap) {
		FilterRequest filter = FilterRequest.build().and(Constant.ID, id);
		generateJpaRepository.batchUpdate(filter, updateMap);
	}

}
