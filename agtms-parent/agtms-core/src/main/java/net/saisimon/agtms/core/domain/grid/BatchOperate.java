package net.saisimon.agtms.core.domain.grid;

import java.util.List;

import lombok.Data;

@Data
public class BatchOperate {

	private String path;
	
	private String size = "sm";
	
	private List<Field<?>> operateFields;
	
}
