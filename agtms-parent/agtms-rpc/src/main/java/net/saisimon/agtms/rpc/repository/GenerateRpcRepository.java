package net.saisimon.agtms.rpc.repository;

import static net.saisimon.agtms.core.constant.Constant.Param.CONTENT;
import static net.saisimon.agtms.core.constant.Constant.Param.DOMAIN;
import static net.saisimon.agtms.core.constant.Constant.Param.FILTER;
import static net.saisimon.agtms.core.constant.Constant.Param.PAGEABLE;
import static net.saisimon.agtms.core.constant.Constant.Param.SORT;
import static net.saisimon.agtms.core.constant.Constant.Param.TOTAL;
import static net.saisimon.agtms.core.constant.Constant.Param.UPDATE;
import static net.saisimon.agtms.rpc.constant.RpcConstant.BATCH_UPDATE;
import static net.saisimon.agtms.rpc.constant.RpcConstant.COUNT;
import static net.saisimon.agtms.rpc.constant.RpcConstant.DELETE;
import static net.saisimon.agtms.rpc.constant.RpcConstant.DELETE_BY_ID;
import static net.saisimon.agtms.rpc.constant.RpcConstant.EXISTS;
import static net.saisimon.agtms.rpc.constant.RpcConstant.FIND_BY_ID;
import static net.saisimon.agtms.rpc.constant.RpcConstant.FIND_LIST;
import static net.saisimon.agtms.rpc.constant.RpcConstant.FIND_ONE;
import static net.saisimon.agtms.rpc.constant.RpcConstant.FIND_PAGE;
import static net.saisimon.agtms.rpc.constant.RpcConstant.SAVE_OR_UPDATE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.util.MappingUtils;

@Repository
@Slf4j
public class GenerateRpcRepository extends AbstractGenerateRepository implements BaseRepository<Domain, Long> {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public Long count(FilterRequest filter) {
		Map<String, String> mapping = mapping();
		if (mapping == null) {
			return null;
		}
		String countUrl = template().getSourceUrl() + COUNT;
		Map<String, Object> paramMap = new HashMap<>();
		filter.mapping(mapping);
		paramMap.put(FILTER, filter);
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(paramMap);
		ResponseEntity<Long> response = restTemplate.postForEntity(countUrl, httpEntity, Long.class);
		if (response != null && response.getStatusCode() == HttpStatus.OK) {
			return response.getBody();
		} else {
			return null;
		}
	}

