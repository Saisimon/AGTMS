package net.saisimon.agtms.core.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 选择器选项实体对象
 * 
 * @author saisimon
 *
 */
@Data
@Entity
@Table(name="agtms_selection_option")
public class SelectionOption {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 选择器选项值
	 */
	@Column(length=50)
	private String value;
	
	/**
	 * 选择器选项名称
	 */
	@Column(length=50)
	private String text;
	
	/**
	 * 选择器ID
	 */
	@Column(columnDefinition="BIGINT(15) NOT NULL COMMENT '选择器ID'")
	private Long selectionId;
	
}
