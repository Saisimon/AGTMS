package net.saisimon.agtms.core.domain.entity;

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
 * 任务实体对象
 * 
 * @author saisimon
 *
 */
@Data
@Entity
@Table(name="agtms_task")
@Document(collection="agtms_task")
public class Task {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 任务类型
	 * 
	 */
	@Column(length=50)
	private String taskType;
	
	/**
	 * 任务创建时间
	 */
	@Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间'")
	private Date taskTime;
	
	/**
	 * 任务参数（json）
	 */
	@Column(length=512)
	private String taskParam;
	
	/**
	 * 任务处理状态
	 * 
	 * @see net.saisimon.agtms.core.enums.HandleStatuses
	 */
	@Column(columnDefinition="INT(11) NOT NULL COMMENT '任务处理状态'")
	private Integer handleStatus;
	
	/**
	 * 任务处理时间
	 */
	@Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '任务处理时间'")
	private Date handleTime;
	
	/**
	 * 任务处理结果
	 */
	@Column(length=255)
	private String handleResult;
	
	/**
	 * 任务创建人员ID
	 */
	@Column(columnDefinition="BIGINT(15) NOT NULL COMMENT '任务创建人员ID'")
	private Long operatorId;
	
}
