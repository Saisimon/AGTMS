package net.saisimon.agtms.core.enums;

public enum Views {
	
	HTML("html"),
	TEXT("text"), 
	TEXTAREA("textarea"), 
	ICON("icon"), 
	IMAGE("image"), 
	LINK("link"), 
	PASSWORD("password"), 
	EMAIL("email"), 
	PHONE("phone");
	
	private String view;
	
	Views(String view) {
		this.view = view;
	}
	
	public String getView() {
		return view;
	}
	
}
