package net.saisimon.agtms.web.config.handler.field;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Views;

/**
 * 图片属性字段处理实现类
 * 
 * @author saisimon
 *
 */
@Component
public class ImageFieldHandler extends AbstractFieldHandler {
	
	@Override
	public Views view() {
		return Views.IMAGE;
	}

}
