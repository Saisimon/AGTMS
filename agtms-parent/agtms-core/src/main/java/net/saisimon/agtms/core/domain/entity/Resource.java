package net.saisimon.agtms.core.domain.entity;

import java.io.Serializable;
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

/**
 * 资源实体对象
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
@ToString
@Entity
@Table(name=Resource.TABLE_NAME)
@Document(collection=Resource.TABLE_NAME)
public class Resource implements Serializable {
	
	private static final long serialVersionUID = 2682933292389934017L;

	public static final String TABLE_NAME = "agtms_resource";
	
	/**
	 * 默认图标
	 */
	public static final String DEFAULT_ICON = "list";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 名称
	 */
	@Column(nullable=false, length=32)
	private String name;
	
	/**
	 * 图标
	 */
	@Column(length=64)
	private String icon;
	
	/**
	 * 对应前端链接
	 */
	@Column
	private String link;
	
	/**
	 * 创建时间
	 */
	@Column
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	@Column
	private Date updateTime;
	
	/**
	 * 创建人员ID
	 */
	@Column(nullable=false)
	private Long operatorId;
	
	/**
	 * 路径
	 */
	@Column
	private String path;
	
	/**
	 * 功能值
	 */
	@Column
	private Integer functions;
	
	/**
	 * 资源对应内容ID
	 */
	@Column
	private Long contentId;
	
	/**
	 * 资源对应内容类型
	 */
	@Column
	private Integer contentType;
	
	
	/**
	 * 资源对应内容类型枚举
	 * 
	 * @author saisimon
	 *
	 */
	@Getter
	public static enum ContentType {
		
		/**
		 * 导航
		 */
		NAVIGATION(1, "navigation"),
		/**
		 * 模板
		 */
		TEMPLATE(2, "template");
		
		private Integer value;
		
		private String name;
		
		ContentType(Integer value, String name) {
			this.value = value;
			this.name = name;
		}
		
	}
	
}
