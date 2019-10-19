package net.saisimon.agtms.web.selection;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.enums.NotificationTypes;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 消息通知类型下拉列表
 * 
 * @author saisimon
 *
 */
@Component
public class NotificationTypeSelection extends AbstractSelection<Integer> {
	
	@Override
	public Map<Integer, String> select() {
		NotificationTypes[] types = NotificationTypes.values();
		Map<Integer, String> typeMap = MapUtil.newHashMap(types.length, true);
		for (NotificationTypes type : types) {
			typeMap.put(type.getType(), getMessage(SystemUtils.humpToCode(type.getName(), ".")));
		}
		return typeMap;
	}
	
}
