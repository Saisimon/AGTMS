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
@Table(name=SelectionOption.TABLE_NAME)
@Document(collection=SelectionOption.TABLE_NAME)
public class SelectionOption implements Serializable {
	
	private static final long serialVersionUID = 5282506945802919316L;
	
	public static final String TABLE_NAME = "agtms_selection_option";

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 下拉列表选项值
	 */
	@Column(length=50)
	private String value;
	
	/**
	 * 下拉列表选项名称
	 */
	@Column(length=50)
	private String text;
	
	/**
	 * 下拉列表ID
	 */
	@Column
	private Long selectionId;
	
}
