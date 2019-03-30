package net.saisimon.agtms.core.enums;

/**
 * 导入导出支持的文件类型枚举
 * 
 * @author saisimon
 *
 */
public enum FileTypes {
	
	/**
	 * Excel xls类型文件
	 */
	XLS("xls"),
	/**
	 * Excel xlsx类型文件
	 */
	XLSX("xlsx"),
	/**
	 * Excel csv类型文件
	 */
	CSV("csv");
	
	private String type;
	
	FileTypes(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
}
