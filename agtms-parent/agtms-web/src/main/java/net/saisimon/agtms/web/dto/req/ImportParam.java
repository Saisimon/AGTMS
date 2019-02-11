package net.saisimon.agtms.web.dto.req;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class ImportParam {
	
	private Long templateId;
	
	private Long userId;
	
	@NotEmpty
	private List<String> importFields;
	
	@NotBlank
	private String importFileType;
	
}