	@Override
	public Boolean exists(FilterRequest filter) {
		Map<String, String> mapping = mapping();
		if (mapping == null) {
			return null;
		}
		String existsUrl = template().getSourceUrl() + EXISTS;
		Map<String, Object> paramMap = new HashMap<>();
		filter.mapping(mapping);
		paramMap.put(FILTER, filter);
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(paramMap);
		ResponseEntity<Boolean> response = restTemplate.postForEntity(existsUrl, httpEntity, Boolean.class);
		if (response != null && response.getStatusCode() == HttpStatus.OK) {
			return response.getBody();
		} else {
			return null;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Domain> findList(FilterRequest filter, FilterSort sort) {
		Map<String, String> mapping = mapping();
		if (mapping == null) {
			return null;
		}
		String findListUrl = template().getSourceUrl() + FIND_LIST;
		Map<String, Object> paramMap = new HashMap<>();
		filter.mapping(mapping);
		sort.mapping(mapping);
		paramMap.put(FILTER, filter);
		paramMap.put(SORT, sort.toString());
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(paramMap);
		ResponseEntity<List> response = restTemplate.postForEntity(findListUrl, httpEntity, List.class);
		if (response != null && response.getStatusCode() == HttpStatus.OK) {
			List list = response.getBody();
			try {
				return conversions(list, MappingUtils.reverse(mapping));
			} catch (GenerateException e) {
				log.error("API-find list error", e);
				return new ArrayList<>(0);
			}
		} else {
			return null;
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Page<Domain> findPage(FilterRequest filter, FilterPageable filterPageable) {
		Map<String, String> mapping = mapping();
		if (mapping == null) {
			return null;
		}
		String findPageUrl = template().getSourceUrl() + FIND_PAGE;
		Map<String, Object> paramMap = new HashMap<>();
		filter.mapping(mapping);
		filterPageable.mapping(mapping);
		paramMap.put(FILTER, filter);
		paramMap.put(PAGEABLE, filterPageable.toMap());
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(paramMap);
		ResponseEntity<Map> response = restTemplate.postForEntity(findPageUrl, httpEntity, Map.class);
		if (response != null && response.getStatusCode() == HttpStatus.OK) {
			Map<String, Object> resp = response.getBody();
			List<Map> list = (List<Map>) resp.get(CONTENT);
			Object totalObj = resp.get(TOTAL);
			long total = 0L;
			if (totalObj != null) {
				total = Long.parseLong(totalObj.toString());
			}
			Pageable pageable = filterPageable.getPageable();
			try {
				return new PageImpl(conversions(list, MappingUtils.reverse(mapping)), pageable, total);
			} catch (GenerateException e) {
				log.error("API-find page error", e);
				return new PageImpl(new ArrayList<>(0), pageable, 0);
			}
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Optional<Domain> findOne(FilterRequest filter, FilterSort sort) {
		Map<String, String> mapping = mapping();
		if (mapping == null) {
			return null;
		}
		String findUrl = template().getSourceUrl() + FIND_ONE;
		Map<String, Object> paramMap = new HashMap<>();
		filter.mapping(mapping);
		sort.mapping(mapping);
		paramMap.put(FILTER, filter);
		paramMap.put(SORT, sort.toString());
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(paramMap);
		ResponseEntity<Map> response = restTemplate.postForEntity(findUrl, httpEntity, Map.class);
		if (response != null && response.getStatusCode() == HttpStatus.OK) {
			Map<String, Object> map = response.getBody();
			try {
				return conversion(map, MappingUtils.reverse(mapping));
			} catch (GenerateException e) {
				log.error("API-find one error", e);
				return Optional.empty();
			}
		} else {
			return null;
		}
	}

	@Override
	public Long delete(FilterRequest filter) {
		Map<String, String> mapping = mapping();
		if (mapping == null) {
			return null;
		}
		String removeUrl = template().getSourceUrl() + DELETE;
		Map<String, Object> paramMap = new HashMap<>();
		filter.mapping(mapping);
		paramMap.put(FILTER, filter);
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(paramMap);
		ResponseEntity<Long> response = restTemplate.postForEntity(removeUrl, httpEntity, Long.class);
		if (response != null && response.getStatusCode() == HttpStatus.OK) {
			return response.getBody();
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Domain saveOrUpdate(Domain entity) {
		if (entity != null) {
			Map<String, String> mapping = mapping();
			if (mapping == null) {
				return null;
			}
			String saveOrUpdateUrl = template().getSourceUrl() + SAVE_OR_UPDATE;
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put(DOMAIN, entity.toMap(mapping));
			HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(paramMap);
			ResponseEntity<Map> response = restTemplate.postForEntity(saveOrUpdateUrl, httpEntity, Map.class);
			if (response != null && response.getStatusCode() == HttpStatus.OK) {
				Map<String, Object> map = response.getBody();
				try {
					return conversion(map, MappingUtils.reverse(mapping)).get();
				} catch (GenerateException e) {
					log.error("API-save or update error", e);
				}
			}
		}
		return null;
	}
	
	@Override
	public void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		if (!CollectionUtils.isEmpty(updateMap)) {
			Map<String, String> mapping = mapping();
			if (mapping == null) {
				return;
			}
			String batchUpdateUrl = template().getSourceUrl() + BATCH_UPDATE;
			Map<String, Object> paramMap = new HashMap<>();
			filter.mapping(mapping);
			paramMap.put(FILTER, filter);
			paramMap.put(UPDATE, MappingUtils.mapping(updateMap, mapping));
			HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(paramMap);
			restTemplate.postForEntity(batchUpdateUrl, httpEntity, void.class);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Domain deleteEntity(Long id) {
		if (id != null) {
			Map<String, String> mapping = mapping();
			if (mapping == null) {
				return null;
			}
			String deleteByIdUrl = template().getSourceUrl() + DELETE_BY_ID + "/" + id;
			ResponseEntity<Map> response = restTemplate.postForEntity(deleteByIdUrl, null, Map.class);
			if (response != null && response.getStatusCode() == HttpStatus.OK) {
				Map<String, Object> map = response.getBody();
				try {
					return conversion(map, MappingUtils.reverse(mapping)).get();
				} catch (GenerateException e) {
					log.error("API-delete error", e);
				}
			}
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Optional<Domain> findById(Long id) {
		if (id != null) {
			Map<String, String> mapping = mapping();
			if (mapping == null) {
				return null;
			}
			String findByIdUrl = template().getSourceUrl() + FIND_BY_ID + "/" + id;
			Map<String, Object> paramMap = new HashMap<>();
			HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(paramMap);
			ResponseEntity<Map> response = restTemplate.postForEntity(findByIdUrl, httpEntity, Map.class);
			if (response != null && response.getStatusCode() == HttpStatus.OK) {
				Map<String, Object> map = response.getBody();
				try {
					return conversion(map, MappingUtils.reverse(mapping));
				} catch (GenerateException e) {
					return Optional.empty();
				}
			}
		}
		return null;
	}
	
	private Map<String, String> mapping() {
		// TODO
		
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Domain> conversions(List<Map> list, Map<String, String> mapping) throws GenerateException {
		List<Domain> domains = new ArrayList<>();
		for (Map<String, Object> map : list) {
			Optional<Domain> optional = conversion(map, mapping);
			if (optional.isPresent()) {
				domains.add(optional.get());
			}
		}
		return domains;
	}
	
}
