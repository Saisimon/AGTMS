package net.saisimon.agtms.jpa.repository.base;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.CollectionUtils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.jpa.util.JpaFilterUtils;

@Slf4j
public class SimpleBaseJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> 
	implements JpaRepository<T, ID>, BaseJpaRepository<T, ID> {
	
	private JpaEntityInformation<T, ?> entityInformation;
	private EntityManager entityManager;
	
	public SimpleBaseJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityInformation = entityInformation;
		this.entityManager = entityManager;
	}

	@Override
	public Long count(final FilterRequest filter) {
		return count(JpaFilterUtils.specification(filter));
	}

	@Override
	public List<T> findList(final FilterRequest filter, final FilterSort sort, String... properties) {
		Sort s = getSort(sort);
		if (ArrayUtil.isEmpty(properties)) {
			return findAll(JpaFilterUtils.specification(filter, properties), s);
		} else {
			TypedQuery<Tuple> query = getTupleQuery(filter, getDomainClass(), s, properties);
			List<Tuple> tuples = query.getResultList();
			List<T> domains = new ArrayList<>(tuples.size());
			for (Tuple tuple : tuples) {
				domains.add(buildDomain(getDomainClass(), tuple, properties));
			}
			return domains;
		}
	}
	
	@SuppressWarnings("unchecked")
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
		Pageable springPageable = filterPageable.getPageable();
		Class<T> domainClass = getDomainClass();
		// 如果是复合ID，直接查出指定属性，否则先查出ID集合再根据ID查指定属性
		if (entityInformation.hasCompositeId()) {
			if (ArrayUtil.isEmpty(properties)) {
				TypedQuery<T> query = getQuery(JpaFilterUtils.specification(filterRequest, properties), springPageable);
				if (springPageable.isPaged()) {
					query.setFirstResult((int) springPageable.getOffset());
					query.setMaxResults(springPageable.getPageSize());
				}
				return query.getResultList();
			} else {
				TypedQuery<Tuple> query = getTupleQuery(filterRequest, domainClass, springPageable, properties);
				List<Tuple> tuples = query.getResultList();
				List<T> domains = new ArrayList<>(tuples.size());
				for (Tuple tuple : tuples) {
					domains.add(buildDomain(domainClass, tuple, properties));
				}
				return domains;
			}
		} else {
			List<String> idNames = new ArrayList<>();
			Iterable<String> it = entityInformation.getIdAttributeNames();
			for (String idName : it) {
				idNames.add(idName);
			}
			TypedQuery<Tuple> query = getTupleQuery(filterRequest, domainClass, springPageable, idNames.toArray(new String[idNames.size()]));
			List<Tuple> tuples = query.getResultList();
			if (CollectionUtils.isEmpty(tuples)) {
				return new ArrayList<>(0);
			}
			List<ID> ids = new ArrayList<>(tuples.size());
			for (Tuple tuple : tuples) {
				ids.add((ID) tuple.get(0));
			}
			if (ArrayUtil.isNotEmpty(properties)) {
				Set<String> propertySet = new HashSet<>(idNames);
				for (String property : properties) {
					if (property != null) {
						propertySet.add(property);
					}
				}
				properties = propertySet.toArray(new String[propertySet.size()]);
			}
			String idName = idNames.get(0);
			List<T> domains = findList(FilterRequest.build().and(idName, ids, Constant.Operator.IN), properties);
			return JpaFilterUtils.sortDomains(domains, ids, idName);
		}
	}
	
	@Override
	public Page<T> findPage(final FilterRequest filter, final FilterPageable pageable, boolean count, String... properties) {
		FilterPageable filterPageable = pageable;
		if (filterPageable == null) {
			filterPageable = FilterPageable.build(null);
		}
		Pageable springPageable = filterPageable.getPageable();
		long total = 0;
		if (count) {
			total = count(filter);
			if (total == 0) {
				return new PageImpl<>(Collections.emptyList(), springPageable, 0);
			}
		}
		List<T> domains = findList(filter, filterPageable, properties);
		if (CollectionUtils.isEmpty(domains)) {
			return new PageImpl<>(new ArrayList<>(0), springPageable, 0);
		}
		return new PageImpl<>(domains, springPageable, count ? total : domains.size());
	}
	
	@Override
	public Optional<T> findOne(final FilterRequest filter, final FilterSort sort, String... properties) {
		Sort s = getSort(sort);
		try {
			if (ArrayUtil.isEmpty(properties)) {
				return Optional.of(getQuery(JpaFilterUtils.specification(filter, properties), s).setMaxResults(1).getSingleResult());
			} else {
				Class<T> domainClass = getDomainClass();
				TypedQuery<Tuple> query = getTupleQuery(filter, domainClass, s, properties).setMaxResults(1);
				return Optional.of(buildDomain(domainClass, query.getSingleResult(), properties));
			}
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}
	
	@Override
	public Optional<T> find(ID id) {
		return findById(id);
	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public Long delete(final FilterRequest filter) {
		List<T> list = findList(filter);
		if (CollectionUtils.isEmpty(list)) {
			return 0L;
		} else {
			for (T entity : list) {
				delete(entity);
			}
			return new Long(list.size());
		}
	}
	
	@Transactional(rollbackOn = Exception.class)
	@Override
	public T saveOrUpdate(T entity) {
		if (entity == null) {
			return entity;
		}
		return saveAndFlush(entity);
	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public void batchUpdate(final FilterRequest filter, Map<String, Object> updateMap) {
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
	
	@SuppressWarnings("unchecked")
	private T buildDomain(Class<?> domainClass, Tuple tuple, String... properties) {
		T domain = (T) ReflectUtil.newInstance(domainClass);
		for (int i = 0; i < properties.length; i++) {
			String propertyName = properties[i];
			Object propertyValue = tuple.get(i);
			ReflectUtil.setFieldValue(domain, propertyName, propertyValue);
		}
		return domain;
	}
	
	private <S> TypedQuery<Tuple> getTupleQuery(FilterRequest filter, Class<S> domainClass, Pageable pageable, String... properties) {
		TypedQuery<Tuple> query = getTupleQuery(filter, domainClass, pageable.getSort(), properties);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());
		return query;
	}
	
	private <S> TypedQuery<Tuple> getTupleQuery(FilterRequest filter, Class<S> domainClass, Sort sort, String... properties) {
		Specification<S> spec = JpaFilterUtils.specification(filter, properties);
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = builder.createTupleQuery();
		Root<S> root = query.from(domainClass);
		if (spec != null) {
			Predicate predicate = spec.toPredicate(root, query, builder);
			if (predicate != null) {
				query.where(predicate);
			}
		}
		if (sort.isSorted()) {
			query.orderBy(QueryUtils.toOrders(sort, root, builder));
		}
		return entityManager.createQuery(query);
	}
	
	private Sort getSort(FilterSort sort) {
		if (sort != null && sort.getSort() != null) {
			return sort.getSort();
		} else {
			return Sort.unsorted();
		}
	}
	
}
