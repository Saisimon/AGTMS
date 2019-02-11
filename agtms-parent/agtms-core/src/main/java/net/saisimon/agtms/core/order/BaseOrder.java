package net.saisimon.agtms.core.order;

import org.springframework.core.Ordered;

public interface BaseOrder extends Ordered {

	@Override
	default int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
