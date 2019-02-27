package net.saisimon.agtms.remote.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.service.RemoteService;

@Import(FeignClientsConfiguration.class)
@Service
public class RemoteTemplateService implements RemoteService {
	
	@Autowired
	private Decoder decoder;
	@Autowired
	private Encoder encoder;
	@Autowired
	private Client client;
	@Autowired
	private Contract contract;

	@Override
	public List<Template> templates(String serviceId) {
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + serviceId);
		return apiService.templates();
	}
	
}
