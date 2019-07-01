package net.saisimon.agtms.jpa.sharding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

/**
 * 分库分表策略
 * 
 * @author saisimon
 *
 */
public class DefaultShardingAlgorithm implements HintShardingAlgorithm<Long> {

	@Override
	public Collection<String> doSharding(Collection<String> availableTargetNames, HintShardingValue<Long> shardingValue) {
		int mod = (int) (shardingValue.getValues().iterator().next() % availableTargetNames.size());
		int i = 0;
		List<String> results = new ArrayList<>();
		for (String availableTargetName : availableTargetNames) {
			if (i == mod) {
				results.add(availableTargetName);
			}
			i++;
		}
		return results;
	}

}
