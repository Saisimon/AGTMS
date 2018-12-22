package net.saisimon.agtms.mongodb.order;

import net.saisimon.agtms.core.order.AbstractOrder;

public abstract class AbstractMongodbOrder extends AbstractOrder {

	@Override
	public int getOrder() {
		return 0;
	}

}
