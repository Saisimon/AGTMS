package net.saisimon.agtms.core.enums;

/**
 * 导入导出支持的文件类型枚举
 * 
 * @author saisimon
 *
 */
public enum FileTypes {
	
	XLS("xls"),
	XLSX("xlsx"),
	CSV("csv");
	
	private String type;
	
	FileTypes(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
}
