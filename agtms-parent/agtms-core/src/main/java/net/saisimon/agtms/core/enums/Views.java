package net.saisimon.agtms.core.enums;

/**
 * 模版列属性展现类型枚举
 * 
 * @author saisimon
 *
 */
public enum Views {
	
	TEXT("text"), 
	TEXTAREA("textarea"), 
	ICON("icon"), 
	IMAGE("image"), 
	LINK("link"), 
	EMAIL("email"), 
	PHONE("phone"), 
	PASSWORD("password");
	
	private String view;
	
	Views(String view) {
		this.view = view;
	}
	
	public String getView() {
		return view;
	}
	
}
