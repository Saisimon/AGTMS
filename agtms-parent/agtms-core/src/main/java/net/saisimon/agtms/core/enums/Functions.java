package net.saisimon.agtms.core.enums;

/**
 * 功能类型枚举
 * 
 * @author saisimon
 *
 */
public enum Functions {
	
	/**
	 * 创建
	 */
	CREATE(1, "create"), 				// 0000001
	/**
	 * 编辑
	 */
	EDIT(2, "edit"), 					// 0000010
	/**
	 * 批量编辑
	 */
	BATCH_EDIT(4, "batchEdit"), 		// 0000100
	/**
	 * 删除
	 */
	REMOVE(8, "remove"), 				// 0001000
	/**
	 * 批量删除
	 */
	BATCH_REMOVE(16, "batchRemove"), 	// 0010000
	/**
	 * 批量导出
	 */
	EXPORT(32, "export"), 				// 0100000
	/**
	 * 批量导入
	 */
	IMPORT(64, "import"); 				// 1000000
	
	/**
	 * 功能代码
	 */
	private Integer code;
	/**
	 * 功能名称
	 */
	private String function;
	
	Functions(Integer code, String function) {
		this.code = code;
		this.function = function;
	}
	
	public String getFunction() {
		return function;
	}
	
	public Integer getCode() {
		return code;
	}
	
}
