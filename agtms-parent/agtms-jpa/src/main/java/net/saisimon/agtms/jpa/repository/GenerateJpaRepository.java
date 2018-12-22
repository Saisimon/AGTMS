package net.saisimon.agtms.jpa.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.jpa.util.JpaFilterUtils;

@Repository
@Slf4j
public class GenerateJpaRepository extends AbstractGenerateRepository {
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Long count(FilterRequest filter) {
		String countSql = buildCountSql();
		if (filter != null) {
			String where = JpaFilterUtils.where(filter);
			if (StringUtils.isNotBlank(where)) {
				countSql += " WHERE " + where;
			}
		}
		Query query = entityManager.createNativeQuery(countSql);
		@SuppressWarnings("unchecked")
		List<BigInteger> totals = query.getResultList();
		long total = 0L;
		for (BigInteger element : totals) {
			total += element == null ? 0 : element.longValue();
		}
		return total;
	}

	@Override
	public Boolean exists(FilterRequest filter) {
		return count(filter) > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Domain> findList(FilterRequest filter, FilterSort sort) {
		Template template = template();
		List<String> columnNames = TemplateUtils.getTableColumnNames(template);
		columnNames.add(CREATOR);
		columnNames.add(CTIME);
		columnNames.add(UTIME);
		String sql = buildSql(template, columnNames);
		if (filter != null) {
			String where = JpaFilterUtils.where(filter);
			if (StringUtils.isNotBlank(where)) {
				sql += " WHERE " + where;
			}
		}
		if (sort != null) {
			String orderby = JpaFilterUtils.orderby(sort);
			if (StringUtils.isNotBlank(orderby)) {
				sql += " ORDER BY " + orderby;
			}
		}
		Query query = entityManager.createNativeQuery(sql);
		try {
			return conversions(query.getResultList(), columnNames);
		} catch (GenerateException e) {
			log.error("find list error", e);
			return new ArrayList<>(0);
		}
	}

	@Override
	public Page<Domain> findPage(FilterRequest filter, FilterPageable pageable) {
		if (pageable == null) {
			pageable = FilterPageable.build(null);
		}
		Pageable springPageable = pageable.getPageable();
		Long total = count(filter);
		if (total == 0) {
			return new PageImpl<>(new ArrayList<>(0), springPageable, total);
		}
		Template template = template();
		List<String> columnNames = TemplateUtils.getTableColumnNames(template);
		columnNames.add(CREATOR);
		columnNames.add(CTIME);
		columnNames.add(UTIME);
		String sql = buildSql(template, columnNames);
		if (filter != null) {
			String where = JpaFilterUtils.where(filter);
			if (StringUtils.isNotBlank(where)) {
				sql += " WHERE " + where;
			}
		}
		if (pageable.getSort() != null) {
			String orderby = JpaFilterUtils.orderby(pageable.getSort());
			if (StringUtils.isNotBlank(orderby)) {
				sql += " ORDER BY " + orderby;
			}
		}
		Query query = entityManager.createNativeQuery(sql);
		if (springPageable.isPaged()) {
			query.setFirstResult((int) springPageable.getOffset());
			query.setMaxResults(springPageable.getPageSize());
		}
		try {
			@SuppressWarnings("unchecked")
			List<Domain> domains = conversions(query.getResultList(), columnNames);
			return new PageImpl<>(domains, springPageable, total);
		} catch (GenerateException e) {
			log.error("find page error", e);
			return new PageImpl<>(new ArrayList<>(0), springPageable, 0L);
		}
	}

	@Override
	public Optional<Domain> findOne(FilterRequest filter, FilterSort sort) {
		Template template = template();
		List<String> columnNames = TemplateUtils.getTableColumnNames(template);
		columnNames.add(CREATOR);
		columnNames.add(CTIME);
		columnNames.add(UTIME);
		String sql = buildSql(template, columnNames);
		if (filter != null) {
			String where = JpaFilterUtils.where(filter);
			if (StringUtils.isNotBlank(where)) {
				sql += " WHERE " + where;
			}
		}
		if (sort != null) {
			String orderby = JpaFilterUtils.orderby(sort);
			if (StringUtils.isNotBlank(orderby)) {
				sql += " ORDER BY " + orderby;
			}
		}
		Query query = entityManager.createNativeQuery(sql);
		query.setMaxResults(1);
		try {
			return conversion((Object[])query.getSingleResult(), columnNames);
		} catch (GenerateException e) {
			log.error("find one error", e);
			return Optional.empty();
		}
	}

	@Override
	@Transactional
	public Long delete(FilterRequest filter) {
		String sql = buildDeleteSql();
		if (filter != null) {
			String where = JpaFilterUtils.where(filter);
			if (StringUtils.isNotBlank(where)) {
				sql += " WHERE " + where;
			}
		}
		Query query = entityManager.createNativeQuery(sql);
		return (long) query.executeUpdate();
	}

	@Override
	public Optional<Domain> findById(Long id) {
		FilterRequest filter = FilterRequest.build().and("id", id);
		return findOne(filter, null);
	}

	@Override
	@Transactional
	public Domain deleteEntity(Long id) {
		Optional<Domain> optional = findById(id);
		if (optional.isPresent()) {
			FilterRequest filter = FilterRequest.build().and("id", id);
			delete(filter);
			return optional.get();
		}
		return null;
	}

	@Override
	@Transactional
	public Domain saveOrUpdate(Domain entity) {
		String sql;
		Object id = entity.getField("id");
		if (id == null) {
			sql = buildInsertSql(entity);
		} else {
			sql = buildUpdateSql(id, entity);
		}
		Query query = entityManager.createNativeQuery(sql);
		query.executeUpdate();
		return entity;
	}

	@Override
	@Transactional
	public void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		Template template = template();
		String sql = "UPDATE `" + TemplateUtils.getTableName(template) + "` SET ";
		for (Entry<String, Object> entry : updateMap.entrySet()) {
			String field = entry.getKey();
			Object value = entry.getValue();
			if (value != null) {
				sql += "`" + field + "`='" + value.toString() + "'";
			}
		}
		if (filter != null) {
			String where = JpaFilterUtils.where(filter);
			if (StringUtils.isNotBlank(where)) {
				sql += " WHERE " + where;
			}
		}
		Query query = entityManager.createNativeQuery(sql);
		query.executeUpdate();
	}
	
