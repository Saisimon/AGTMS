package net.saisimon.agtms.mongodb.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.mongodb.util.MongodbFilterUtils;

@Repository
@Slf4j
public class GenerateMongoRepository extends AbstractGenerateRepository {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<Domain> findList(FilterRequest filter, FilterSort sort) {
		Query query = null;
		if (filter != null) {
			query = MongodbFilterUtils.query(filter);
		} else {
			query = new Query();
		}
		if (sort != null) {
			query.with(sort.getSort());
		}
		try {
			return conversions(mongoTemplate.find(query, Map.class, collectionName()));
		} catch (GenerateException e) {
			log.error("find list error", e);
			return new ArrayList<>(0);
		}
	}
	
	@Override
	public Page<Domain> findPage(FilterRequest filter, FilterPageable filterPageable) {
		Query query = null;
		if (filter != null) {
			query = MongodbFilterUtils.query(filter);
		} else {
			query = new Query();
		}
		long total = mongoTemplate.count(query, collectionName());
		if (filterPageable == null) {
			filterPageable = FilterPageable.build(null);
		}
		Pageable pageable = filterPageable.getPageable();
		query.with(pageable);
		try {
			List<Domain> domains = conversions(mongoTemplate.find(query, Map.class, collectionName()));
			return new PageImpl<>(domains, pageable, total);
		} catch (GenerateException e) {
			log.error("find page error", e);
			return new PageImpl<>(new ArrayList<>(0), pageable, 0L);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Optional<Domain> findOne(FilterRequest filter, FilterSort sort) {
		Query query = null;
		if (filter != null) {
			query = MongodbFilterUtils.query(filter);
		} else {
			query = new Query();
		}
		if (sort != null) {
			query.with(sort.getSort());
		}
		try {
			return conversion(mongoTemplate.findOne(query, Map.class, collectionName()));
		} catch (GenerateException e) {
			log.error("find one error", e);
			return Optional.empty();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Optional<Domain> findById(Long id) {
		try {
			return conversion(mongoTemplate.findById(id, Map.class, collectionName()));
		} catch (GenerateException e) {
			log.error("find by id error", e);
			return Optional.empty();
		}
	}
	
	@Override
	public Domain deleteEntity(Long id) {
		Optional<Domain> optional = findById(id);
		Domain domain = null;
		if (optional.isPresent()) {
			domain = optional.get();
			mongoTemplate.remove(domain, collectionName());
		}
		return domain;
	}
	
	@Override
	public Domain saveOrUpdate(Domain entity) {
		mongoTemplate.save(entity, collectionName());
		return entity;
	}
	
	@Override
	public void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		if (!CollectionUtils.isEmpty(updateMap)) {
			Update update = new Update();
			for (String key : updateMap.keySet()) {
				update.set(key, updateMap.get(key));
			}
			Query query = null;
			if (filter != null) {
				query = MongodbFilterUtils.query(filter);
			} else {
				query = new Query();
			}
			mongoTemplate.updateMulti(query, update, collectionName());
		}
	}
	
	@Override
	public Long count(FilterRequest filter) {
		Query query = null;
		if (filter != null) {
			query = MongodbFilterUtils.query(filter);
		} else {
			query = new Query();
		}
		return mongoTemplate.count(query, collectionName());
	}

	@Override
	public Boolean exists(FilterRequest filter) {
		Query query = null;
		if (filter != null) {
			query = MongodbFilterUtils.query(filter);
		} else {
			query = new Query();
		}
		return mongoTemplate.exists(query, collectionName());
	}

	@Override
	public Long delete(FilterRequest filter) {
		Query query = null;
		if (filter != null) {
			query = MongodbFilterUtils.query(filter);
		} else {
			query = new Query();
		}
		return mongoTemplate.remove(query, collectionName()).getDeletedCount();
	}
	
	private String collectionName() {
		return TemplateUtils.getTableName(template());
	}
	
}
