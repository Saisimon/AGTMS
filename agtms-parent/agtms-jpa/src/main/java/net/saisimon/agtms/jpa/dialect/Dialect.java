package net.saisimon.agtms.jpa.dialect;

import java.util.Map;

import org.springframework.data.domain.Pageable;

import net.saisimon.agtms.core.domain.entity.Template.TemplateField;

/**
 * 数据库方言
 * 
 * @author saisimon
 *
 */
public interface Dialect {
	
	/**
	 * 列类型表达式
	 * 
	 * @param field 模版列信息
	 * @return 列类型表达式
	 */
	String columnType(TemplateField field);
	
	/**
	 * 创建表 SQL 语句
	 * 
	 * @param fieldInfoMap 模版列信息集合
	 * @param tableName 表名
	 * @return SQL 语句
	 */
	String buildCreateSQL(Map<String, TemplateField> fieldInfoMap, String tableName);
	
	/**
	 * 删除表 SQL 语句
	 * 
	 * @param tableName 表名
	 * @return SQL 语句
	 */
	String buildDropSQL(String tableName);
	
	/**
	 * 添加列 SQL 语句
	 * 
	 * @param field 模版列信息
	 * @param tableName 表名
	 * @param columnName 列名
	 * @return SQL 语句
	 */
	String buildAlterAddSQL(TemplateField field, String tableName, String columnName);
	
	/**
	 * 修改列 SQL 语句
	 * 
	 * @param field 模版列信息
	 * @param tableName 表名
	 * @param columnName 列名
	 * @return SQL 语句
	 */
	String buildAlterModifySQL(TemplateField field, String tableName, String columnName);
	
	/**
	 * 删除列 SQL 语句
	 * 
	 * @param tableName 表名
	 * @param columnName 列名
	 * @return SQL 语句
	 */
	String buildAlterDropSQL(String tableName, String columnName);
	
	/**
	 * 创建索引 SQL 语句
	 * 
	 * @param tableName 表名
	 * @param columnName 列名
	 * @param indexName 索引名
	 * @param unique 是否为唯一索引
	 * @return SQL 语句
	 */
	String buildCreateIndexSQL(String tableName, String columnName, String indexName, boolean unique);
	
	/**
	 * 删除索引 SQL 语句
	 * 
	 * @param tableName 表名
	 * @param indexName 列名
	 * @return SQL 语句
	 */
	String buildDropIndexSQL(String tableName, String indexName);
	
	/**
	 * 包装分页 SQL 语句
	 * 
	 * @param sql SQL 语句
	 * @param pageable 分页信息
	 */
	void wrapPageSQL(StringBuilder sql, Pageable pageable);
	
}
