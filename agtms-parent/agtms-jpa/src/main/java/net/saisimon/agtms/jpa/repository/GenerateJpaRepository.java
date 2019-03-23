package net.saisimon.agtms.jpa.repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.jpa.dialect.Dialect;
import net.saisimon.agtms.jpa.domain.Statement;
import net.saisimon.agtms.jpa.util.JpaFilterUtils;

@Repository
@Slf4j
public class GenerateJpaRepository extends AbstractGenerateRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private Dialect dialect;
	
	@Override
	public Long count(FilterRequest filter) {
		StringBuilder sql = buildCountSql();
		Object[] args = null;
		if (filter != null) {
			Statement where = JpaFilterUtils.where(filter);
			if (where.isNotEmpty()) {
				sql.append(" WHERE ").append(where.getExpression());
				if (where.getArgs() != null) {
					args = where.getArgs().toArray();
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql.toString());
		}
		return jdbcTemplate.queryForObject(sql.toString(), args, Long.class);
	}

	@Override
	public List<Domain> findList(FilterRequest filter, FilterSort sort, String... properties) {
		Template template = template();
		Set<String> columnNames = getColumnNames(template, properties);
		StringBuilder sql = buildSql(template, columnNames);
		Object[] args = null;
		if (filter != null) {
			Statement where = JpaFilterUtils.where(filter);
			if (where.isNotEmpty()) {
				sql.append(" WHERE ").append(where.getExpression());
				if (where.getArgs() != null) {
					args = where.getArgs().toArray();
				}
			}
		}
		if (sort != null) {
			String orderby = JpaFilterUtils.orderby(sort);
			if (StringUtils.isNotBlank(orderby)) {
				sql.append(" ORDER BY ").append(orderby);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql.toString());
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), args);
		try {
			return conversions(list);
		} catch (GenerateException e) {
			log.error("find list error", e);
			return new ArrayList<>(0);
		}
	}
	
	@Override
	public List<Domain> findList(FilterRequest filter, FilterPageable pageable, String... properties) {
		Template template = template();
		Set<String> columnNames = getColumnNames(template, properties);
		StringBuilder sql = buildSql(template, columnNames);
		List<Object> args = new ArrayList<>();
		if (filter != null) {
			Statement where = JpaFilterUtils.where(filter);
			if (where.isNotEmpty()) {
				sql.append(" WHERE ").append(where.getExpression());
				if (where.getArgs() != null) {
					args = where.getArgs();
				}
			}
		}
		if (pageable != null) {
			sql.append(" ORDER BY ").append(JpaFilterUtils.orderby(pageable.getSort()));
			dialect.wrapPageSql(sql, pageable.getPageable());
		}
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql.toString());
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), args.toArray());
		try {
			return conversions(list);
		} catch (GenerateException e) {
			log.error("find list error", e);
			return new ArrayList<>(0);
		}
	}

	@Override
	public Page<Domain> findPage(FilterRequest filter, FilterPageable pageable, String... properties) {
		StringBuilder countSql = buildCountSql();
		List<Object> args = new ArrayList<>();
		Statement where = JpaFilterUtils.where(filter);
		if (where.isNotEmpty()) {
			countSql.append(" WHERE ").append(where.getExpression());
			if (where.getArgs() != null) {
				args = where.getArgs();
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + countSql.toString());
		}
		Long total = jdbcTemplate.queryForObject(countSql.toString(), args.toArray(), Long.class);
		if (pageable == null) {
			pageable = FilterPageable.build(null);
		}
		Pageable springPageable = pageable.getPageable();
		if (total == 0) {
			return new PageImpl<>(new ArrayList<>(0), springPageable, total);
		}
		Template template = template();
		Set<String> columnNames = getColumnNames(template, properties);
		StringBuilder sql = buildSql(template, columnNames);
		if (where.isNotEmpty()) {
			sql.append(" WHERE ").append(where.getExpression());
		}
		sql.append(" ORDER BY ").append(JpaFilterUtils.orderby(pageable.getSort()));
		dialect.wrapPageSql(sql, springPageable);
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql.toString());
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), args.toArray());
		try {
			return new PageImpl<>(conversions(list), springPageable, total);
		} catch (GenerateException e) {
			log.error("find page error", e);
			return new PageImpl<>(new ArrayList<>(0), springPageable, 0L);
		}
	}

	@Override
	public Optional<Domain> findOne(FilterRequest filter, FilterSort sort, String... properties) {
		Template template = template();
		Set<String> columnNames = getColumnNames(template, properties);
		StringBuilder sql = buildSql(template, columnNames);
		Object[] args = null;
		if (filter != null) {
			Statement where = JpaFilterUtils.where(filter);
			if (where.isNotEmpty()) {
				sql.append(" WHERE ").append(where.getExpression());
				if (where.getArgs() != null) {
					args = where.getArgs().toArray();
				}
			}
		}
		if (sort != null) {
			sql.append(" ORDER BY ").append(JpaFilterUtils.orderby(sort));
		}
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql.toString());
		}
		ArgumentPreparedStatementSetter pss = new ArgumentPreparedStatementSetter(args);
		List<Map<String, Object>> maps = jdbcTemplate.query(conn -> {
			PreparedStatement ps = conn.prepareStatement(sql.toString());
			pss.setValues(ps);
			ps.setMaxRows(1);
			return ps;
		}, new RowMapperResultSetExtractor<Map<String, Object>>(new ColumnMapRowMapper()));
		pss.cleanupParameters();
		if (CollectionUtils.isEmpty(maps)) {
			return Optional.empty();
		}
		try {
			return Optional.ofNullable(conversion(maps.get(0)));
		} catch (GenerateException e) {
			log.error("find one error", e);
			return Optional.empty();
		}
	}
	
	@Override
	@Transactional
	public void delete(Domain entity) {
		if (entity == null) { 
			return;
		}
		StringBuilder sql = buildDeleteSql();
		Object[] args = {entity.getField(Constant.ID)};
		sql.append(" WHERE id = ?");
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql.toString());
		}
		jdbcTemplate.update(sql.toString(), args);
	}

	@Override
	@Transactional
	public Long delete(FilterRequest filter) {
		StringBuilder sql = buildDeleteSql();
		Object[] args = null;
		if (filter != null) {
			Statement where = JpaFilterUtils.where(filter);
			if (where.isNotEmpty()) {
				sql.append(" WHERE ").append(where.getExpression());
				if (where.getArgs() != null) {
					args = where.getArgs().toArray();
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql.toString());
		}
		return (long) jdbcTemplate.update(sql.toString(), args);
	}

	@Override
	@Transactional
	public Domain saveOrUpdate(Domain entity) {
		if (entity == null) {
			return entity;
		}
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
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(TemplateUtils.getTableName(template)).append(" SET ");
		List<Object> args = new ArrayList<>();
		int size = sql.length();
		for (Entry<String, Object> entry : updateMap.entrySet()) {
			String field = entry.getKey();
			Object value = entry.getValue();
			if (value != null) {
				if (sql.length() != size) {
					sql.append(", ");
				}
				sql.append(field).append(" = ?");
				args.add(value);
			}
		}
		Statement where = JpaFilterUtils.where(filter);
		if (where.isNotEmpty()) {
			sql.append(" WHERE ").append(where.getExpression());
			if (where.getArgs() != null) {
				args.addAll(where.getArgs());
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql.toString());
		}
		jdbcTemplate.update(sql.toString(), args.toArray());
	}
	
	private StringBuilder buildCountSql() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(").append(Constant.ID).append(") FROM ").append(TemplateUtils.getTableName(template()));
		return sb;
	}
	
	private StringBuilder buildSql(Template template, Set<String> columnNames) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ").append(columnNames.stream().collect(Collectors.joining(", "))).append(" FROM ").append(TemplateUtils.getTableName(template));
		return sb;
	}
	
	private StringBuilder buildDeleteSql() {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ").append(TemplateUtils.getTableName(template()));
		return sb;
	}
	
	private boolean insert(Domain domain) {
		Template template = template();
		Set<String> columnNames = TemplateUtils.getFieldNames(template);
		columnNames.add(Constant.OPERATORID);
		columnNames.add(Constant.CREATETIME);
		columnNames.add(Constant.UPDATETIME);
		List<Object> args = new ArrayList<>();
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();
		for (String field : columnNames) {
			if (columns.length() != 0) {
				columns.append(", ");
				values.append(", ");
			}
			columns.append(field);
			values.append("?");
			args.add(domain.getField(field));
		}
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(TemplateUtils.getTableName(template)).append(" (").append(columns).append(") VALUES (").append(values).append(") ");
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql.toString());
		}
		KeyHolder holder = new GeneratedKeyHolder();
		ArgumentPreparedStatementSetter pss = new ArgumentPreparedStatementSetter(args.toArray());
		int result = jdbcTemplate.update(conn -> {
//			PreparedStatement ps = conn.prepareStatement(sql.toString(), java.sql.Statement.RETURN_GENERATED_KEYS);
			PreparedStatement ps = conn.prepareStatement(sql.toString(), new String[] { Constant.ID });
			pss.setValues(ps);
			return ps;
		}, holder);
		if (result > 0) {
			domain.setField(Constant.ID, holder.getKey().longValue(), Long.class);
		}
		pss.cleanupParameters();
		return result > 0;
	}
	
	private boolean update(Object id, Domain domain) {
		Template template = template();
		Set<String> columnNames = TemplateUtils.getFieldNames(template);
		columnNames.add(Constant.OPERATORID);
		columnNames.add(Constant.CREATETIME);
		columnNames.add(Constant.UPDATETIME);
		List<Object> args = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(TemplateUtils.getTableName(template)).append(" SET ");
		int size = sql.length();
		for (String field : columnNames) {
			Object value = domain.getField(field);
			if (value != null) {
				if (sql.length() != size) {
					sql.append(", ");
				}
				sql.append(field).append(" = ?");
				args.add(value);
			}
		}
		sql.append(" WHERE id = ?");
		args.add(id);
		if (log.isDebugEnabled()) {
			log.debug("Generate SQL: " + sql.toString());
		}
		return jdbcTemplate.update(sql.toString(), args.toArray()) > 0;
	}
	
	private List<Domain> conversions(List<Map<String, Object>> list) throws GenerateException {
		List<Domain> domains = new ArrayList<>();
		for (Map<String, Object> map : list) {
			Domain domain = conversion(map);
			if (domain != null) {
				domains.add(domain);
			}
		}
		return domains;
	}
	
	private Set<String> getColumnNames(Template template, String... properties) {
		Set<String> columnNames = null;
		if (properties == null || properties.length == 0) {
			columnNames = TemplateUtils.getFieldNames(template);
		} else {
			columnNames = new HashSet<>(properties.length);
			for (String property : properties) {
				if (property != null) {
					columnNames.add(property);
				}
			}
		}
		columnNames.add(Constant.ID);
		columnNames.add(Constant.OPERATORID);
		columnNames.add(Constant.CREATETIME);
		columnNames.add(Constant.UPDATETIME);
		return columnNames;
	}

}
