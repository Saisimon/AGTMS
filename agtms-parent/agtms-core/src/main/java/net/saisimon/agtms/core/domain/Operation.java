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
 * 操作记录实体对象
 * 
 * @author saisimon
 *
 */
@Data
@Entity
@Table(name="agtms_operation")
@Document(collection="agtms_operation")
public class Operation {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 操作人ID
	 */
	@Column(columnDefinition="bigint(15) not null")
	private Long operatorId;
	
	/**
	 * 操作类型
	 */
	@Column(columnDefinition="bigint(15) not null")
	private Long operateType;
	
	/**
	 * 操作时间
	 */
	@Column(columnDefinition="timestamp default current_timestamp")
	private Date operateTime;
	
}
