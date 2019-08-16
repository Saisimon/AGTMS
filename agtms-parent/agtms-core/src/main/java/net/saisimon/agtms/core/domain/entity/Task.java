package net.saisimon.agtms.core.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Table(name=Task.TABLE_NAME)
@Document(collection=Task.TABLE_NAME)
public class Task implements Serializable {
	
	private static final long serialVersionUID = 3283171487096285213L;
	
	public static final String TABLE_NAME = "agtms_task";

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
	@Column
	private Date taskTime;
	
	/**
	 * 任务参数（json）
	 */
	@Lob
	@Column
	private String taskParam;
	
	/**
	 * 任务处理状态
	 * 
	 * @see net.saisimon.agtms.core.enums.HandleStatuses
	 */
	@Column
	private Integer handleStatus;
	
	/**
	 * 任务处理时间
	 */
	@Column
	private Date handleTime;
	
	/**
	 * 任务处理结果
	 */
	@Column(length=255)
	private String handleResult;
	
	/**
	 * 任务创建人员ID
	 */
	@Column(nullable=false)
	private Long operatorId;
	
	@Column(length=50)
	private String ip;
	
	@Column
	private Integer port;
	
}
