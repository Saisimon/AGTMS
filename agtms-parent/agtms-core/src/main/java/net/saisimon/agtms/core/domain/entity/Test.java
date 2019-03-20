package net.saisimon.agtms.core.domain.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Entity
@Table(name="agtms_test")
@Document(collection="agtms_test")
public class Test {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private Long column0field0;
	
	@Column
	private Double column0field1;
	
	@Column(length=500)
	private String column0field2;
	
	@Column
	private Date column0field3;
	
	@Column(nullable=false)
	private Long column1field0;
	
	@Column(nullable=false)
	private Double column1field1;
	
	@Column(nullable=false, length=500)
	private String column1field2;
	
	@Column(nullable=false)
	private Date column1field3;
	
	@Column
	private Date createTime;
	
	@Column
	private Date updateTime;
	
	@Column(nullable=false)
	private Long operatorId;
	
}
