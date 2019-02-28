package net.saisimon.agtms.jpa.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.TemplateUtils;

/**
 * DDL 服务
 * 
 * @author saisimon
 *
 */
@Service
@Slf4j
public class DdlService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 根据模板创建表结构
	 * 
	 * @param template 模板对象
	 */
	public boolean createTable(Template template) {
		try {
			String sql = buildCreateSql(template);
			if (log.isDebugEnabled()) {
				log.debug("DDL: {}", sql);
			}
			jdbcTemplate.execute(sql);
			return true;
		} catch (DataAccessException e) {
			log.error("模板创建表结构失败", e);
			return false;
		}
	}
	
	/**
	 * 根据模板的变化修改表结构
	 * 
	 * @param template 新模板对象
	 * @param oldTemplate 老模板对象
	 */
	public boolean alterTable(Template template, Template oldTemplate) {
		Map<String, String> rollbackMap = new HashMap<>();
		Set<String> sqlSet = new HashSet<>();
		try {
			String tableName = TemplateUtils.getTableName(template);
			Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
			Map<String, TemplateField> oldFieldInfoMap = TemplateUtils.getFieldInfoMap(oldTemplate);
			Set<String> commonFieldName = new HashSet<>(fieldInfoMap.keySet());
			commonFieldName.retainAll(oldFieldInfoMap.keySet());
			for (String fieldName : oldFieldInfoMap.keySet()) {
				if (commonFieldName.contains(fieldName)) {
					continue;
				}
				String sql = buildAlterDropSql(tableName, fieldName);
				if (log.isDebugEnabled()) {
					log.debug("DDL: {}", sql);
				}
				jdbcTemplate.execute(sql);
				String rollbackSql = buildAlterAddSql(tableName, fieldName, oldFieldInfoMap.get(fieldName));
				rollbackMap.put(sql, rollbackSql);
				sqlSet.add(sql);
			}
			for (Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
				String fieldName = entry.getKey();
				TemplateField field = entry.getValue();
				if (commonFieldName.contains(fieldName)) {
					TemplateField oldField = oldFieldInfoMap.get(fieldName);
					if (!oldField.getFieldType().equals(field.getFieldType())) {
						String sql = buildAlterModifySql(tableName, fieldName, field);
						if (log.isDebugEnabled()) {
							log.debug("DDL: {}", sql);
						}
						jdbcTemplate.execute(sql);
						String rollbackSql = buildAlterModifySql(tableName, fieldName, oldField);
						rollbackMap.put(sql, rollbackSql);
						sqlSet.add(sql);
					}
				} else {
					String sql = buildAlterAddSql(tableName, fieldName, field);
					if (log.isDebugEnabled()) {
						log.debug("DDL: {}", sql);
					}
					jdbcTemplate.execute(sql);
					String rollbackSql = buildAlterDropSql(tableName, fieldName);
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
	public boolean dropTable(Template template) {
		try {
			String sql = buildDropSql(template);
			if (log.isDebugEnabled()) {
				log.debug("DDL: {}", sql);
			}
			jdbcTemplate.execute(sql);
			return true;
		} catch (DataAccessException e) {
			log.error("模板删除表结构失败", e);
			return false;
		}
	}
	
	private String buildAlterModifySql(String tableName, String name, TemplateField field) {
		return "ALTER TABLE " + tableName + " MODIFY COLUMN " + name + columnType(field)  + ";";
	}
	
	private String buildAlterAddSql(String tableName, String name, TemplateField field) {
		return "ALTER TABLE " + tableName + " ADD COLUMN " + name + columnType(field)  + ";";
	}
	
	private String buildAlterDropSql(String tableName, String name) {
		return "ALTER TABLE " + tableName + " DROP COLUMN " + name + ";";
	}
	
	private String buildDropSql(Template template) {
		return "DROP TABLE IF EXISTS " + TemplateUtils.getTableName(template);
	}

	private String buildCreateSql(Template template) {
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		String sql = "CREATE TABLE IF NOT EXISTS " + TemplateUtils.getTableName(template) + " (";
		sql += "id BIGINT(15) UNSIGNED NOT NULL AUTO_INCREMENT, ";
		for (Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldname = entry.getKey();
			TemplateField field = entry.getValue();
			sql += fieldname;
			sql += columnType(field) + ", ";
		}
		sql += Constant.OPERATORID + " BIGINT(15) NOT NULL, ";
		sql += Constant.CREATETIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ";
		sql += Constant.UPDATETIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ";
		sql += "PRIMARY KEY (id)";
		sql += ") DEFAULT CHARSET=utf8";
		return sql;
	}
	
	private String columnType(TemplateField field) {
		String column = "";
		if (Classes.LONG.getName().equals(field.getFieldType())) {
			column = " BIGINT(15)";
		} else if (Classes.DOUBLE.getName().equals(field.getFieldType())) {
			column = " DECIMAL(15,2)";
		} else if (Classes.DATE.getName().equals(field.getFieldType())) {
			column = " DATE";
		} else {
			column = " VARCHAR(500)";
		}
		if (field.getRequired()) {
			column += " NOT NULL";
		}
		if (Classes.DATE.getName().equals(field.getFieldType())) {
			column += " DEFAULT NULL";
		} else if (StringUtils.isNotEmpty(field.getDefaultValue())) {
			column += " DEFAULT '" + field.getDefaultValue() + "'";
		}
		return column;
	}
	
}
