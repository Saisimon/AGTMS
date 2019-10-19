package net.saisimon.agtms.web.dto.req;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 下拉列表参数对象
 * 
 * @author saisimon
 *
 */
@Data
public class SelectionParam {
	
	private Long id;
	
	@NotBlank
	private String title;
	
	@NotNull
	private Integer type;
	
	private List<SelectionOptionParam> options;
	
	private SelectionTemplateParam template;
	
	@Data
	public static class SelectionOptionParam {
		
		private String value;
		
		private String text;
		
	}
	
	@Data
	public static class SelectionTemplateParam {
		
		private Long id;
		
		private String value;
		
		private String text;
		
	}
	
}
