package net.saisimon.agtms.web.dto.req;

import java.util.List;

import lombok.Data;

@Data
public class ImportParam {
	
	private Object templateId;
	
	private Long userId;
	
	private String importFileName;
	
	private List<String> importFields;
	
	private String importFileType;
	
	private String importFileUUID;
	
}
