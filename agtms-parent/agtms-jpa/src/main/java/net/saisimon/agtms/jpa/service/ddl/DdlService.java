package net.saisimon.agtms.jpa.service.ddl;

import net.saisimon.agtms.core.domain.Template;

/**
 * DDL 服务
 * 
 * @author saisimon
 *
 */
public interface DdlService {
	
	/**
	 * 根据模板创建表结构
	 * 
	 * @param template 模板对象
	 */
	boolean createTable(Template template);
	
	/**
	 * 根据模板的变化修改表结构
	 * 
	 * @param template 新模板对象
	 * @param oldTemplate 老模板对象
	 */
	boolean alterTable(Template template, Template oldTemplate);
	
	/**
	 * 根据模板删除表结构
	 * 
	 * @param template 模板对象
	 */
	boolean dropTable(Template template);
	
}
