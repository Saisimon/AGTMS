package net.saisimon.agtms.rpc.service;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.rpc.repository.GenerateRpcRepository;

@Slf4j
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
	
	@Override
	public Boolean saveOrUpdate(Domain domain) {
		Assert.notNull(domain, "domain can not be null");
		return generateRpcRepository.saveOrUpdate(domain) != null;
	}
	
	@Override
	public Domain findById(Long id, Long operatorId) {
		if (id == null) {
			return null;
		}
		return generateRpcRepository.findById(id).orElse(null);
	}

	@Override
	public Boolean checkExist(Domain domain) {
		Assert.notNull(domain, "domain can not be null");
		Set<String> uniques = TemplateUtils.getUniques(template());
		if (CollectionUtils.isNotEmpty(uniques)) {
			for (String fieldName : uniques) {
				FilterRequest filter = FilterRequest.build().and(fieldName, domain.getField(fieldName));
				Boolean exist = generateRpcRepository.exists(filter);
				if (exist == null || exist) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Boolean saveDomain(Domain domain) {
		try {
			Domain d = generateRpcRepository.saveOrUpdate(domain);
			if (d != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error("save domain error", e);
			return false;
		}
	}

	@Override
	public Boolean updateDomain(Domain domain, Domain oldDomain) {
		try {
			Object id = oldDomain.getField(Constant.ID);
			if (id != null) {
				domain.setField(Constant.ID, id, id.getClass());
				Domain d = generateRpcRepository.saveOrUpdate(domain);
				if (d != null) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			log.error("save domain error", e);
		}
		return false;
	}
	
	@Override
	public void updateDomain(Long id, Map<String, Object> updateMap) {
		try {
			generateRpcRepository.batchUpdate(FilterRequest.build().and(Constant.ID, id), updateMap);
		} catch (Exception e) {
			log.error("update domain error", e);
		}
	}
	
	@Override
	public Domain delete(Long id) {
		if (id == null) {
			return null;
		}
		return generateRpcRepository.deleteEntity(id);
	}

}
