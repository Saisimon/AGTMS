package net.saisimon.agtms.redis.order;

import net.saisimon.agtms.core.order.AbstractOrder;

public abstract class AbstractRedisOrder extends AbstractOrder {
	
	@Override
	public int getOrder() {
		return 0;
	}
	
}
