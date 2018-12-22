package net.saisimon.agtms.mongodb.repository.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
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

public class SimpleBaseMongoRepository<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> 
	implements MongoRepository<T, ID>, BaseMongoRepository<T, ID> {
	
	private MongoOperations mongoOperations;
	private String collectionName;
	private Class<T> entityClass;

	public SimpleBaseMongoRepository(MongoEntityInformation<T, ID> entityInformation,
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
	public Boolean exists(final FilterRequest filter) {
		return mongoOperations.exists(MongodbFilterUtils.query(filter), entityClass, collectionName);
	}

	@Override
	public List<T> findList(final FilterRequest filter, FilterSort sort) {
		Query query = MongodbFilterUtils.query(filter);
		if (sort != null) {
			query.with(sort.getSort());
		}
		return mongoOperations.find(query, entityClass, collectionName);
	}

	@Override
	public Page<T> findPage(final FilterRequest filter, FilterPageable filterPageable) {
		Query query = MongodbFilterUtils.query(filter);
		long total = mongoOperations.count(query, entityClass, collectionName);
		if (total > 0) {
			if (filterPageable == null) {
				filterPageable = FilterPageable.build(null);
			}
			Pageable pageable = filterPageable.getPageable();
			query = query.with(pageable);
			List<T> domains = mongoOperations.find(query, entityClass, collectionName);
			return new PageImpl<>(domains, pageable, total);
		} else {
			return Page.empty(Pageable.unpaged());
		}
	}

	@Override
	public Optional<T> findOne(final FilterRequest filter, FilterSort sort) {
		Query query = MongodbFilterUtils.query(filter);
		if (sort != null) {
			query.with(sort.getSort());
		}
		return Optional.ofNullable(mongoOperations.findOne(query, entityClass, collectionName));
	}

	@Override
	public Long delete(final FilterRequest filter) {
		return mongoOperations.remove(MongodbFilterUtils.query(filter), entityClass, collectionName).getDeletedCount();
	}
	
	@Override
	public T deleteEntity(ID id) {
		Optional<T> optional = findById(id);
		T entity = null;
		if (optional.isPresent()) {
			entity = optional.get();
			mongoOperations.remove(entity, collectionName);
		}
		return entity;
	}

	@Override
	public T saveOrUpdate(T entity) {
		mongoOperations.save(entity, collectionName);
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

}
