package net.saisimon.agtms.mongodb.order;

import net.saisimon.agtms.core.order.BaseOrder;

public interface MongodbOrder extends BaseOrder {

	@Override
	default int getOrder() {
		return 0;
	}

}
