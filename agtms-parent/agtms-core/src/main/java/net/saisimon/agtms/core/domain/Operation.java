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
@Table(name="agtms_operation")
@Document(collection="agtms_operation")
public class Operation {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(columnDefinition="bigint(15) not null")
	private Long operatorId;
	
	@Column(columnDefinition="bigint(11) not null")
	private Integer operateType;
	
	@Column(columnDefinition="timestamp default current_timestamp")
	private Date operateTime;
	
}
