package net.saisimon.agtms.core.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * 操作记录实体对象
 * 
 * @author saisimon
 *
 */
@Data
@Entity
@Table(name="agtms_operation")
@Document(collection="agtms_operation")
public class Operation implements Serializable {
	
	private static final long serialVersionUID = -7685951999912873569L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 操作人ID
	 */
	@Column
	private Long operatorId;
	
	/**
	 * 操作类型
	 */
	@Column
	private Integer operateType;
	
	/**
	 * 操作时间
	 */
	@Column
	private Date operateTime;
	
	/**
	 * 操作链接
	 */
	@Column(length=100)
	private String operateUrl;
	
	/**
	 * 操作IP
	 */
	@Column(length=50)
	private String operateIp;
	
	/**
	 * 操作状态
	 */
	@Column
	private Integer operateStatus;
	
	/**
	 * 操作内容
	 */
	@Column(length=100)
	private String operateContent;
	
}
