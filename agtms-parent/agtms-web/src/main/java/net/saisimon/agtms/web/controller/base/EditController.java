package net.saisimon.agtms.web.controller.base;

import java.util.List;

import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.EditGrid;
import net.saisimon.agtms.core.domain.grid.Field;

public abstract class EditController extends BaseController {
	
	protected EditGrid getEditGrid(Long id, Object key) {
		EditGrid editGrid = new EditGrid();
		editGrid.setBreadcrumbs(breadcrumbs(key));
		editGrid.setFields(fields(id, key));
		return editGrid;
	}
	
	protected abstract List<Breadcrumb> breadcrumbs(Object key);
	
	protected abstract List<Field<?>> fields(Long id, Object key);
	
}
