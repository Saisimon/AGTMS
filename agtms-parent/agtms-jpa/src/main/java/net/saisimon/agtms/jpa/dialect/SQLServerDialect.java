package net.saisimon.agtms.jpa.dialect;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Views;

public class SQLServerDialect implements Dialect {

	@Override
	public String buildCreateSQL(Map<String, TemplateField> fieldInfoMap, String tableName) {
		if (StringUtils.isEmpty(tableName)) {
			return null;
		}
		String sql = "CREATE TABLE " + tableName + " (";
		sql += Constant.ID + " BIGINT IDENTITY NOT NULL, ";
		if (!CollectionUtils.isEmpty(fieldInfoMap)) {
			for (Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
				String fieldname = entry.getKey();
				TemplateField field = entry.getValue();
				sql += fieldname;
				sql += columnType(field) + ", ";
			}
		}
		sql += Constant.OPERATORID + " BIGINT NOT NULL, ";
		sql += Constant.CREATETIME + " DATETIME2, ";
		sql += Constant.UPDATETIME + " DATETIME2, ";
		sql += "PRIMARY KEY (id))";
		return sql;
	}
	
	@Override
	public String buildDropSQL(String tableName) {
		if (StringUtils.isEmpty(tableName)) {
			return null;
		}
		return "DROP TABLE " + tableName;
	}

	@Override
	public String buildAlterAddSQL(TemplateField field, String tableName, String columnName) {
		if (field == null || StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
			return null;
		}
		return "ALTER TABLE " + tableName + " ADD " + columnName + columnType(field);
	}

	@Override
	public String buildAlterModifySQL(TemplateField field, String tableName, String columnName) {
		if (field == null || StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
			return null;
		}
		return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + columnType(field);
	}

	@Override
	public String buildAlterDropSQL(String tableName, String columnName) {
		if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
			return null;
		}
		return "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
	}

	@Override
	public String columnType(TemplateField field) {
		if (field == null) {
			return null;
		}
		String column = "";
		if (Classes.LONG.getName().equals(field.getFieldType())) {
			column = " BIGINT";
		} else if (Classes.DOUBLE.getName().equals(field.getFieldType())) {
			column = " FLOAT";
		} else if (Classes.DATE.getName().equals(field.getFieldType())) {
			column = " DATETIME2";
		} else if (Views.TEXTAREA.getView().equals(field.getViews())) {
			column = " TEXT";
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
	public void wrapPageSQL(StringBuilder sql, Pageable pageable) {
		if (sql == null || pageable == null) {
			return;
		}
		sql.append(" OFFSET ").append(pageable.getOffset()).append(" ROW FETCH NEXT ").append(pageable.getPageSize()).append(" ROW ONLY");
	}

}
