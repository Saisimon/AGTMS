package net.saisimon.agtms.jpa.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.util.TemplateUtils;

@Service
@Slf4j
public class DdlService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void createTable(Template template) {
		String sql = buildCreateSql(template);
		log.info("DDL: {}", sql);
		jdbcTemplate.execute(sql);
	}
	
	public void alterTable(Template template, Template oldTemplate) {
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
			log.info("DDL: {}", sql);
			jdbcTemplate.execute(sql);
		}
		for (Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldName = entry.getKey();
			TemplateField field = entry.getValue();
			if (commonFieldName.contains(fieldName)) {
				TemplateField oldField = oldFieldInfoMap.get(fieldName);
				if (!oldField.getFieldType().equals(field.getFieldType())) {
					String sql = buildAlterModifySql(tableName, fieldName, field);
					log.info("DDL: {}", sql);
					jdbcTemplate.execute(sql);
				}
			} else {
				String sql = buildAlterAddSql(tableName, fieldName, field);
				log.info("DDL: {}", sql);
				jdbcTemplate.execute(sql);
			}
		}
	}
	
	public void dropTable(Template template) {
		String sql = buildDropSql(template);
		log.info("DDL: {}", sql);
		jdbcTemplate.execute(sql);
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
			sql += columnType(field);
			if (field.getRequired()) {
				sql += " NOT NULL, ";
			} else if (field.getDefaultValue() != null) {
				sql += " DEFAULT '" + field.getDefaultValue() + "', ";
			} else {
				sql += " DEFAULT NULL, ";
			}
		}
		sql += AbstractGenerateRepository.CREATOR + " BIGINT(15) DEFAULT NULL, ";
		sql += AbstractGenerateRepository.CTIME + " BIGINT(15) DEFAULT NULL, ";
		sql += AbstractGenerateRepository.UTIME + " BIGINT(15) DEFAULT NULL, ";
		sql += "PRIMARY KEY (id)";
		sql += ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
		return sql;
	}
	
	private String columnType(TemplateField field) {
		if (Classes.INTEGER.getClazz().equals(field.getFieldType())) {
			return " BIGINT(15) ";
		} else if (Classes.DOUBLE.getClazz().equals(field.getFieldType())) {
			return " DECIMAL(15,2) ";
		} else {
			return " VARCHAR(500) ";
		}
	}
	
}
