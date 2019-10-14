package net.saisimon.agtms.web.dto.req;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.core.dto.BaseTaskParam;

@Data
@EqualsAndHashCode(callSuper=false)
public class ImportParam extends BaseTaskParam {
	
	private String importFileName;
	
	private List<String> importFields;
	
	private String importFileType;
	
}
