package net.saisimon.agtms.core.domain.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * 模板实体对象
 * 
 * @author saisimon
 *
 */
@Data
@Entity
@Table(name="agtms_template")
@Document(collection="agtms_template")
public class Template implements Cloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 导航ID
	 */
	@Column(columnDefinition="BIGINT(15) DEFAULT -1 COMMENT '导航ID'")
	private Long navigationId;
	
	/**
	 * 模板标题
	 */
	@Column(length=50, nullable=false)
	private String title;
	
	/**
	 * 模板支持的功能
	 */
	@Column(columnDefinition="INT(11) COMMENT '模板支持的功能'")
	private Integer function;
	
	/**
	 * 模板下属的列信息
	 */
	@OneToMany(targetEntity=TemplateColumn.class, fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)
	@JoinColumn(name="template_id")
	private Set<TemplateColumn> columns;
	
	/**
	 * 模板下属的列下一个下标
	 */
	@Column(columnDefinition="INT(11) COMMENT '模板下属的列下一个下标'")
	private Integer columnIndex;
	
	/**
	 * 模板创建时间
	 */
	@Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '模板创建时间'")
	private Date createTime;
	
	/**
	 * 模板更新时间
	 */
	@Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '模板更新时间'")
	private Date updateTime;
	
	/**
	 * 模板创建人员ID
	 */
	@Column(columnDefinition="BIGINT(15) NOT NULL COMMENT '模板创建人员ID'")
	private Long operatorId;
	
	/**
	 * 模板数据来源
	 */
	@Column(length=50, nullable=false)
	private String source;
	
	@Transient
	private String service;
	
	@Transient
	private String key;
	
	/**
	 * 模板列实体对象
	 * 
	 * @author saisimon
	 *
	 */
	@Data
	@Entity
	@Table(name="agtms_template_column")
	public static class TemplateColumn implements Cloneable {
		
		@Id
		@GeneratedValue(strategy=GenerationType.IDENTITY)
		private Long id;
		
		/**
		 * 列名
		 */
		@Column(length=20, nullable=false)
		private String columnName;
		
		/**
		 * 列显示名
		 */
		@Column(length=50, nullable=false)
		private String title;
		
		/**
		 * 列下属的属性信息
		 */
		@OneToMany(targetEntity=TemplateField.class, fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)
		@JoinColumn(name="template_column_id")
		private Set<TemplateField> fields;
		
		/**
		 * 列顺序值
		 */
		@Column(columnDefinition="INT(11)")
		private Integer ordered;
		
		/**
		 * 列下属的属性下一个下标
		 */
		@Column(columnDefinition="INT(11) COMMENT '列下属的属性下一个下标'")
		private Integer fieldIndex;
		
	}
	
	/**
	 * 模板列属性实体对象
	 * 
	 * @author saisimon
	 *
	 */
	@Data
	@Entity
	@Table(name="agtms_template_field")
	public static class TemplateField implements Cloneable {
		
		@Id
		@GeneratedValue(strategy=GenerationType.IDENTITY)
		private Long id;
		
		/**
		 * 属性名
		 */
		@Column(length=20, nullable=false)
		private String fieldName;
		
		/**
		 * 属性类型
		 * 
		 * @see net.saisimon.agtms.core.enums.Classes
		 */
		@Column(length=20, nullable=false)
		private String fieldType;
		
		/**
		 * 属性显示名
		 */
		@Column(length=50, nullable=false)
		private String fieldTitle;
		
		/**
		 * 属性展现类型
		 * 
		 * @see net.saisimon.agtms.core.enums.Views
		 */
		@Column(length=20)
		private String view;
		
		/**
		 * 下拉列表标识
		 */
		@Column(length=50)
		private String selection;
		
		/**
		 * 是否支持过滤
		 */
		@Column(columnDefinition="TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否支持过滤'")
		private Boolean filter;
		
		/**
		 * 是否支持排序
		 */
		@Column(columnDefinition="TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否支持排序'")
		private Boolean sorted;
		
		/**
		 * 是否必填
		 */
		@Column(columnDefinition="TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否必填'")
		private Boolean required;
		
		/**
		 * 是否唯一
		 */
		@Column(columnDefinition="TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否唯一'")
		private Boolean uniqued;
		
		/**
		 * 列表页是否隐藏
		 */
		@Column(columnDefinition="TINYINT(1) NOT NULL DEFAULT 0 COMMENT '列表页是否隐藏'")
		private Boolean hidden;
		
		/**
		 * 默认值
		 */
		@Column(length=20)
		private String defaultValue;
		
		/**
		 * 宽度
		 */
		@Column(columnDefinition="INT(5) COMMENT '宽度'")
		private Integer width;
		
		/**
		 * 属性顺序值
		 */
		@Column(columnDefinition="INT(11) COMMENT '属性顺序值'")
		private Integer ordered;
		
		/**
		 * 模板属性下拉列表的唯一标识
		 * 
		 * @return 唯一标识
		 */
		public String selectionSign(String service) {
			if (selection == null) {
				return null;
			}
			if (service != null) {
				return service + "-" + selection;
			} else {
				return selection;
			}
		}
		
	}
	
	/**
	 * 模板的唯一标识
	 * 
	 * @return 唯一标识
	 */
	public String sign() {
		if (id != null) {
			return id.toString();
		}
		if (service != null && key != null) {
			return service + "-" + key;
		}
		return null;
	}
	
}
