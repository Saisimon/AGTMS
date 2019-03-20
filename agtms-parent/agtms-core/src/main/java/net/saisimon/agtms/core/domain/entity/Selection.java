package net.saisimon.agtms.core.domain.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * 下拉列表实体对象
 * 
 * @author saisimon
 *
 */
@Data
@Entity
@Table(name="agtms_selection")
@Document(collection="agtms_selection")
public class Selection {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 下拉列表名称
	 */
	@Column(nullable=false, length=50)
	private String title;
	
	/**
	 * 下拉列表创建时间
	 */
	@Column
	private Date createTime;
	
	/**
	 * 下拉列表更新时间
	 */
	@Column
	private Date updateTime;
	
	/**
	 * 下拉列表创建人员ID
	 */
	@Column
	private Long operatorId;
	
	/**
	 * 下拉列表类型
	 * 
	 * @see net.saisimon.agtms.core.enums.SelectTypes
	 */
	@Column
	private Integer type;
	
	@Transient
	private String service;
	
	@Transient
	private String key;
	
	/**
	 * 下拉列表的唯一标识
	 * 
	 * @return 唯一标识
	 */
	public String sign() {
		if (id != null) {
			return id.toString();
		}
		if (service != null && key != null) {
			return service + "-" + key;
		}
		return null;
	}
	
}
