package net.saisimon.agtms.mongodb.repository.base;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.mongodb.util.MongodbFilterUtils;

public class SimpleBaseMongodbRepository<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> 
	implements MongoRepository<T, ID>, BaseMongodbRepository<T, ID> {
	
	private MongoOperations mongoOperations;
	private String collectionName;
	private Class<T> entityClass;

	public SimpleBaseMongodbRepository(MongoEntityInformation<T, ID> entityInformation,
			MongoOperations mongoOperations) {
		super(entityInformation, mongoOperations);
		this.mongoOperations = mongoOperations;
		this.collectionName = entityInformation.getCollectionName();
		this.entityClass = entityInformation.getJavaType();
	}
	
	@Override
	public String getCollectionName() {
		return collectionName;
	}

	@Override
	public Long count(final FilterRequest filter) {
		return mongoOperations.count(MongodbFilterUtils.query(filter), entityClass, collectionName);
	}

	@Override
	public List<T> findList(final FilterRequest filter, final FilterSort sort, String... properties) {
		Query query = MongodbFilterUtils.query(filter);
		if (sort != null) {
			query.with(sort.getSort());
		}
		if (properties != null && properties.length > 0) {
			BasicQuery basicQuery = new BasicQuery(query.getQueryObject(), getFieldMap(properties));
			basicQuery.setSortObject(query.getSortObject());
			query = basicQuery;
		}
		return mongoOperations.find(query, entityClass, collectionName);
	}
	
	@Override
	public List<T> findList(final FilterRequest filter, final FilterPageable pageable, String... properties) {
		FilterPageable filterPageable = pageable;
		if (filterPageable == null) {
			filterPageable = FilterPageable.build(null);
		}
		FilterRequest filterRequest = filter;
		if (filterRequest == null) {
			filterRequest = FilterRequest.build();
		}
		filterRequest.and(filterPageable.getParam());
		Query query = MongodbFilterUtils.query(filterRequest);
		query.with(filterPageable.getPageable());
		if (properties != null && properties.length > 0) {
			BasicQuery basicQuery = new BasicQuery(query.getQueryObject(), getFieldMap(properties));
			basicQuery.setSortObject(query.getSortObject());
			query = basicQuery;
		}
		return mongoOperations.find(query, entityClass, collectionName);
	}

	@Override
	public Page<T> findPage(final FilterRequest filter, final FilterPageable pageable, boolean count, String... properties) {
		Query query = MongodbFilterUtils.query(filter);
		FilterPageable filterPageable = pageable;
		if (filterPageable == null) {
			filterPageable = FilterPageable.build(null);
		}
		Pageable springPageable = filterPageable.getPageable();
		long total = 0;
		if (count) {
			total = mongoOperations.count(query, entityClass, collectionName);
			if (total == 0) {
				return new PageImpl<>(Collections.emptyList(), springPageable, total);
			}
		}
		query.with(springPageable);
		if (properties != null && properties.length > 0) {
			BasicQuery basicQuery = new BasicQuery(query.getQueryObject(), getFieldMap(properties));
			basicQuery.setSortObject(query.getSortObject());
			query = basicQuery;
		}
		List<T> domains = mongoOperations.find(query, entityClass, collectionName);
		return new PageImpl<>(domains, springPageable, count ? total : domains.size());
	}

	@Override
	public Optional<T> findOne(final FilterRequest filter, final FilterSort sort, String... properties) {
		Query query = MongodbFilterUtils.query(filter);
		if (sort != null) {
			query.with(sort.getSort());
		}
		if (properties != null && properties.length > 0) {
			BasicQuery basicQuery = new BasicQuery(query.getQueryObject(), getFieldMap(properties));
			basicQuery.setSortObject(query.getSortObject());
			query = basicQuery;
		}
		return Optional.ofNullable(mongoOperations.findOne(query, entityClass, collectionName));
	}
	
	@Override
	public Optional<T> find(ID id) {
		return findById(id);
	}

	@Override
	public Long delete(final FilterRequest filter) {
		return mongoOperations.remove(MongodbFilterUtils.query(filter), entityClass, collectionName).getDeletedCount();
	}
	
	@Override
	public T saveOrUpdate(T entity) {
		if (entity != null) {
			mongoOperations.save(entity, collectionName);
		}
		return entity;
	}
	
	@Override
	public void batchUpdate(final FilterRequest filter, Map<String, Object> updateMap) {
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
			mongoOperations.updateMulti(query, update, entityClass, collectionName);
		}
	}

	@Override
	public Boolean existCollection() {
		return mongoOperations.collectionExists(collectionName);
	}

	@Override
	public void createCollection() {
		mongoOperations.createCollection(collectionName);
	}

	@Override
	public void dropCollection() {
		mongoOperations.dropCollection(collectionName);
	}

	@Override
	public List<IndexInfo> getIndexes() {
		IndexOperations indexOperations = mongoOperations.indexOps(collectionName);
		return indexOperations.getIndexInfo();
	}

	@Override
	public String createIndex(IndexDefinition indexDefinition) {
		IndexOperations indexOperations = mongoOperations.indexOps(collectionName);
		return indexOperations.ensureIndex(indexDefinition);
	}

	@Override
	public void dropIndex(String name) {
		IndexOperations indexOperations = mongoOperations.indexOps(collectionName);
		indexOperations.dropIndex(name);
	}
	
	private Document getFieldMap(String... properties) {
		Document fieldMap = new Document();
		for (String property : properties) {
			fieldMap.put(property, true);
		}
		return fieldMap;
	}

}
