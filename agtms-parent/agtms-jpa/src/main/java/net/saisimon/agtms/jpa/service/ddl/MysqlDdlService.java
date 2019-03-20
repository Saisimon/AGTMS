package net.saisimon.agtms.jpa.service.ddl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.util.TemplateUtils;

@Slf4j
public class MysqlDdlService implements DdlService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public boolean createTable(Template template) {
		if (template == null) {
			return false;
		}
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

	@Override
	public boolean alterTable(Template template, Template oldTemplate) {
		if (template == null || oldTemplate == null) {
			return false;
		}
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

	@Override
	public boolean dropTable(Template template) {
		if (template == null) {
			return false;
		}
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
		return "ALTER TABLE " + tableName + " MODIFY COLUMN " + name + columnType(field) + ";";
	}

	private String buildAlterAddSql(String tableName, String name, TemplateField field) {
		return "ALTER TABLE " + tableName + " ADD COLUMN " + name + columnType(field) + ";";
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
		sql += Constant.ID + " BIGINT NOT NULL AUTO_INCREMENT, ";
		for (Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldname = entry.getKey();
			TemplateField field = entry.getValue();
			sql += fieldname;
			sql += columnType(field) + ", ";
		}
		sql += Constant.OPERATORID + " BIGINT NOT NULL, ";
		sql += Constant.CREATETIME + " DATETIME, ";
		sql += Constant.UPDATETIME + " DATETIME, ";
		sql += "PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8";
		return sql;
	}

	private String columnType(TemplateField field) {
		String column = "";
		if (Classes.LONG.getName().equals(field.getFieldType())) {
			column = " BIGINT";
		} else if (Classes.DOUBLE.getName().equals(field.getFieldType())) {
			column = " DOUBLE";
		} else if (Classes.DATE.getName().equals(field.getFieldType())) {
			column = " DATETIME";
		} else {
			column = " VARCHAR(500)";
		}
		if (field.getRequired()) {
			column += " NOT NULL";
		}
		return column;
	}

}
