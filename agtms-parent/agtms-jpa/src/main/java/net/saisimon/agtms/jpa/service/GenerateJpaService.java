package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.enums.DataSources;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.jpa.repository.GenerateJpaRepository;

@Service
public class GenerateJpaService implements GenerateService {
	
	@Autowired
	private GenerateJpaRepository generateJpaRepository;

	@Override
	public AbstractGenerateRepository getRepository() {
		return generateJpaRepository;
	}

	@Override
	public DataSources key() {
		return DataSources.JPA;
	}
	
	@Override
	public Boolean saveOrUpdate(Domain domain) {
		Assert.notNull(domain, "domain can not be null");
		return generateJpaRepository.saveOrUpdate(domain) != null;
	}

}
