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
 * 角色资源关联对象
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
@ToString
@Entity
@Table(name=RoleResource.TABLE_NAME)
@Document(collection=RoleResource.TABLE_NAME)
public class RoleResource implements Serializable {

	private static final long serialVersionUID = -7155157438519382524L;

	public static final String TABLE_NAME = "agtms_role_resource";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 角色ID
	 */
	@Column
	private Long roleId;
	
	/**
	 * 资源ID
	 */
	@Column
	private Long resourceId;
	
}
