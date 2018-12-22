package net.saisimon.agtms.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name="agtms_navigation")
@Document(collection="agtms_navigation")
public class Navigation {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, length=50)
	private String title;
	
	@Column(length=50)
	private String icon;
	
	@Column(nullable=false, columnDefinition="int(11) default 0")
	private Integer priority;
	
	@Column(nullable=false, columnDefinition="timestamp default current_timestamp")
	private String createTime;
	
	@Column(nullable=false, columnDefinition="timestamp default current_timestamp")
	private String updateTime;
	
	@JsonIgnore
	@Column(nullable=false, columnDefinition="bigint(20)")
	private Long belong;
	
	@Column(columnDefinition="bigint(20) default -1")
	private Long parentId;
	
}
