package net.saisimon.agtms.core.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;

import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.enums.DataSources;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.core.util.TokenUtils;

public interface GenerateService {
	
	AbstractGenerateRepository getRepository();
	
	DataSources key();
	
	Boolean saveOrUpdate(Domain domain);
	
	default Domain findById(Long id, Long userId) {
		if (id == null) {
			return null;
		}
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		Optional<Domain> optional = repository.findById(id);
		if (optional.isPresent()) {
			Domain domain = optional.get();
			Object obj = domain.getField(AbstractGenerateRepository.CREATOR);
			String creator = obj == null ? "" : obj.toString();
			if (userId.toString().equals(creator)) {
				return domain;
			}
		}
		return null;
	}
	
	default Boolean checkExist(Domain domain) {
		Assert.notNull(domain, "domain can not be null");
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		Template template = template();
		Set<String> uniques = TemplateUtils.getUniques(template);
		if (CollectionUtils.isNotEmpty(uniques)) {
			for (String fieldName : uniques) {
				FilterRequest filter = FilterRequest.build().and(AbstractGenerateRepository.CREATOR, template.getUserId()).and(fieldName, domain.getField(fieldName));
				boolean exist = repository.exists(filter);
				if (exist) {
					return true;
				}
			}
		}
		return false;
	}
	
	default Boolean saveDomain(Domain domain) {
		Assert.notNull(domain, "domain can not be null");
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		fillCommonFields(domain, null);
		return saveOrUpdate(domain);
	}
	
	default Boolean updateDomain(Domain domain, Domain oldDomain) {
		Assert.notNull(domain, "domain can not be null");
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		fillCommonFields(domain, oldDomain);
		return saveOrUpdate(domain);
	}
	
	default void updateDomain(Long id, Map<String, Object> updateMap) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		FilterRequest filter = FilterRequest.build().and("id", id);
		repository.batchUpdate(filter, updateMap);
	}
	
	default Domain newGenerate() throws GenerateException {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.newGenerate();
	}
	
	default Long count(FilterRequest filter) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.count(filter);
	}
	
	default Page<Domain> findPage(FilterRequest filter, FilterPageable pageable) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.findPage(filter, pageable);
	}
	
	default List<Domain> findList(FilterRequest filter, FilterSort sort) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.findList(filter, sort);
	}
	
	default Domain delete(Long id) {
		if (id == null) {
			return null;
		}
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.deleteEntity(id);
	}
	
	default Template template() {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.template();
	}
	
	default void init(Template template) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		repository.init(template);
	}
	
	default void fillCommonFields(Domain newDomain, Domain oldDomain) {
		Long time = System.currentTimeMillis();
		if (oldDomain == null) {
			newDomain.setField(AbstractGenerateRepository.CTIME, time, Long.class);
			UserInfo userInfo = TokenUtils.getUserInfo();
			if (userInfo != null) {
				newDomain.setField(AbstractGenerateRepository.CREATOR, userInfo.getUserId(), Long.class);
			}
		} else {
			newDomain.setField("id", oldDomain.getField("id"), Long.class);
			newDomain.setField(AbstractGenerateRepository.CTIME, oldDomain.getField(AbstractGenerateRepository.CTIME), Long.class);
			newDomain.setField(AbstractGenerateRepository.CREATOR, oldDomain.getField(AbstractGenerateRepository.CREATOR), Long.class);
		}
		newDomain.setField(AbstractGenerateRepository.UTIME, time, Long.class);
	}
	
}
