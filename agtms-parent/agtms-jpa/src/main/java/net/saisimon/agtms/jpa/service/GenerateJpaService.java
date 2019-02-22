package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
}
