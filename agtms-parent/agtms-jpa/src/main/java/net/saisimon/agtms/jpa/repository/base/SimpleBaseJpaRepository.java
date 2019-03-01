package net.saisimon.agtms.jpa.repository.base;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.jpa.util.JpaFilterUtils;

@Slf4j
public class SimpleBaseJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> 
	implements JpaRepository<T, ID>, BaseJpaRepository<T, ID> {
	
	public SimpleBaseJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
	}

	@Override
	public Long count(final FilterRequest filter) {
		return count(JpaFilterUtils.specification(filter));
	}

	@Override
	public List<T> findList(final FilterRequest filter, FilterSort sort) {
		if (sort != null) {
			return findAll(JpaFilterUtils.specification(filter), sort.getSort());
		} else {
			return findAll(JpaFilterUtils.specification(filter));
		}
	}

	@Override
	public Page<T> findPage(final FilterRequest filter, FilterPageable filterPageable) {
		if (filterPageable == null) {
			filterPageable = FilterPageable.build(null);
		}
		return findAll(JpaFilterUtils.specification(filter), filterPageable.getPageable());
	}

	@Override
	public Optional<T> findOne(final FilterRequest filter, FilterSort sort) {
		Sort s = null;
		if (sort != null) {
			s = sort.getSort();
		} else {
			s = Sort.unsorted();
		}
		try {
			return Optional.of(getQuery(JpaFilterUtils.specification(filter), s).setMaxResults(1).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}
	
	@Override
	public Optional<T> find(ID id) {
		return findById(id);
	}

	@Transactional
	@Override
	public Long delete(final FilterRequest filter) {
		List<T> list = findList(filter);
		if (CollectionUtils.isEmpty(list)) {
			return 0L;
		} else {
			deleteInBatch(list);
			return new Long(list.size());
		}
	}
	
	@Transactional
	@Override
	public T saveOrUpdate(T entity) {
		return saveAndFlush(entity);
	}

	@Transactional
	@Override
	public void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		List<T> list = findList(filter);
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		try {
			for (T entity : list) {
				BeanUtils.populate(entity, updateMap);
			}
			saveAll(list);
			flush();
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("batch update error", e);
		}
	}
	
}
