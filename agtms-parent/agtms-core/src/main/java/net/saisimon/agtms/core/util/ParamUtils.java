package net.saisimon.agtms.core.util;

import org.springframework.util.Assert;

import net.saisimon.agtms.core.factory.ParamHandlerFactory;
import net.saisimon.agtms.core.handler.ParamHandler;

public class ParamUtils {
	
	private ParamUtils() {
		throw new IllegalAccessError();
	}
	
	public static <T> T handler(Class<T> paramClass, Object param) {
		ParamHandler<T> handler = ParamHandlerFactory.getHandler(paramClass);
		Assert.notNull(handler, paramClass.getName() + " no ParamHandler");
		return handler.handler(param);
	}
	
}
