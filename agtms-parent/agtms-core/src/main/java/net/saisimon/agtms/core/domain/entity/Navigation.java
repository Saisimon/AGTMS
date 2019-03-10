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
 * 导航实体对象
 * 
 * @author saisimon
 *
 */
@Data
@Entity
@Table(name="agtms_navigation")
@Document(collection="agtms_navigation")
public class Navigation {
	
	/**
	 * 导航默认图标
	 */
	public static final String DEFAULT_ICON = "list";
	/**
	 * 导航默认优先级
	 */
	public static final Long DEFAULT_PRIORITY = 0L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 导航名称
	 */
	@Column(nullable=false, length=50)
	private String title;
	
	/**
	 * 导航图标
	 */
	@Column(length=10)
	private String icon;
	
	/**
	 * 导航优先级
	 */
	@Column(nullable=false, columnDefinition="BIGINT(15) DEFAULT 0 COMMENT '导航优先级'")
	private Long priority;
	
	/**
	 * 导航创建时间
	 */
	@Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '导航创建时间'")
	private Date createTime;
	
	/**
	 * 导航更新时间
	 */
	@Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '导航更新时间'")
	private Date updateTime;
	
	/**
	 * 导航创建人员ID
	 */
	@Column(nullable=false, columnDefinition="BIGINT(15) COMMENT '导航创建人员ID'")
	private Long operatorId;
	
	/**
	 * 父导航ID
	 */
	@Column(columnDefinition="BIGINT(15) DEFAULT -1 COMMENT '父导航ID'")
	private Long parentId;
	
}
