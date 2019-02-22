package net.saisimon.agtms.rpc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.rpc.repository.GenerateRpcRepository;

@Service
public class GenerateRpcService implements GenerateService {
	
	private static final Sign RPC_SIGN = Sign.builder().name("rpc").text("rpc").build();
	
	@Autowired
	private GenerateRpcRepository generateRpcRepository;
	
	@Override
	public AbstractGenerateRepository getRepository() {
		return generateRpcRepository;
	}
	
	@Override
	public Sign sign() {
		return RPC_SIGN;
	}
	
}
