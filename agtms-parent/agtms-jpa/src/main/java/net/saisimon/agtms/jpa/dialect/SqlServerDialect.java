package net.saisimon.agtms.jpa.dialect;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.domain.Pageable;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.enums.Classes;

public class SqlServerDialect implements Dialect {

	@Override
	public String buildCreateSql(Map<String, TemplateField> fieldInfoMap, String tableName) {
		String sql = "CREATE TABLE " + tableName + " (";
		sql += Constant.ID + " BIGINT IDENTITY NOT NULL, ";
		for (Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldname = entry.getKey();
			TemplateField field = entry.getValue();
			sql += fieldname;
			sql += columnType(field) + ", ";
		}
		sql += Constant.OPERATORID + " BIGINT NOT NULL, ";
		sql += Constant.CREATETIME + " DATETIME2, ";
		sql += Constant.UPDATETIME + " DATETIME2, ";
		sql += "PRIMARY KEY (id))";
		return sql;
	}
	
	@Override
	public String buildDropSql(String tableName) {
		return "DROP TABLE " + tableName;
	}

	@Override
	public String buildAlterAddSql(TemplateField field, String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " ADD " + columnName + columnType(field);
	}

	@Override
	public String buildAlterModifySql(TemplateField field, String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + columnType(field);
	}

	@Override
	public String buildAlterDropSql(String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
	}

	@Override
	public String columnType(TemplateField field) {
		String column = "";
		if (Classes.LONG.getName().equals(field.getFieldType())) {
			column = " BIGINT";
		} else if (Classes.DOUBLE.getName().equals(field.getFieldType())) {
			column = " FLOAT";
		} else if (Classes.DATE.getName().equals(field.getFieldType())) {
			column = " DATETIME2";
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
		sql.append(" OFFSET ").append(pageable.getOffset()).append(" ROW FETCH NEXT ").append(pageable.getPageSize()).append(" ROW ONLY");
	}

}
