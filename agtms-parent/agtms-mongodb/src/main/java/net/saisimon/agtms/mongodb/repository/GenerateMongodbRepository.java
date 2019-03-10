package net.saisimon.agtms.mongodb.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.mongodb.util.MongodbFilterUtils;

@Repository
@Slf4j
public class GenerateMongodbRepository extends AbstractGenerateRepository {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<Domain> findList(FilterRequest filter, FilterSort sort, String... properties) {
		Query query = null;
		if (filter != null) {
			query = MongodbFilterUtils.query(filter);
		} else {
			query = new Query();
		}
		if (sort != null) {
			query.with(sort.getSort());
		}
		Template template = template();
		Document fields = getFieldMap(template, properties);
		BasicQuery basicQuery = new BasicQuery(query.getQueryObject(), fields);
		basicQuery.setSortObject(query.getSortObject());
		try {
			return conversions(mongoTemplate.find(basicQuery, Map.class, collectionName(template)));
		} catch (GenerateException e) {
			log.error("find list error", e);
			return new ArrayList<>(0);
		}
	}
	
	@Override
	public List<Domain> findList(FilterRequest filter, FilterPageable pageable, String... properties) {
		Query query = null;
		if (filter != null) {
			query = MongodbFilterUtils.query(filter);
		} else {
			query = new Query();
		}
		if (pageable != null) {
			Pageable springPageable = pageable.getPageable();
			query.with(springPageable);
		}
		Template template = template();
		Document fields = getFieldMap(template, properties);
		BasicQuery basicQuery = new BasicQuery(query.getQueryObject(), fields);
		basicQuery.setSortObject(query.getSortObject());
		try {
			return conversions(mongoTemplate.find(basicQuery, Map.class, collectionName(template)));
		} catch (GenerateException e) {
			log.error("find list error", e);
			return new ArrayList<>(0);
		}
	}
	
	@Override
	public Page<Domain> findPage(FilterRequest filter, FilterPageable pageable, String... properties) {
		Query query = null;
		if (filter != null) {
			query = MongodbFilterUtils.query(filter);
		} else {
			query = new Query();
		}
		Template template = template();
		String collectionName = collectionName(template);
		long total = mongoTemplate.count(query, collectionName);
		if (pageable == null) {
			pageable = FilterPageable.build(null);
		}
		Pageable springPageable = pageable.getPageable();
		query.with(springPageable);
		Document fields = getFieldMap(template, properties);
		BasicQuery basicQuery = new BasicQuery(query.getQueryObject(), fields);
		basicQuery.setSortObject(query.getSortObject());
		try {
			List<Domain> domains = conversions(mongoTemplate.find(basicQuery, Map.class, collectionName));
			return new PageImpl<>(domains, springPageable, total);
		} catch (GenerateException e) {
			log.error("find page error", e);
			return new PageImpl<>(new ArrayList<>(0), springPageable, 0L);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Optional<Domain> findOne(FilterRequest filter, FilterSort sort, String... properties) {
		Query query = null;
		if (filter != null) {
			query = MongodbFilterUtils.query(filter);
		} else {
			query = new Query();
		}
		if (sort != null) {
			query.with(sort.getSort());
		}
		Template template = template();
		Document fields = getFieldMap(template, properties);
		BasicQuery basicQuery = new BasicQuery(query.getQueryObject(), fields);
		basicQuery.setSortObject(query.getSortObject());
		try {
			return Optional.ofNullable(conversion(mongoTemplate.findOne(basicQuery, Map.class, collectionName(template))));
		} catch (GenerateException e) {
			log.error("find one error", e);
			return Optional.empty();
		}
	}
	
	@Override
	public Domain saveOrUpdate(Domain entity) {
		mongoTemplate.save(entity, collectionName(template()));
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
			mongoTemplate.updateMulti(query, update, collectionName(template()));
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
		return mongoTemplate.count(query, collectionName(template()));
	}

	@Override
	public Long delete(FilterRequest filter) {
		Query query = null;
		if (filter != null) {
			query = MongodbFilterUtils.query(filter);
		} else {
			query = new Query();
		}
		return mongoTemplate.remove(query, collectionName(template())).getDeletedCount();
	}
	
	@Override
	public void delete(Domain entity) {
		mongoTemplate.remove(entity, collectionName(template()));
	}
	
	private String collectionName(Template template) {
		return TemplateUtils.getTableName(template);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Domain> conversions(List<Map> list) throws GenerateException {
		List<Domain> domains = new ArrayList<>();
		for (Map<String, Object> map : list) {
			Domain domain = conversion(map);
			if (domain != null) {
				domains.add(domain);
			}
		}
		return domains;
	}
	
	private Document getFieldMap(Template template, String... properties) {
		Document fieldMap = new Document();
		if (properties == null || properties.length == 0) {
			List<String> columnNames = TemplateUtils.getTableColumnNames(template);
			for (String columnName : columnNames) {
				fieldMap.put(columnName, true);
			}
		} else {
			for (String property : properties) {
				fieldMap.put(property, true);
			}
		}
		fieldMap.put(Constant.MONGODBID, true);
		fieldMap.put(Constant.OPERATORID, true);
		fieldMap.put(Constant.CREATETIME, true);
		fieldMap.put(Constant.UPDATETIME, true);
		return fieldMap;
	}

}
