package net.saisimon.agtms.core.order;

import org.springframework.core.Ordered;

public abstract class AbstractOrder implements Ordered {

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