	private String buildCountSql() {
		return "SELECT COUNT(1) FROM `" + TemplateUtils.getTableName(template()) + "` ";
	}
	
	private String buildSql(Template template, List<String> columnNames) {
		String sql = "SELECT ";
		sql += columnNames.stream().map(field -> "`" + field + "`").collect(Collectors.joining(","));
		sql += " FROM `" + TemplateUtils.getTableName(template) + "` ";
		return sql;
	}
	
	private String buildDeleteSql() {
		return "DELETE FROM `" + TemplateUtils.getTableName(template()) + "` ";
	}
	
	private String buildInsertSql(Domain domain) {
		Template template = template();
		List<String> columnNames = TemplateUtils.getTableColumnNames(template);
		columnNames.add(CREATOR);
		columnNames.add(CTIME);
		columnNames.add(UTIME);
		String columns = "`id`";
		String values = "NULL";
		for (String field : columnNames) {
			Object value = domain.getField(field);
			if (value != null) {
				columns += ", `" + field + "`";
				values += ", '" + value + "'";
			}
		}
		return "INSERT INTO `" + TemplateUtils.getTableName(template) + "` (" + columns + ") VALUES (" + values + ") ";
	}
	
	private String buildUpdateSql(Object id, Domain domain) {
		Template template = template();
		List<String> columnNames = TemplateUtils.getTableColumnNames(template);
		columnNames.add(CREATOR);
		columnNames.add(CTIME);
		columnNames.add(UTIME);
		String sql = "UPDATE `" + TemplateUtils.getTableName(template) + "` SET ";
		for (String field : columnNames) {
			Object value = domain.getField(field);
			if (value != null) {
				sql += "`" + field + "`='" + value.toString() + "'";
			}
		}
		sql += " WHERE `id` = " + id.toString();
		return sql;
	}
	
	private List<Domain> conversions(List<Object[]> datas, List<String> columnNames) throws GenerateException {
		List<Domain> domains = new ArrayList<>();
		for (Object[] data : datas) {
			Optional<Domain> optional = conversion(data, columnNames);
			if (optional.isPresent()) {
				domains.add(optional.get());
			}
		}
		return domains;
	}
	
	private Optional<Domain> conversion(Object[] data, List<String> columnNames) throws GenerateException {
		if (data == null || data.length == 0) {
			return Optional.empty();
		}
		Domain domain = newGenerate();
		for (int i = 0; i < data.length; i++) {
			Object value = data[i];
			if (value != null) {
				String columnName = columnNames.get(i);
				if (value instanceof BigInteger) {
					domain.setField(columnName, ((BigInteger) value).longValue(), Long.class);
				} else if (value instanceof BigDecimal) {
					domain.setField(columnName, ((BigDecimal) value).doubleValue(), Double.class);
				} else {
					domain.setField(columnName, value, value.getClass());
				}
			}
		}
		return Optional.of(domain);
	}
	
}
