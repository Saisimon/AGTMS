package net.saisimon.agtms.core.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.util.DomainUtils;
import net.saisimon.agtms.core.util.TemplateUtils;

/**
 * 自定义对象服务接口
 * 
 * @author saisimon
 *
 */
public interface GenerateService {
	
	AbstractGenerateRepository getRepository();
	
	Sign sign();
	
	default void init(Template template) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		repository.init(template);
	}
	
	default Template template() {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.template();
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
	
	default Boolean checkExist(Domain domain) {
		Assert.notNull(domain, "domain can not be null");
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		Template template = template();
		Set<String> uniques = TemplateUtils.getUniques(template);
		if (!CollectionUtils.isEmpty(uniques)) {
			FilterRequest filter = FilterRequest.build().and(Constant.OPERATORID, template.getOperatorId());
			FilterRequest uniquesFilter = FilterRequest.build();
			for (String fieldName : uniques) {
				Object fieldValue = domain.getField(fieldName);
				if (fieldValue != null) {
					uniquesFilter.or(fieldName, fieldValue);
				}
			}
			filter.and(uniquesFilter);
			return repository.exists(filter);
		}
		return false;
	}
	
	default List<Domain> findList(FilterRequest filter, FilterSort sort) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.findList(filter, sort);
	}
	
	default Page<Domain> findPage(FilterRequest filter, FilterPageable pageable) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.findPage(filter, pageable);
	}
	
	default void delete(Domain entity) {
		if (entity == null) {
			return;
		}
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		repository.delete(entity);
	}
	
	default Domain findById(Long id, Long operatorId) {
		if (id == null || operatorId == null) {
			return null;
		}
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		Optional<Domain> optional = repository.findById(id);
		if (optional.isPresent()) {
			Domain domain = optional.get();
			Object obj = domain.getField(Constant.OPERATORID);
			if (obj == null) {
				return domain;
			}
			if (operatorId.toString().equals(obj.toString())) {
				return domain;
			}
		}
		return null;
	}
	
	default Domain saveDomain(Domain domain) {
		Assert.notNull(domain, "domain can not be null");
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		DomainUtils.fillCommonFields(domain, null);
		return repository.saveOrUpdate(domain);
	}
	
	default Domain updateDomain(Domain domain, Domain oldDomain) {
		Assert.notNull(domain, "domain can not be null");
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		DomainUtils.fillCommonFields(domain, oldDomain);
		return repository.saveOrUpdate(domain);
	}
	
	default void updateDomain(Long id, Map<String, Object> updateMap) {
		if (id == null || CollectionUtils.isEmpty(updateMap)) {
			return;
		}
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		repository.update(id, updateMap);
	}
	
}
