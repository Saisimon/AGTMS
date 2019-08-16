package net.saisimon.agtms.web.dto.req;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.core.dto.TaskParam;

@Data
@EqualsAndHashCode(callSuper=false)
public class ImportParam extends TaskParam {
	
	private String importFileName;
	
	private List<String> importFields;
	
	private String importFileType;
	
}
