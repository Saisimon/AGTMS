package net.saisimon.agtms.core.enums;

/**
 * 模版列属性展现类型枚举
 * 
 * @author saisimon
 *
 */
public enum Views implements BaseEnum<String> {
	
	/**
	 * 文本类型
	 */
	TEXT("text", 512), 
	/**
	 * 长文本类型
	 */
	TEXTAREA("textarea", -1), 
	/**
	 * 图标类型
	 */
	ICON("icon", 64), 
	/**
	 * 图片类型
	 */
	IMAGE("image", 64), 
	/**
	 * 链接类型
	 */
	LINK("link", 1024), 
	/**
	 * 邮件类型
	 */
	EMAIL("email", 256), 
	/**
	 * 电话类型
	 */
	PHONE("phone", 32), 
	/**
	 * 密码类型
	 */
	PASSWORD("password", 32),
	/**
	 * 下拉列表类型
	 */
	SELECTION("selection", -1);
	
	private String view;
	private Integer size;
	
	Views(String view, Integer size) {
		this.view = view;
		this.size = size;
	}
	
	@Override
	public String getKey() {
		return view;
	}
	
	public Integer getSize() {
		return size;
	}
	
}
