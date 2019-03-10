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
 * 选择器实体对象
 * 
 * @author saisimon
 *
 */
@Data
@Entity
@Table(name="agtms_selection")
@Document(collection="agtms_selection")
public class Selection {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 选择器名称
	 */
	@Column(nullable=false, length=50)
	private String title;
	
	/**
	 * 选择器创建时间
	 */
	@Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '选择器创建时间'")
	private Date createTime;
	
	/**
	 * 选择器更新时间
	 */
	@Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '选择器更新时间'")
	private Date updateTime;
	
	/**
	 * 选择器创建人员ID
	 */
	@Column(columnDefinition="BIGINT(15) NOT NULL COMMENT '选择器创建人员ID'")
	private Long operatorId;
	
	/**
	 * 选择器类型
	 * 
	 * @see net.saisimon.agtms.core.enums.SelectTypes
	 */
	@Column(columnDefinition="INT(11) NOT NULL COMMENT '选择器类型'")
	private Integer type;
	
}
