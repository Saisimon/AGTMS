package net.saisimon.agtms.core.handler;

import org.springframework.stereotype.Component;

@Component
public class ObjectParamHandler implements ParamHandler<Object> {

	@Override
	public Object handler(Object param) {
		return param;
	}

}
