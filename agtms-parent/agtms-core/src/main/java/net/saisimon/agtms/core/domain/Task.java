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

@Data
@Entity
@Table(name="agtms_task")
@Document(collection="agtms_task")
public class Task {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=32, nullable=false)
	private String taskType;
	
	@Column(columnDefinition="timestamp default current_timestamp")
	private Date taskTime;
	
	@Column(length=512)
	private String taskParam;
	
	@Column(columnDefinition="bigint(11) not null")
	private Integer handleStatus;
	
	@Column(columnDefinition="timestamp default current_timestamp")
	private Date handleTime;
	
	@Column(length=512)
	private String handleResult;
	
	@Column(columnDefinition="bigint(15) not null")
	private Long operatorId;
	
}
