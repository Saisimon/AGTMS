package net.saisimon.agtms.remote.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.remote.repository.GenerateRemoteRepository;

@Service
public class GenerateRemoteService implements GenerateService {
	
	private static final Sign REMOTE_SIGN = Sign.builder().name("remote").text("REMOTE").order(Ordered.LOWEST_PRECEDENCE).build();
	
	@Autowired
	private GenerateRemoteRepository generateRemoteRepository;
	
	@Override
	public AbstractGenerateRepository getRepository() {
		return generateRemoteRepository;
	}
	
	@Override
	public Sign sign() {
		return REMOTE_SIGN;
	}
	
}
