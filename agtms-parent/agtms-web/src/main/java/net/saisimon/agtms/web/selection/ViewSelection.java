package net.saisimon.agtms.web.selection;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 展现类型下拉列表
 * 
 * @author saisimon
 *
 */
@Component
public class ViewSelection extends AbstractSelection<String> {

	@Override
	public Map<String, String> select() {
		Views[] vs = Views.values();
		Map<String, String> viewMap = MapUtil.newHashMap(vs.length, true);
		for (Views v : vs) {
			viewMap.put(v.getView(), getMessage(SystemUtils.humpToCode(v.getView(), ".")));
		}
		return viewMap;
	}
	
}
