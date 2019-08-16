package net.saisimon.agtms.web.dto.req;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.core.dto.TaskParam;

@Data
@EqualsAndHashCode(callSuper=false)
public class ExportParam extends TaskParam {
	
	@NotEmpty
	private List<String> exportFields;
	
	@NotBlank
	private String exportFileType;
	
	private String exportFileName;
	
	private Map<String, Object> filter;
	
}
