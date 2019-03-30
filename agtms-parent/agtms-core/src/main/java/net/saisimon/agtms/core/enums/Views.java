package net.saisimon.agtms.core.enums;

/**
 * 模版列属性展现类型枚举
 * 
 * @author saisimon
 *
 */
public enum Views {
	
	/**
	 * 文本类型
	 */
	TEXT("text"), 
	/**
	 * 长文本类型
	 */
	TEXTAREA("textarea"), 
	/**
	 * 图标类型
	 */
	ICON("icon"), 
	/**
	 * 图片类型
	 */
	IMAGE("image"), 
	/**
	 * 链接类型
	 */
	LINK("link"), 
	/**
	 * 邮件类型
	 */
	EMAIL("email"), 
	/**
	 * 电话类型
	 */
	PHONE("phone"), 
	/**
	 * 密码类型
	 */
	PASSWORD("password"),
	/**
	 * 下拉列表类型
	 */
	SELECTION("selection");
	
	private String view;
	
	Views(String view) {
		this.view = view;
	}
	
	public String getView() {
		return view;
	}
	
}
