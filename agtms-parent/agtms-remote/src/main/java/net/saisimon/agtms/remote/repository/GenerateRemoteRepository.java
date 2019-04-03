package net.saisimon.agtms.remote.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import cn.hutool.core.map.MapUtil;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
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
		if (SystemUtils.isBlank(template.getService()) || SystemUtils.isBlank(template.getKey())) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getService());
		Map<String, Object> filterMap = null;
		if (filter != null) {
			filterMap = filter.toMap();
		}
		try {
			return apiService.count(template.getKey(), filterMap);
		} catch (Exception e) {
			log.error(String.format("remote count failed. %s, %s", template.getService(), template.getKey()));
			return null;
		}
	}

	@Override
	public List<Domain> findList(FilterRequest filter, FilterSort sort, String... properties) {
		Template template = template();
		if (SystemUtils.isBlank(template.getService()) || SystemUtils.isBlank(template.getKey())) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getService());
		Map<String, Object> body = new HashMap<>();
		Map<String, Object> filterMap = null;
		if (filter != null) {
			filterMap = filter.toMap();
		}
		body.put(Constant.Param.FILTER, filterMap);
		if (sort != null) {
			body.put(Constant.Param.SORT, sort.toString());
		}
		if (properties != null && properties.length > 0) {
			body.put(Constant.Param.PROPERTIES, Stream.of(properties).collect(Collectors.joining(",")));
		}
		try {
			List<Map<String, Object>> list = apiService.findList(template.getKey(), body);
			return conversions(list);
		} catch (GenerateException e) {
			log.error("find list error", e);
			return new ArrayList<>(0);
		} catch (Exception e) {
			log.error(String.format("remote findList failed. %s, %s", template.getService(), template.getKey()));
			return null;
		}
	}
	
	@Override
	public List<Domain> findList(FilterRequest filter, FilterPageable pageable, String... properties) {
		Template template = template();
		if (SystemUtils.isBlank(template.getService()) || SystemUtils.isBlank(template.getKey())) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getService());
		Map<String, Object> body = MapUtil.newHashMap(3);
		Map<String, Object> filterMap = null;
		if (filter != null) {
			filterMap = filter.toMap();
		}
		body.put(Constant.Param.FILTER, filterMap);
		if (pageable != null) {
			body.put(Constant.Param.PAGEABLE, pageable.toMap());
		}
		if (properties != null && properties.length > 0) {
			body.put(Constant.Param.PROPERTIES, Stream.of(properties).collect(Collectors.joining(",")));
		}
		try {
			List<Map<String, Object>> list = apiService.findList(template.getKey(), body);
			return conversions(list);
		} catch (GenerateException e) {
			log.error("find list error", e);
			return new ArrayList<>(0);
		} catch (Exception e) {
			log.error(String.format("remote findList failed. %s, %s", template.getService(), template.getKey()));
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<Domain> findPage(FilterRequest filter, FilterPageable pageable, String... properties) {
		Template template = template();
		if (SystemUtils.isBlank(template.getService()) || SystemUtils.isBlank(template.getKey())) {
			return null;
		}
		if (pageable == null) {
			pageable = FilterPageable.build(null);
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getService());
		Map<String, Object> body = MapUtil.newHashMap(3);
		Map<String, Object> filterMap = null;
		if (filter != null) {
			filterMap = filter.toMap();
		}
		body.put(Constant.Param.FILTER, filterMap);
		body.put(Constant.Param.PAGEABLE, pageable.toMap());
		if (properties != null && properties.length > 0) {
			body.put(Constant.Param.PROPERTIES, Stream.of(properties).collect(Collectors.joining(",")));
		}
		Pageable springPageable = pageable.getPageable();
		try {
			Map<String, Object> map = apiService.findPage(template.getKey(), body);
			if (CollectionUtils.isEmpty(map)) {
				return new PageImpl<>(new ArrayList<>(0), springPageable, 0L);
			}
			List<Map<String, Object>> list = (List<Map<String, Object>>) map.get(Constant.Param.ROWS);
			Object total = map.get(Constant.Param.TOTAL);
			List<Domain> domains = conversions(list);
			return new PageImpl<>(domains, springPageable, Long.parseLong(total.toString()));
		} catch (GenerateException e) {
			log.error("find page error", e);
			return new PageImpl<>(new ArrayList<>(0), springPageable, 0L);
		} catch (Exception e) {
			log.error(String.format("remote findPage failed. %s, %s", template.getService(), template.getKey()));
			return null;
		}
	}

	@Override
	public Optional<Domain> findOne(FilterRequest filter, FilterSort sort, String... properties) {
		Template template = template();
		if (SystemUtils.isBlank(template.getService()) || SystemUtils.isBlank(template.getKey())) {
			return Optional.empty();
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getService());
		Map<String, Object> body = MapUtil.newHashMap(3);
		Map<String, Object> filterMap = null;
		if (filter != null) {
			filterMap = filter.toMap();
		}
		body.put(Constant.Param.FILTER, filterMap);
		if (sort != null) {
			body.put(Constant.Param.SORT, sort.toString());
		}
		if (properties != null && properties.length > 0) {
			body.put(Constant.Param.PROPERTIES, Stream.of(properties).collect(Collectors.joining(",")));
		}
		try {
			Map<String, Object> map = apiService.findOne(template.getKey(), body);
			return Optional.ofNullable(conversion(map));
		} catch (GenerateException e) {
			log.error("find one error", e);
			return Optional.empty();
		} catch (Exception e) {
			log.error(String.format("remote findOne failed. %s, %s", template.getService(), template.getKey()));
			return Optional.empty();
		}
	}
	
	@Override
	public Long delete(FilterRequest filter) {
		Template template = template();
		if (SystemUtils.isBlank(template.getService()) || SystemUtils.isBlank(template.getKey())) {
			return null;
		}
		if (!TemplateUtils.hasOneOfFunctions(template, Functions.REMOVE, Functions.BATCH_REMOVE)) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getService());
		Map<String, Object> filterMap = null;
		if (filter != null) {
			filterMap = filter.toMap();
		}
		try {
			return apiService.delete(template.getKey(), filterMap);
		} catch (Exception e) {
			log.error(String.format("remote delete failed. %s, %s", template.getService(), template.getKey()));
			return null;
		}
	}
	
	@Override
	public void delete(Domain entity) {
		Template template = template();
		if (SystemUtils.isBlank(template.getService()) || SystemUtils.isBlank(template.getKey())) {
			return;
		}
		if (!TemplateUtils.hasOneOfFunctions(template, Functions.REMOVE, Functions.BATCH_REMOVE)) {
			return;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getService());
		try {
			apiService.deleteEntity(template.getKey(), SystemUtils.toJson(entity));
		} catch (Exception e) {
			log.error(String.format("remote delete failed. %s, %s", template.getService(), template.getKey()));
		}
	}
	
	@Override
	public Domain saveOrUpdate(Domain entity) {
		if (entity == null) {
			return null;
		}
		Template template = template();
		if (SystemUtils.isBlank(template.getService()) || SystemUtils.isBlank(template.getKey())) {
			return null;
		}
		if (!TemplateUtils.hasOneOfFunctions(template, Functions.CREATE, Functions.EDIT, Functions.BATCH_EDIT)) {
			return null;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getService());
		Map<String, Object> map = apiService.saveOrUpdate(template.getKey(), SystemUtils.toJson(entity));
		try {
			return conversion(map);
		} catch (GenerateException e) {
			log.error("saveOrUpdate error", e);
			return null;
		} catch (Exception e) {
			log.error(String.format("remote saveOrUpdate failed. %s, %s", template.getService(), template.getKey()));
			return null;
		}
	}
	
	@Override
	public void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		if (CollectionUtils.isEmpty(updateMap)) {
			return;
		}
		Template template = template();
		if (SystemUtils.isBlank(template.getService()) || SystemUtils.isBlank(template.getKey())) {
			return;
		}
		if (!TemplateUtils.hasOneOfFunctions(template, Functions.BATCH_EDIT)) {
			return;
		}
		ApiService apiService = Feign.builder().decoder(decoder).encoder(encoder).client(client).contract(contract).target(ApiService.class, "http://" + template.getService());
		Map<String, Object> body = MapUtil.newHashMap(2);
		Map<String, Object> filterMap = null;
		if (filter != null) {
			filterMap = filter.toMap();
		}
		body.put(Constant.Param.FILTER, filterMap);
		body.put(Constant.Param.UPDATE, updateMap);
		try {
			apiService.batchUpdate(template.getKey(), body);
		} catch (Exception e) {
			log.error(String.format("remote batchUpdate failed. %s, %s", template.getService(), template.getKey()));
		}
	}

	private List<Domain> conversions(List<Map<String, Object>> list) throws GenerateException {
		List<Domain> domains = new ArrayList<>(list.size());
		for (Map<String, Object> map : list) {
			Domain domain = conversion(map);
			if (domain != null) {
				domains.add(domain);
			}
		}
		return domains;
	}

}
