package net.saisimon.agtms.core.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 下拉列表选项实体对象
 * 
 * @author saisimon
 *
 */
@Data
@Entity
@Table(name="agtms_selection_template")
public class SelectionTemplate {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 下拉列表对应模板ID
	 */
	@Column(columnDefinition="BIGINT(15) COMMENT '下拉列表对应模板ID'")
	private Long templateId;
	
	/**
	 * 下拉列表选项值对应模板的属性名
	 */
	@Column(length=50)
	private String valueFieldName;
	
	/**
	 * 下拉列表选项名称对应模板的属性名
	 */
	@Column(length=50)
	private String textFieldName;
	
	/**
	 * 下拉列表ID
	 */
	@Column(columnDefinition="BIGINT(15) NOT NULL COMMENT '下拉列表ID'")
	private Long selectionId;
	
}
