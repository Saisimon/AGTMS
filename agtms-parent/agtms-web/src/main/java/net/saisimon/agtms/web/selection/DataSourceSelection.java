package net.saisimon.agtms.web.selection;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;

@Component
public class DataSourceSelection extends AbstractSelection<String> {

	@Override
	public Map<String, String> select() {
		List<Sign> signs = GenerateServiceFactory.getSigns();
		Map<String, String> dataSourceMap = MapUtil.newHashMap(signs.size(), true);
		for (Sign sign : signs) {
			if ("remote".equals(sign.getName())) {
				continue;
			}
			dataSourceMap.put(sign.getName(), sign.getText() == null ? sign.getName() : sign.getText());
		}
		return dataSourceMap;
	}

}
