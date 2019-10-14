package net.saisimon.agtms.core.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.util.DomainUtils;
import net.saisimon.agtms.core.util.SystemUtils;
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
	
	default void init(final Template template) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		repository.init(template);
	}
	
	default void remove() {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		repository.remove();
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
	
	default Long count(final FilterRequest filter) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.count(filter);
	}
	
	default Boolean checkExist(Domain domain, Collection<Long> operatorIds) {
		Assert.notNull(domain, "domain can not be null");
		Assert.notNull(operatorIds, "operatorId can not be null");
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		Template template = template();
		Set<String> uniques = TemplateUtils.getUniques(template);
		if (!CollectionUtils.isEmpty(uniques)) {
			FilterRequest filter = FilterRequest.build().and(Constant.OPERATORID, operatorIds, Constant.Operator.IN);
			Object id = domain.getField(Constant.ID);
			if (id != null) {
				filter.and(Constant.ID, id, Operator.NE);
			}
			FilterRequest uniquesFilter = FilterRequest.build();
			boolean check = false;
			for (String fieldName : uniques) {
				Object fieldValue = domain.getField(fieldName);
				if (fieldValue != null) {
					uniquesFilter.or(fieldName, fieldValue);
					check = true;
				}
			}
			if (!check) {
				return false;
			}
			filter.and(uniquesFilter);
			return repository.exists(filter);
		}
		return false;
	}
	
	default Optional<Domain> findOne(final FilterRequest filter, final FilterSort sort, String... properties) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.findOne(filter, sort, properties);
	}
	
	default List<Domain> findList(final FilterRequest filter, final FilterSort sort, String... properties) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.findList(filter, sort, properties);
	}
	
	default List<Domain> findList(final FilterRequest filter, final FilterPageable pageable, String... properties) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.findList(filter, pageable, properties);
	}
	
	default Page<Domain> findPage(final FilterRequest filter, final FilterPageable pageable, boolean count, String... properties) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		return repository.findPage(filter, pageable, count, properties);
	}
	
	default void delete(Domain entity) {
		if (entity == null) {
			return;
		}
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		repository.delete(entity);
	}
	
	default Domain findById(Long id, Collection<Long> operatorIds) {
		if (id == null || CollectionUtils.isEmpty(operatorIds)) {
			return null;
		}
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		Optional<Domain> optional = repository.find(id);
		if (optional.isPresent()) {
			Domain domain = optional.get();
			Object obj = domain.getField(Constant.OPERATORID);
			if (obj instanceof Long && operatorIds.contains((Long) obj)) {
				return domain;
			}
		}
		return null;
	}
	
	default Domain saveDomain(Domain domain, Long operatorId) {
		Assert.notNull(domain, "domain can not be null");
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		DomainUtils.fillCommonFields(domain, null, operatorId);
		return repository.saveOrUpdate(domain);
	}
	
	default Domain updateDomain(Domain domain, Domain oldDomain, Long operatorId) {
		Assert.notNull(domain, "domain can not be null");
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		DomainUtils.fillCommonFields(domain, oldDomain, operatorId);
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
	
	default void delete(final FilterRequest filter) {
		AbstractGenerateRepository repository = getRepository();
		Assert.notNull(repository, "repository can not be null");
		repository.delete(filter);
	}
	
	default void createIndexes(String tableName, Map<String, TemplateField> fieldInfoMap) {
		if (StringUtils.isEmpty(tableName) || CollectionUtils.isEmpty(fieldInfoMap)) {
			return;
		}
		for (Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldName = entry.getKey();
			TemplateField field = entry.getValue();
			if (!field.getFilter() && !field.getSorted() && !field.getUniqued()) {
				continue;
			}
			boolean unique = field.getUniqued();
			CompletableFuture.runAsync(() -> {
				createIndex(tableName, fieldName, unique);
			}, SystemUtils.EXECUTOR);
		}
	}
	
	default void alterIndexes(String tableName, Map<String, TemplateField> fieldInfoMap, Map<String, TemplateField> oldFieldInfoMap) {
		if (StringUtils.isEmpty(tableName)) {
			return;
		}
		Set<String> commonFieldName = new HashSet<>(fieldInfoMap.keySet());
		commonFieldName.retainAll(oldFieldInfoMap.keySet());
		for (Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldName = entry.getKey();
			TemplateField field = entry.getValue();
			if (commonFieldName.contains(fieldName)) {
				TemplateField oldField = oldFieldInfoMap.get(fieldName);
				boolean oldHasIndex = oldField.getFilter() || oldField.getSorted() || oldField.getUniqued();
				boolean hasIndex = field.getFilter() || field.getSorted() || field.getUniqued();
				if (oldHasIndex && !hasIndex) {
					CompletableFuture.runAsync(() -> {
						dropIndex(tableName, fieldName);
					}, SystemUtils.EXECUTOR);
				} else if (!oldHasIndex && hasIndex) {
					boolean unique = field.getUniqued();
					CompletableFuture.runAsync(() -> {
						createIndex(tableName, fieldName, unique);
					}, SystemUtils.EXECUTOR);
				}
			} else {
				if (!field.getFilter() && !field.getSorted() && !field.getUniqued()) {
					continue;
				}
				boolean unique = field.getUniqued();
				CompletableFuture.runAsync(() -> {
					createIndex(tableName, fieldName, unique);
				}, SystemUtils.EXECUTOR);
			}
		}
	}
	
	boolean createTable();
	
	boolean alterTable(Template oldTemplate);
	
	boolean dropTable();
	
	boolean createIndex(String tableName, String columnName, boolean unique);
	
	boolean dropIndex(String tableName, String columnName);
	
}
