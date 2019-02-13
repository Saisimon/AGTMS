package net.saisimon.agtms.core.order;

import org.springframework.core.Ordered;

/**
 * 基础排序接口
 * 
 * @author saisimon
 *
 */
public interface BaseOrder extends Ordered {

	@Override
	default int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
