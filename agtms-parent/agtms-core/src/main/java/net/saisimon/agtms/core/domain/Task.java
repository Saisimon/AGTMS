package net.saisimon.agtms.core.domain;

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
	 */
	@Column(length=32, nullable=false)
	private String taskType;
	
	/**
	 * 任务创建时间
	 */
	@Column(columnDefinition="timestamp default current_timestamp")
	private Date taskTime;
	
	/**
	 * 任务参数（json）
	 */
	@Column(length=512)
	private String taskParam;
	
	/**
	 * 任务处理状态
	 * @see net.saisimon.agtms.core.enums.HandleStatuses
	 */
	@Column(columnDefinition="int(11) not null")
	private Integer handleStatus;
	
	/**
	 * 任务处理时间
	 */
	@Column(columnDefinition="timestamp default current_timestamp")
	private Date handleTime;
	
	/**
	 * 任务处理结果
	 */
	@Column(length=512)
	private String handleResult;
	
	/**
	 * 任务创建人员ID
	 */
	@Column(columnDefinition="bigint(15) not null")
	private Long operatorId;
	
}
