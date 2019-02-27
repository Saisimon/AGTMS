package net.saisimon.agtms.web.dto.req;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class ExportParam {
	
	private Object templateId;
	
	private Long userId;
	
	@NotEmpty
	private List<String> exportFields;
	
	@NotBlank
	private String exportFileType;
	
	private String exportFileName;
	
	private String exportFileUUID;
	
	private Map<String, Object> filter;
	
}
