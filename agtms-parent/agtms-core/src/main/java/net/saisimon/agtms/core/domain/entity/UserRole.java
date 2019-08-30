package net.saisimon.agtms.core.domain.entity;

import java.io.Serializable;

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
 * 用户角色关联对象
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
@ToString
@Entity
@Table(name=UserRole.TABLE_NAME)
@Document(collection=UserRole.TABLE_NAME)
public class UserRole implements Serializable {

	private static final long serialVersionUID = 3981375664706115047L;

	public static final String TABLE_NAME = "agtms_user_role";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 用户ID
	 */
	@Column
	private Long userId;
	
	/**
	 * 角色ID
	 */
	@Column
	private Long roleId;
	
	/**
	 * 角色路径
	 */
	@Column
	private String rolePath;
	
}
