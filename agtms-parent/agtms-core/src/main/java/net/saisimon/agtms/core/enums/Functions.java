package net.saisimon.agtms.core.enums;

/**
 * 功能类型枚举
 * 
 * @author saisimon
 *
 */
public enum Functions {
	
	/**
	 * 	查看(1)
	 */
	VIEW(1, "view"),
	/**
	 * 创建(10)
	 */
	CREATE(1 << 1, "create"),
	/**
	 * 编辑(100)
	 */
	EDIT(1 << 2, "edit"),
	/**
	 * 批量编辑(1000)
	 */
	BATCH_EDIT(1 << 3, "batchEdit"),
	/**
	 * 删除(10000)
	 */
	REMOVE(1 << 4, "remove"),
	/**
	 * 批量删除(100000)
	 */
	BATCH_REMOVE(1 << 5, "batchRemove"),
	/**
	 * 批量导出(1000000)
	 */
	EXPORT(1 << 6, "export"),
	/**
	 * 批量导入(10000000)
	 */
	IMPORT(1 << 7, "import"),
	/**
	 * 锁定(100000000)
	 */
	LOCK(1 << 8, "lock"),
	/**
	 * 解锁(1000000000)
	 */
	UNLOCK(1 << 9, "unlock"),
	/**
	 * 重置密码(10000000000)
	 */
	RESET_PASSWORD(1 << 10, "resetPassword"),
	/**
	 * 角色授权(100000000000)
	 */
	GRANT(1 << 11, "grant");
	
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
