package net.saisimon.agtms.web.controller.base;

import java.util.List;

import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.EditGrid;
import net.saisimon.agtms.core.domain.grid.Field;

/**
 * 编辑页面抽象控制器
 * 
 * @author saisimon
 *
 */
public abstract class AbstractEditController<T> extends BaseController {
	
	/**
	 * 前端编辑相关信息的框架
	 * 
	 * @param id 
	 * @param key 关键词
	 * @return 编辑相关信息
	 */
	protected EditGrid getEditGrid(T entity, Object key) {
		EditGrid editGrid = new EditGrid();
		editGrid.setBreadcrumbs(breadcrumbs(entity, key));
		editGrid.setFields(fields(entity, key));
		return editGrid;
	}
	
	/**
	 * 前端面包屑导航配置
	 * 
	 * @param key 关键词
	 * @return 面包屑导航
	 */
	protected abstract List<Breadcrumb> breadcrumbs(T entity, Object key);
	
	/**
	 * 前端编辑字段信息配置
	 * 
	 * @param id 
	 * @param key 关键词
	 * @return 编辑字段信息
	 */
	protected abstract List<Field<?>> fields(T entity, Object key);
	
}
