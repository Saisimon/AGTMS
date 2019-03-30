package net.saisimon.agtms.core.domain.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * 模板实体对象
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
@Entity
@Table(name="agtms_template")
@Document(collection="agtms_template")
public class Template implements Cloneable, Serializable {
	
	private static final long serialVersionUID = 2550898797755828181L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 导航ID
	 */
	@Column
	private Long navigationId;
	
	/**
	 * 模板标题
	 */
	@Column(length=50, nullable=false)
	private String title;
	
	/**
	 * 模板支持的功能
	 */
	@Column
	private Integer functions;
	
	/**
	 * 模板下属的列信息
	 */
	@OneToMany(targetEntity=TemplateColumn.class, cascade=CascadeType.ALL, mappedBy="template", orphanRemoval=true)
	private Set<TemplateColumn> columns = new HashSet<>();
	
	/**
	 * 模板下属的列下一个下标
	 */
	@Column
	private Integer columnIndex;
	
	/**
	 * 模板创建时间
	 */
	@Column
	private Date createTime;
	
	/**
	 * 模板更新时间
	 */
	@Column
	private Date updateTime;
	
	/**
	 * 模板创建人员ID
	 */
	@Column
	private Long operatorId;
	
	/**
	 * 模板数据来源
	 */
	@Column(length=50, nullable=false)
	private String source;
	
	@Transient
	@javax.persistence.Transient
	private String service;
	
	@Transient
	@javax.persistence.Transient
	private String key;
	
	public void addColumn(TemplateColumn column) {
		if (column == null) {
			return;
		}
		this.columns.add(column);
		column.setTemplate(this);
	}
	
	public void removeColumn(TemplateColumn column) {
		if (column == null) {
			return;
		}
		this.columns.remove(column);
		column.setTemplate(null);
	}
	
	public void setColumns(Set<TemplateColumn> columns) {
		if (columns == null) {
			return;
		}
		if (this.columns == null) {
			this.columns = new HashSet<>();
		} else {
			this.columns.clear();
		}
		this.columns.addAll(columns);
	}
	
	/**
	 * 模板列实体对象
	 * 
	 * @author saisimon
	 *
	 */
	@Setter
	@Getter
	@Entity
	@Table(name="agtms_template_column")
	public static class TemplateColumn implements Cloneable, Serializable {
		
		private static final long serialVersionUID = 2196263141511063585L;

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
		@OneToMany(targetEntity=TemplateField.class, cascade=CascadeType.ALL, mappedBy="templateColumn", orphanRemoval=true)
		private Set<TemplateField> fields = new HashSet<>();
		
		/**
		 * 列顺序值
		 */
		@Column
		private Integer ordered;
		
		/**
		 * 列下属的属性下一个下标
		 */
		@Column
		private Integer fieldIndex;
		
		@Transient
		@JsonIgnore
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "template_id", nullable=false)
		private Template template;
		
		public void setFields(Set<TemplateField> fields) {
			if (fields == null) {
				return;
			}
			if (this.fields == null) {
				this.fields = new HashSet<>();
			} else {
				this.fields.clear();
			}
			this.fields.addAll(fields);
		}
		
		public void addField(TemplateField field) {
			if (field == null) {
				return;
			}
			this.fields.add(field);
			field.setTemplateColumn(this);
		}

		public void removeField(TemplateField field) {
			if (field == null) {
				return;
			}
			fields.remove(field);
			field.setTemplateColumn(null);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			TemplateColumn other = (TemplateColumn) obj;
			if (columnName == null) {
				if (other.columnName != null) {
					return false;
				}
			} else if (!columnName.equals(other.columnName)) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
			return result;
		}
		
	}
	
	/**
	 * 模板列属性实体对象
	 * 
	 * @author saisimon
	 *
	 */
	@Setter
	@Getter
	@Entity
	@Table(name="agtms_template_field")
	public static class TemplateField implements Cloneable, Serializable {
		
		private static final long serialVersionUID = 2985059236957444563L;

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
		private String views;
		
		/**
		 * 下拉列表标识
		 */
		@Column(length=50)
		private String selection;
		
		/**
		 * 是否支持过滤
		 */
		@Column
		private Boolean filter;
		
		/**
		 * 是否支持排序
		 */
		@Column
		private Boolean sorted;
		
		/**
		 * 是否必填
		 */
		@Column
		private Boolean required;
		
		/**
		 * 是否唯一
		 */
		@Column
		private Boolean uniqued;
		
		/**
		 * 列表页是否隐藏
		 */
		@Column
		private Boolean hidden;
		
		/**
		 * 默认值
		 */
		@Column(length=20)
		private String defaultValue;
		
		/**
		 * 属性顺序值
		 */
		@Column
		private Integer ordered;
		
		@Transient
		@JsonIgnore
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "template_column_id", nullable=false)
		private TemplateColumn templateColumn;
		
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

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			TemplateField other = (TemplateField) obj;
			if (fieldName == null) {
				if (other.fieldName != null) {
					return false;
				}
			} else if (!fieldName.equals(other.fieldName)) {
				return false;
			}
			return true;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
			return result;
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
