package net.saisimon.agtms.core.enums;

/**
 * 文件类型枚举
 * 
 * @author saisimon
 *
 */
public enum FileTypes {
	
	/**
	 * Excel xls 类型文件
	 */
	XLS("xls"),
	/**
	 * Excel xlsx 类型文件
	 */
	XLSX("xlsx"),
	/**
	 * csv 类型文件
	 */
	CSV("csv"),
	/**
	 * pdf 类型文件
	 */
	PDF("pdf");
	
	private String type;
	
	FileTypes(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
}
