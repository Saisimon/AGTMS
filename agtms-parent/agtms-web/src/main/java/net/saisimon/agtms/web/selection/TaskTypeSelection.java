package net.saisimon.agtms.web.selection;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.factory.ActuatorFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;

@Component
public class TaskTypeSelection extends AbstractSelection<String> {
	
	@Override
	public Map<String, String> select() {
		List<Sign> signs = ActuatorFactory.getSigns();
		Map<String, String> dataSourceMap = MapUtil.newHashMap(signs.size(), true);
		for (Sign sign : signs) {
			dataSourceMap.put(sign.getName(), getMessage(sign.getText() == null ? sign.getName() : sign.getText()));
		}
		return dataSourceMap;
	}
	
}
