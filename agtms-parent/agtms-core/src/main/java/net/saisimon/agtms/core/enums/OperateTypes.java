package net.saisimon.agtms.core.enums;

/**
 * 操作类型枚举
 * 
 * @author saisimon
 *
 */
public enum OperateTypes {
	
	/**
	 * 登出
	 */
	LOGOUT(0, "logout"),
	/**
	 * 注册
	 */
	REGISTER(10, "register"),
	/**
	 * 登入
	 */
	LOGIN(20, "login"),
	/**
	 * 查询
	 */
	QUERY(30, "query"), 
	/**
	 * 编辑
	 */
	EDIT(40, "edit"),
	/**
	 * 删除
	 */
	REMOVE(50, "remove"),
	/**
	 * 批量编辑
	 */
	BATCH_EDIT(60, "batch.edit"),
	/**
	 * 批量删除
	 */
	BATCH_REMOVE(70, "batch.remove"),
	/**
	 * 导出
	 */
	EXPORT(80, "export"),
	/**
	 * 导入
	 */
	IMPORT(90, "import");
	
	private Integer type;
	private String name;
	
	OperateTypes(Integer type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Integer getType() {
		return type;
	}
	
}
