package net.saisimon.agtms.core.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Entity
@Table(name="agtms_template")
@Document(collection="agtms_template")
public class Template implements Cloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(columnDefinition="bigint(15)")
	private Long navigationId;
	
	@Column(length=50, nullable=false)
	private String title;
	
	@Column(columnDefinition="bigint(11)")
	private Integer function;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="template_id")
	private List<TemplateColumn> columns;
	
	@Column(columnDefinition="bigint(11)")
	private Integer columnIndex;
	
	@Column(columnDefinition="timestamp default current_timestamp")
	private Date createTime;
	
	@Column(columnDefinition="timestamp default current_timestamp")
	private Date updateTime;
	
	@Column(columnDefinition="bigint(15) not null")
	private Long operatorId;
	
	@Column(length=50, nullable=false)
	private String source;
	
	@Column(length=500)
	private String sourceUrl;
	
	@Data
	@Entity
	@Table(name="agtms_template_column")
	@Document
	public static class TemplateColumn implements Cloneable {
		
		@Id
		@GeneratedValue(strategy=GenerationType.IDENTITY)
		private Long id;
		
		@Column(length=20, nullable=false)
		private String columnName;
		
		@Column(length=50, nullable=false)
		private String title;
		
		@OneToMany(cascade=CascadeType.ALL)
		@JoinColumn(name="template_column_id")
		private List<TemplateField> fields;
		
		@Column(columnDefinition="bigint(11)")
		private Integer ordered;
		
		@Column(columnDefinition="bigint(11)")
		private Integer fieldIndex;

	}
	
	@Data
	@Entity
	@Table(name="agtms_template_field")
	@Document
	public static class TemplateField implements Cloneable {
		
		@Id
		@GeneratedValue(strategy=GenerationType.IDENTITY)
		private Long id;
		
		@Column(length=20, nullable=false)
		private String fieldName;
		
		@Column(length=20, nullable=false)
		private String fieldType;
		
		@Column(length=50, nullable=false)
		private String fieldTitle;
		
		@Column(length=20)
		private String view;
		
		@Column(columnDefinition="tinyint(1) not null DEFAULT '0'")
		private Boolean filter;
		
		@Column(columnDefinition="tinyint(1) not null DEFAULT '0'")
		private Boolean sorted;
		
		@Column(columnDefinition="tinyint(1) not null DEFAULT '0'")
		private Boolean required;
		
		@Column(columnDefinition="tinyint(1) not null DEFAULT '0'")
		private Boolean uniqued;
		
		@Column(columnDefinition="tinyint(1) not null DEFAULT '0'")
		private Boolean hidden;
		
		@Column(length=20)
		private String defaultValue;
		
		@Column(columnDefinition="int(5)")
		private Integer width;
		
		@Column(columnDefinition="bigint(11)")
		private Integer ordered;

	}
	
}
