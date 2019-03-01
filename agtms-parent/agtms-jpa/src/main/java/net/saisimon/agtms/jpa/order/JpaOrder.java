package net.saisimon.agtms.jpa.order;

import net.saisimon.agtms.core.order.BaseOrder;

public interface JpaOrder extends BaseOrder {

	@Override
	default int getOrder() {
		return 0;
	}

}
