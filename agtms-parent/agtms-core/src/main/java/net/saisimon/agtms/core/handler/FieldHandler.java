package net.saisimon.agtms.core.handler;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.order.BaseOrder;

/**
 * 属性字段处理接口
 * 
 * @author saisimon
 *
 */
public interface FieldHandler extends BaseOrder {
	
	/**
	 * 验证属性值是否符合要求
	 * 
	 * @param template 模板对象
	 * @param field 属性对象
	 * @param value 属性值
	 * @return 验证结果
	 */
	Result validate(Template template, TemplateField field, Object value);
	
	/**
	 * 数据脱敏
	 * 
	 * @param value 属性值
	 * @return 数据脱敏后属性值
	 */
	Object masking(Object value);
	
	/**
	 * 属性字段处理器对应的展现类型
	 * 
	 * @return 展现类型数组
	 */
	Views key();
	
}
