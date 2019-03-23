package net.saisimon.agtms.jpa.dialect;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.domain.Pageable;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.enums.Classes;

public class PostgresqlDialect implements Dialect {

	@Override
	public String buildCreateSql(Map<String, TemplateField> fieldInfoMap, String tableName) {
		String sql = "CREATE TABLE " + tableName + " (";
		sql += Constant.ID + " BIGSERIAL NOT NULL, ";
		for (Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldname = entry.getKey();
			TemplateField field = entry.getValue();
			sql += fieldname;
			sql += columnType(field) + ", ";
		}
		sql += Constant.OPERATORID + " INT8 NOT NULL, ";
		sql += Constant.CREATETIME + " TIMESTAMP, ";
		sql += Constant.UPDATETIME + " TIMESTAMP, ";
		sql += "PRIMARY KEY (id))";
		return sql;
	}
	
	@Override
	public String buildDropSql(String tableName) {
		return "DROP TABLE " + tableName;
	}

	@Override
	public String buildAlterAddSql(TemplateField field, String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + columnType(field);
	}

	@Override
	public String buildAlterModifySql(TemplateField field, String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " TYPE " + columnType(field);
	}

	@Override
	public String buildAlterDropSql(String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
	}

	@Override
	public String columnType(TemplateField field) {
		String column = "";
		if (Classes.LONG.getName().equals(field.getFieldType())) {
			column = " INT8";
		} else if (Classes.DOUBLE.getName().equals(field.getFieldType())) {
			column = " FLOAT8";
		} else if (Classes.DATE.getName().equals(field.getFieldType())) {
			column = " TIMESTAMP";
		} else {
			column = " VARCHAR(500)";
		}
		if (field.getRequired()) {
			column += " NOT NULL";
		}
		return column;
	}
	
	@Override
	public void wrapPageSql(StringBuilder sql, Pageable pageable) {
		sql.append(" LIMIT ").append(pageable.getPageSize()).append(" OFFSET ").append(pageable.getOffset());
	}

}
