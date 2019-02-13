package net.saisimon.agtms.core.domain.generate;

import java.util.Date;

/**
 * 生成自定义对象的基础对象
 * 
 * @author saisimon
 *
 */
public class Generate {
	
	/**
	 * 创建人员ID
	 */
	private Long operatorId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	public Long getOperatorId() {
		return operatorId;
	}
	
	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
