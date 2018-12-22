package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.DataSources;
import net.saisimon.agtms.core.enums.Selections;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class DataSourceSelection extends AbstractSelection {

	@Override
	public LinkedHashMap<String, String> select() {
		DataSources[] dss = DataSources.values();
		LinkedHashMap<String, String> dataSourceMap = new LinkedHashMap<>(dss.length);
		for (DataSources ds : dss) {
			dataSourceMap.put(ds.getSource(), getMessage(SystemUtils.humpToCode(ds.getSource(), ".")));
		}
		return dataSourceMap;
	}

	@Override
	public Selections key() {
		return Selections.DATA_SOURCE;
	}
	
}
