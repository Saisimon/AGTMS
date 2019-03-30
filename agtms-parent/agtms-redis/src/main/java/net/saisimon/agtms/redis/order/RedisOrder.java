package net.saisimon.agtms.redis.order;

import net.saisimon.agtms.core.order.BaseOrder;

public interface RedisOrder extends BaseOrder {
	
	@Override
	default int getOrder() {
		return -1;
	}
	
}
