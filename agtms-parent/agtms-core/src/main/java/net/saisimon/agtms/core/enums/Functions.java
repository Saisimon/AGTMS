package net.saisimon.agtms.core.enums;

/**
 * 功能类型枚举
 * 
 * @author saisimon
 *
 */
public enum Functions {
	
	/**
	 * 创建(0000001)
	 */
	CREATE(1, "create"),
	/**
	 * 编辑(0000010)
	 */
	EDIT(2, "edit"),
	/**
	 * 批量编辑(0000100)
	 */
	BATCH_EDIT(4, "batchEdit"),
	/**
	 * 删除(0001000)
	 */
	REMOVE(8, "remove"),
	/**
	 * 批量删除(0010000)
	 */
	BATCH_REMOVE(16, "batchRemove"),
	/**
	 * 批量导出(0100000)
	 */
	EXPORT(32, "export"),
	/**
	 * 批量导入(1000000)
	 */
	IMPORT(64, "import");
	
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
