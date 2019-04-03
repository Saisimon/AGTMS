package net.saisimon.agtms.remote.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.service.RemoteService;
import net.saisimon.agtms.core.util.SystemUtils;

@Import(FeignClientsConfiguration.class)
@Service
@Slf4j
public class RemoteApiService implements RemoteService {
	
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
		if (SystemUtils.isBlank(serviceId)) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + serviceId);
		try {
			return apiService.templates();
		} catch (Exception e) {
			log.error(String.format("remote templates failed. %s", serviceId));
			return null;
		}
	}
	
	@Override
	public Template template(String serviceId, String key) {
		if (SystemUtils.isBlank(serviceId)) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + serviceId);
		try {
			return apiService.template(key);
		} catch (Exception e) {
			log.error(String.format("remote template failed. %s, %s", serviceId, key));
			return null;
		}
	}
	
	@Override
	public LinkedHashMap<?, String> selection(String serviceId, String key, Map<String, Object> body) {
		if (SystemUtils.isBlank(serviceId)) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + serviceId);
		try {
			return apiService.selection(key, body);
		} catch (Exception e) {
			log.error(String.format("remote selection failed. %s, %s", serviceId, key));
			return null;
		}
	}

}
