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

/**
 * SQL Server 数据库方言
 * 
 * @author saisimon
 *
 */
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
				sql += " " + columnType(field) + ", ";
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
		return String.format("DROP TABLE %s", tableName);
	}

	@Override
	public String buildAlterAddSQL(TemplateField field, String tableName, String columnName) {
		if (field == null || StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
			return null;
		}
		return String.format("ALTER TABLE %s ADD %s %s", tableName, columnName, columnType(field));
	}

	@Override
	public String buildAlterModifySQL(TemplateField field, String tableName, String columnName) {
		if (field == null || StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
			return null;
		}
		return String.format("ALTER TABLE %s ALTER COLUMN %s %s", tableName, columnName, columnType(field));
	}

	@Override
	public String buildAlterDropSQL(String tableName, String columnName) {
		if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
			return null;
		}
		return String.format("ALTER TABLE %s DROP COLUMN %s", tableName, columnName);
	}

	@Override
	public String columnType(TemplateField field) {
		if (field == null) {
			return null;
		}
		String column = "";
		if (Classes.LONG.getKey().equals(field.getFieldType())) {
			column = "BIGINT";
		} else if (Classes.DOUBLE.getKey().equals(field.getFieldType())) {
			column = "FLOAT";
		} else if (Classes.DATE.getKey().equals(field.getFieldType())) {
			column = "DATETIME2";
		} else if (Views.TEXTAREA.getKey().equals(field.getViews())) {
			column = "TEXT";
		} else if (Views.PHONE.getKey().equals(field.getViews())) {
			column = "VARCHAR(32)";
		} else if (Views.EMAIL.getKey().equals(field.getViews())) {
			column = "VARCHAR(256)";
		} else if (Views.LINK.getKey().equals(field.getViews())) {
			column = "VARCHAR(1024)";
		} else if (Views.ICON.getKey().equals(field.getViews())) {
			column = "VARCHAR(64)";
		} else if (Views.IMAGE.getKey().equals(field.getViews())) {
			column = "VARCHAR(64)";
		} else {
			column = "VARCHAR(512)";
		}
		if (field.getRequired()) {
			column += " NOT NULL";
		}
		return column;
	}
	
	@Override
	public String buildCreateIndexSQL(String tableName, String columnName, String indexName, boolean unique) {
		if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName) || StringUtils.isEmpty(indexName)) {
			return null;
		}
		return String.format("CREATE%sINDEX %s ON %s (%s)", unique ? " UNIQUE " : " ", indexName, tableName, columnName);
	}

	@Override
	public String buildDropIndexSQL(String tableName, String indexName) {
		if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(indexName)) {
			return null;
		}
		return String.format("DROP INDEX IF EXISTS %s.%s", tableName, indexName);
	}
	
	@Override
	public void wrapPageSQL(StringBuilder sql, Pageable pageable) {
		if (sql == null || pageable == null) {
			return;
		}
		sql.append(" OFFSET ").append(pageable.getOffset()).append(" ROW FETCH NEXT ").append(pageable.getPageSize()).append(" ROW ONLY");
	}

}
