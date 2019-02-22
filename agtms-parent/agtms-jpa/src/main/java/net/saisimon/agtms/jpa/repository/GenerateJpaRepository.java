package net.saisimon.agtms.jpa.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.jpa.domain.Statement;
import net.saisimon.agtms.jpa.util.JpaFilterUtils;

@Repository
@Slf4j
public class GenerateJpaRepository extends AbstractGenerateRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Long count(FilterRequest filter) {
		String sql = buildCountSql();
		Object[] args = null;
		if (filter != null) {
			Statement where = JpaFilterUtils.where(filter);
			if (where.isNotEmpty()) {
				sql += " WHERE " + where.getExpression();
				args = where.getArgs().toArray();
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql);
		}
		return jdbcTemplate.queryForObject(sql, args, Long.class);
	}

	@Override
	public Boolean exists(FilterRequest filter) {
		return count(filter) > 0;
	}

	@Override
	public List<Domain> findList(FilterRequest filter, FilterSort sort) {
		Template template = template();
		List<String> columnNames = TemplateUtils.getTableColumnNames(template);
		columnNames.add(Constant.ID);
		columnNames.add(Constant.OPERATORID);
		columnNames.add(Constant.CREATETIME);
		columnNames.add(Constant.UPDATETIME);
		String sql = buildSql(template, columnNames);
		Object[] args = null;
		if (filter != null) {
			Statement where = JpaFilterUtils.where(filter);
			if (where.isNotEmpty()) {
				sql += " WHERE " + where.getExpression();
				args = where.getArgs().toArray();
			}
		}
		if (sort != null) {
			String orderby = JpaFilterUtils.orderby(sort);
			if (StringUtils.isNotBlank(orderby)) {
				sql += " ORDER BY " + orderby;
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql);
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, args);
		try {
			return conversions(list);
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
		columnNames.add(Constant.ID);
		columnNames.add(Constant.OPERATORID);
		columnNames.add(Constant.CREATETIME);
		columnNames.add(Constant.UPDATETIME);
		String sql = buildSql(template, columnNames);
		List<Object> args = new ArrayList<>();
		if (filter != null) {
			Statement where = JpaFilterUtils.where(filter);
			if (where.isNotEmpty()) {
				sql += " WHERE " + where.getExpression();
				args = where.getArgs();
			}
		}
		if (pageable.getSort() != null) {
			String orderby = JpaFilterUtils.orderby(pageable.getSort());
			if (StringUtils.isNotBlank(orderby)) {
				sql += " ORDER BY " + orderby;
			}
		}
		sql += " LIMIT ?, ?";
		args.add(springPageable.getOffset());
		args.add(springPageable.getPageSize());
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql);
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, args.toArray());
		try {
			List<Domain> domains = conversions(list);
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
		columnNames.add(Constant.ID);
		columnNames.add(Constant.OPERATORID);
		columnNames.add(Constant.CREATETIME);
		columnNames.add(Constant.UPDATETIME);
		String sql = buildSql(template, columnNames);
		Object[] args = null;
		if (filter != null) {
			Statement where = JpaFilterUtils.where(filter);
			if (where.isNotEmpty()) {
				sql += " WHERE " + where.getExpression();
				args = where.getArgs().toArray();
			}
		}
		if (sort != null) {
			String orderby = JpaFilterUtils.orderby(sort);
			if (StringUtils.isNotBlank(orderby)) {
				sql += " ORDER BY " + orderby;
			}
		}
		sql += " LIMIT 0, 1";
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql);
		}
		Map<String, Object> map = jdbcTemplate.queryForMap(sql, args);
		try {
			return Optional.ofNullable(conversion(map));
		} catch (GenerateException e) {
			log.error("find one error", e);
			return Optional.empty();
		}
	}

	@Override
	@Transactional
	public Long delete(FilterRequest filter) {
		String sql = buildDeleteSql();
		Object[] args = null;
		if (filter != null) {
			Statement where = JpaFilterUtils.where(filter);
			if (where.isNotEmpty()) {
				sql += " WHERE " + where.getExpression();
				args = where.getArgs().toArray();
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql);
		}
		return (long) jdbcTemplate.update(sql, args);
	}

	@Override
	public Optional<Domain> findById(Long id) {
		FilterRequest filter = FilterRequest.build().and(Constant.ID, id);
		return findOne(filter, null);
	}

	@Override
	@Transactional
	public Domain deleteEntity(Long id) {
		Optional<Domain> optional = findById(id);
		if (optional.isPresent()) {
			FilterRequest filter = FilterRequest.build().and(Constant.ID, id);
			delete(filter);
			return optional.get();
		}
		return null;
	}

	@Override
	@Transactional
	public Domain saveOrUpdate(Domain entity) {
		Object id = entity.getField(Constant.ID);
		if (id == null) {
			insert(entity);
		} else {
			update(id, entity);
		}
		return entity;
	}

	@Override
	@Transactional
	public void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		if (filter == null) {
			return;
		}
		Template template = template();
		String sql = "UPDATE `" + TemplateUtils.getTableName(template) + "` SET ";
		List<Object> args = new ArrayList<>();
		String set = "";
		for (Entry<String, Object> entry : updateMap.entrySet()) {
			String field = entry.getKey();
			Object value = entry.getValue();
			if (value != null) {
				if (!"".equals(set)) {
					set += ", ";
				}
				set += "`" + field + "` = ?";
				args.add(value);
			}
		}
		sql += set;
		Statement where = JpaFilterUtils.where(filter);
		if (where.isNotEmpty()) {
			sql += " WHERE " + where.getExpression();
			if (where.getArgs() != null) {
				args.addAll(where.getArgs());
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql);
		}
		jdbcTemplate.update(sql, args.toArray());
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
	
	private boolean insert(Domain domain) {
		Template template = template();
		List<String> columnNames = TemplateUtils.getTableColumnNames(template);
		columnNames.add(Constant.OPERATORID);
		columnNames.add(Constant.CREATETIME);
		columnNames.add(Constant.UPDATETIME);
		List<Object> args = new ArrayList<>();
		String columns = "`id`";
		String values = "NULL";
		for (String field : columnNames) {
			columns += ", `" + field + "`";
			values += ", ?";
			args.add(domain.getField(field));
		}
		String sql = "INSERT INTO `" + TemplateUtils.getTableName(template) + "` (" + columns + ") VALUES (" + values + ") ";
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql);
		}
		boolean result = jdbcTemplate.update(sql, args.toArray()) > 0;
		if (result) {
			Long id = getLastInsertId();
			domain.setField(Constant.ID, id, Long.class);
		}
		return result;
	}
	
	private Long getLastInsertId() {
		String sql = "SELECT LAST_INSERT_ID()";
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql);
		}
		return jdbcTemplate.queryForObject(sql, Long.class);
	}
	
	private boolean update(Object id, Domain domain) {
		Template template = template();
		List<String> columnNames = TemplateUtils.getTableColumnNames(template);
		columnNames.add(Constant.OPERATORID);
		columnNames.add(Constant.CREATETIME);
		columnNames.add(Constant.UPDATETIME);
		List<Object> args = new ArrayList<>();
		String sql = "UPDATE `" + TemplateUtils.getTableName(template) + "` SET ";
		String set = "";
		for (String field : columnNames) {
			Object value = domain.getField(field);
			if (value != null) {
				if (!"".equals(set)) {
					set += ", ";
				}
				set += "`" + field + "` = ?";
				args.add(value);
			}
		}
		sql += set + " WHERE `id` = ?";
		args.add(id);
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql);
		}
		return jdbcTemplate.update(sql, args.toArray()) > 0;
	}
	
	private List<Domain> conversions(List<Map<String, Object>> list) throws GenerateException {
		List<Domain> domains = new ArrayList<>();
		for (Map<String, Object> map : list) {
			Domain domain = conversion(map, null);
			if (domain != null) {
				domains.add(domain);
			}
		}
		return domains;
	}
	
}
