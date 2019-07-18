package net.saisimon.agtms.jpa.service.ddl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.jpa.dialect.Dialect;

/**
 * DDL 服务
 * 
 * @author saisimon
 *
 */
@Slf4j
@Service
public class DdlService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private Dialect dialect;

	/**
	 * 根据模板创建表结构
	 * 
	 * @param template 模板对象
	 */
	public boolean createTable(String tableName, Map<String, TemplateField> fieldInfoMap) {
		if (StringUtils.isEmpty(tableName)) {
			return false;
		}
		String sql = dialect.buildCreateSQL(fieldInfoMap, tableName);
		if (log.isDebugEnabled()) {
			log.debug("DDL: {}", sql);
		}
		try {
			jdbcTemplate.execute(sql);
			return true;
		} catch (DataAccessException e) {
			log.error("模板创建表结构失败, SQL: " + sql, e);
			return false;
		}
	}

	/**
	 * 根据模板的变化修改表结构
	 * 
	 * @param template 新模板对象
	 * @param oldTemplate 老模板对象
	 */
	public boolean alterTable(String tableName, Map<String, TemplateField> fieldInfoMap, Map<String, TemplateField> oldFieldInfoMap) {
		if (StringUtils.isEmpty(tableName)) {
			return false;
		}
		Map<String, String> rollbackMap = new HashMap<>();
		Set<String> sqlSet = new HashSet<>();
		try {
			Set<String> commonFieldName = new HashSet<>(fieldInfoMap.keySet());
			commonFieldName.retainAll(oldFieldInfoMap.keySet());
			for (String fieldName : oldFieldInfoMap.keySet()) {
				if (commonFieldName.contains(fieldName)) {
					continue;
				}
				String sql = dialect.buildAlterDropSQL(tableName, fieldName);
				if (log.isDebugEnabled()) {
					log.debug("DDL: {}", sql);
				}
				jdbcTemplate.execute(sql);
				String rollbackSql = dialect.buildAlterAddSQL(oldFieldInfoMap.get(fieldName), tableName, fieldName);
				rollbackMap.put(sql, rollbackSql);
				sqlSet.add(sql);
			}
			for (Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
				String fieldName = entry.getKey();
				TemplateField field = entry.getValue();
				if (commonFieldName.contains(fieldName)) {
					TemplateField oldField = oldFieldInfoMap.get(fieldName);
					if (!oldField.getFieldType().equals(field.getFieldType())) {
						String sql = dialect.buildAlterModifySQL(field, tableName, fieldName);
						if (log.isDebugEnabled()) {
							log.debug("DDL: {}", sql);
						}
						jdbcTemplate.execute(sql);
						String rollbackSql = dialect.buildAlterModifySQL(oldField, tableName, fieldName);
						rollbackMap.put(sql, rollbackSql);
						sqlSet.add(sql);
					}
				} else {
					String sql = dialect.buildAlterAddSQL(field, tableName, fieldName);
					if (log.isDebugEnabled()) {
						log.debug("DDL: {}", sql);
					}
					jdbcTemplate.execute(sql);
					String rollbackSql = dialect.buildAlterDropSQL(tableName, fieldName);
					rollbackMap.put(sql, rollbackSql);
					sqlSet.add(sql);
				}
			}
			return true;
		} catch (DataAccessException e) {
			for (String sql : sqlSet) {
				String rollbackSql = rollbackMap.get(sql);
				if (rollbackSql == null) {
					continue;
				}
				if (log.isDebugEnabled()) {
					log.debug("ROLLBACK DDL: {}", rollbackSql);
				}
				jdbcTemplate.execute(rollbackSql);
			}
			log.error("模板修改表结构失败", e);
			return false;
		}
	}

	/**
	 * 根据模板删除表结构
	 * 
	 * @param template 模板对象
	 */
	public boolean dropTable(String tableName) {
		if (StringUtils.isEmpty(tableName)) {
			return false;
		}
		String sql = dialect.buildDropSQL(tableName);
		if (log.isDebugEnabled()) {
			log.debug("DDL: {}", sql);
		}
		try {
			jdbcTemplate.execute(sql);
			return true;
		} catch (DataAccessException e) {
			log.error("模板删除表结构失败, SQL: " + sql, e);
			return false;
		}
	}
	
	public boolean createIndex(String tableName, String columnName, boolean unique) {
		if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
			return false;
		}
		String indexName = tableName + "_" + columnName + "_idx";
		String sql = dialect.buildCreateIndexSQL(tableName, columnName, indexName, unique);
		if (log.isDebugEnabled()) {
			log.debug("DDL: {}", sql);
		}
		try {
			jdbcTemplate.execute(sql);
			return true;
		} catch (DataAccessException e) {
			log.error("模板创建表索引结构失败", e);
			return false;
		}
	}
	
	public boolean dropIndex(String tableName, String columnName) {
		if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
			return false;
		}
		String indexName = tableName + "_" + columnName + "_idx";
		String sql = dialect.buildDropIndexSQL(tableName, indexName);
		if (log.isDebugEnabled()) {
			log.debug("DDL: {}", sql);
		}
		try {
			jdbcTemplate.execute(sql);
			return true;
		} catch (DataAccessException e) {
			log.error("模板删除表索引结构失败", e);
			return false;
		}
	}
	
}
