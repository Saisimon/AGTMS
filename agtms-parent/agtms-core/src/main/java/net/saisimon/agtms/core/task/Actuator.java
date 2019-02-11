package net.saisimon.agtms.core.task;

import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.util.SystemUtils;

public interface Actuator<P> {
	
	Result execute(P param) throws Exception;
	
	Sign sign();
	
	@SuppressWarnings("unchecked")
	default Class<P> getParamClass() {
		return (Class<P>) SystemUtils.getInterfaceGenericClass(getClass(), Actuator.class, 0);
	}
	
}
