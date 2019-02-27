package net.saisimon.agtms.remote.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.remote.service.ApiService;

@Import(FeignClientsConfiguration.class)
@Repository
@Slf4j
public class GenerateRemoteRepository extends AbstractGenerateRepository implements BaseRepository<Domain, Long> {
	
	@Autowired
	private Decoder decoder;
	@Autowired
	private Encoder encoder;
	@Autowired
	private Client client;
	@Autowired
	private Contract contract;
	
	@Override
	public Long count(FilterRequest filter) {
		Template template = template();
		if (StringUtils.isBlank(template.getSourceUrl()) || StringUtils.isBlank(template.getKey())) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getSourceUrl());
		Map<String, Object> filterMap = new HashMap<>();
		if (filter != null) {
			filterMap = filter.toMap();
		}
		return apiService.count(template.getKey(), filterMap);
	}

	@Override
	public List<Domain> findList(FilterRequest filter, FilterSort sort) {
		Template template = template();
		if (StringUtils.isBlank(template.getSourceUrl()) || StringUtils.isBlank(template.getKey())) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getSourceUrl());
		Map<String, Object> body = new HashMap<>();
		Map<String, Object> filterMap = new HashMap<>();
		if (filter != null) {
			filterMap = filter.toMap();
		}
		body.put("filter", filterMap);
		if (sort != null) {
			body.put("sort", sort.toString());
		}
		List<Map<String, Object>> list = apiService.findList(template.getKey(), body);
		try {
			return conversions(list);
		} catch (GenerateException e) {
			log.error("find list error", e);
			return new ArrayList<>(0);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<Domain> findPage(FilterRequest filter, FilterPageable pageable) {
		Template template = template();
		if (StringUtils.isBlank(template.getSourceUrl()) || StringUtils.isBlank(template.getKey())) {
			return null;
		}
		if (pageable == null) {
			pageable = FilterPageable.build(null);
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getSourceUrl());
		Map<String, Object> body = new HashMap<>();
		Map<String, Object> filterMap = new HashMap<>();
		if (filter != null) {
			filterMap = filter.toMap();
		}
		body.put("filter", filterMap);
		body.put("pageable", pageable.toMap());
		Map<String, Object> map = apiService.findPage(template.getKey(), body);
		if (CollectionUtils.isEmpty(map)) {
			return null;
		}
		Pageable springPageable = pageable.getPageable();
		List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("rows");
		Object total = map.get("total");
		try {
			List<Domain> domains = conversions(list);
			return new PageImpl<>(domains, springPageable, Long.parseLong(total.toString()));
		} catch (GenerateException e) {
			log.error("find page error", e);
			return new PageImpl<>(new ArrayList<>(0), springPageable, 0L);
		}
	}

	@Override
	public Optional<Domain> findOne(FilterRequest filter, FilterSort sort) {
		Template template = template();
		if (StringUtils.isBlank(template.getSourceUrl()) || StringUtils.isBlank(template.getKey())) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getSourceUrl());
		Map<String, Object> body = new HashMap<>();
		Map<String, Object> filterMap = new HashMap<>();
		if (filter != null) {
			filterMap = filter.toMap();
		}
		body.put("filter", filterMap);
		if (sort != null) {
			body.put("sort", sort.toString());
		}
		Map<String, Object> map = apiService.findOne(template.getKey(), body);
		try {
			return Optional.ofNullable(conversion(map));
		} catch (GenerateException e) {
			log.error("find one error", e);
			return Optional.empty();
		}
	}

	@Override
	public Long delete(FilterRequest filter) {
		Template template = template();
		if (StringUtils.isBlank(template.getSourceUrl()) || StringUtils.isBlank(template.getKey())) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getSourceUrl());
		Map<String, Object> filterMap = new HashMap<>();
		if (filter != null) {
			filterMap = filter.toMap();
		}
		return apiService.delete(template.getKey(), filterMap);
	}

	@Override
	public Domain saveOrUpdate(Domain entity) {
		if (entity == null) {
			return null;
		}
		Template template = template();
		if (StringUtils.isBlank(template.getSourceUrl()) || StringUtils.isBlank(template.getKey())) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getSourceUrl());
		Map<String, Object> map = apiService.saveOrUpdate(template.getKey(), SystemUtils.toJson(entity));
		try {
			return conversion(map);
		} catch (GenerateException e) {
			log.error("saveOrUpdate error", e);
			return null;
		}
	}
	
	@Override
	public void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		if (CollectionUtils.isEmpty(updateMap)) {
			return;
		}
		Template template = template();
		if (StringUtils.isBlank(template.getSourceUrl()) || StringUtils.isBlank(template.getKey())) {
			return;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getSourceUrl());
		Map<String, Object> body = new HashMap<>();
		Map<String, Object> filterMap = new HashMap<>();
		if (filter != null) {
			filterMap = filter.toMap();
		}
		body.put("filter", filterMap);
		body.put("update", updateMap);
		apiService.batchUpdate(template.getKey(), body);
	}

	private List<Domain> conversions(List<Map<String, Object>> list) throws GenerateException {
		List<Domain> domains = new ArrayList<>();
		for (Map<String, Object> map : list) {
			Domain domain = conversion(map);
			if (domain != null) {
				domains.add(domain);
			}
		}
		return domains;
	}
	
}
