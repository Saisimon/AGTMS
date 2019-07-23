package net.saisimon.agtms.web.config.handler.field;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Views;

@Component
public class IconFieldHandler extends AbstractFieldHandler {
	
	@Override
	public Views key() {
		return Views.ICON;
	}

}
