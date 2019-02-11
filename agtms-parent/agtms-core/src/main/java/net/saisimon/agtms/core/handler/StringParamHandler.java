package net.saisimon.agtms.core.handler;

import org.springframework.stereotype.Component;

@Component
public class StringParamHandler implements ParamHandler<String> {

	@Override
	public String handler(Object param) {
		return param == null ? null : param.toString();
	}

}
