package net.saisimon.agtms.jpa.dialect;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.domain.Pageable;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Views;

public class OracleDialect implements Dialect {

	@Override
	public String buildCreateSql(Map<String, TemplateField> fieldInfoMap, String tableName) {
		String sql = "CREATE TABLE " + tableName + " (";
		sql += Constant.ID + " NUMBER(19, 0) GENERATED ALWAYS AS IDENTITY, ";
		for (Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldname = entry.getKey();
			TemplateField field = entry.getValue();
			sql += fieldname;
			sql += columnType(field) + ", ";
		}
		sql += Constant.OPERATORID + " NUMBER(19, 0) NOT NULL, ";
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
		return "ALTER TABLE " + tableName + " ADD " + columnName + columnType(field);
	}

	@Override
	public String buildAlterModifySql(TemplateField field, String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " MODIFY " + columnName + columnType(field);
	}

	@Override
	public String buildAlterDropSql(String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
	}

	@Override
	public String columnType(TemplateField field) {
		String column = "";
		if (Classes.LONG.getName().equals(field.getFieldType())) {
			column = " NUMBER(19, 0)";
		} else if (Classes.DOUBLE.getName().equals(field.getFieldType())) {
			column = " FLOAT";
		} else if (Classes.DATE.getName().equals(field.getFieldType())) {
			column = " TIMESTAMP";
		} else if (Views.TEXTAREA.getView().equals(field.getViews())) {
			column = " CLOB";
		} else if (Views.PHONE.getView().equals(field.getViews())) {
			column = " VARCHAR(32)";
		} else if (Views.EMAIL.getView().equals(field.getViews())) {
			column = " VARCHAR(256)";
		} else if (Views.LINK.getView().equals(field.getViews())) {
			column = " VARCHAR(1024)";
		} else if (Views.ICON.getView().equals(field.getViews())) {
			column = " VARCHAR(64)";
		} else if (Views.IMAGE.getView().equals(field.getViews())) {
			column = " VARCHAR(64)";
		} else {
			column = " VARCHAR(512)";
		}
		if (field.getRequired()) {
			column += " NOT NULL";
		}
		return column;
	}
	
	@Override
	public void wrapPageSql(StringBuilder sql, Pageable pageable) {
		sql.insert(0, "SELECT * FROM ( SELECT row_.*, rownum rownum_ FROM ( ");
		sql.append(" ) row_ WHERE rownum <= ").append(pageable.getOffset() + pageable.getPageSize()).append(" ) table_alias WHERE table_alias.rownum_ > ").append(pageable.getOffset());
	}

}
