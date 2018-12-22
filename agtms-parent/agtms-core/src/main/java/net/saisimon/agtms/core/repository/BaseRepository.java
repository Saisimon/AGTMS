package net.saisimon.agtms.core.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;

public interface BaseRepository<T, ID> {
	
	Long count(FilterRequest filter);
	
	Boolean exists(FilterRequest filter);
	
	default List<T> findList(FilterRequest filter) {
		return findList(filter, null);
	}
	
	List<T> findList(FilterRequest filter, FilterSort sort);
	
	Page<T> findPage(FilterRequest filter, FilterPageable pageable);
	
	default Optional<T> findOne(FilterRequest filter) {
		return findOne(filter, null);
	}
	
	Optional<T> findOne(FilterRequest filter, FilterSort sort);
	
	Long delete(FilterRequest filter);
	
	Optional<T> findById(ID id);
	
	T deleteEntity(ID id);
	
	T saveOrUpdate(T entity);
	
	void batchUpdate(FilterRequest filter, Map<String, Object> updateMap);
	
}
