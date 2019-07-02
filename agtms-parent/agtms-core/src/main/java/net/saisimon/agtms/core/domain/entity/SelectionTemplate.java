package net.saisimon.agtms.core.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * 下拉列表选项实体对象
 * 
 * @author saisimon
 *
 */
@Data
@Entity
@Table(name=SelectionTemplate.TABLE_NAME)
@Document(collection=SelectionTemplate.TABLE_NAME)
public class SelectionTemplate implements Serializable {
	
	private static final long serialVersionUID = -6549055157762843411L;
	
	public static final String TABLE_NAME = "agtms_selection_template";

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 下拉列表对应模板ID
	 */
	@Column
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
	@Column
	private Long selectionId;
	
}
