package net.saisimon.agtms.core.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.util.Assert;

import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.repository.BaseRepository;

public interface BaseService<T, ID> {
	
	BaseRepository<T, ID> getRepository();
	
	default Long count(FilterRequest filter) {
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.count(filter);
	}
	
	default Boolean exists(FilterRequest filter) {
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.exists(filter);
	}
	
	default List<T> findList(FilterRequest filter) {
		return findList(filter, null);
	}
	
	default List<T> findList(FilterRequest filter, FilterSort sort) {
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.findList(filter, sort);
	}
	
	default Page<T> findPage(FilterRequest filter, FilterPageable pageable) {
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.findPage(filter, pageable);
	}
	
	default Optional<T> findOne(FilterRequest filter) {
		return findOne(filter, null);
	}
	
	default Optional<T> findOne(FilterRequest filter, FilterSort sort) {
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.findOne(filter, sort);
	}
	
	default Long delete(FilterRequest filter) {
		Assert.notNull(filter, "filter can not be null");
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.delete(filter);
	}
	
	default Optional<T> findById(ID id) {
		Assert.notNull(id, "id can not be null");
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.findById(id);
	}
	
	default T delete(ID id) {
		Assert.notNull(id, "id can not be null");
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.deleteEntity(id);
	}
	
	default T saveOrUpdate(T entity) {
		Assert.notNull(entity, "entity can not be null");
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.saveOrUpdate(entity);
	}
	
	default void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		Assert.notNull(updateMap, "update map can not be null");
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		repository.batchUpdate(filter, updateMap);
	}
	
}
