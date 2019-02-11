package net.saisimon.agtms.core.domain.grid;

import java.util.List;

import lombok.Data;

@Data
public class EditGrid {
	
	private List<Breadcrumb> breadcrumbs;
	
	private List<Field<?>> fields;
	
}
