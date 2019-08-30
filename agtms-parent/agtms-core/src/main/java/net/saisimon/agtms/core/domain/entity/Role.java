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
 * 角色实体对象
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
@ToString
@Entity
@Table(name=Role.TABLE_NAME)
@Document(collection=Role.TABLE_NAME)
public class Role implements Serializable {

	private static final long serialVersionUID = 3881875049453844438L;

	public static final String TABLE_NAME = "agtms_role";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 名称
	 */
	@Column(length=64, nullable=false, unique=true)
	private String name;
	
	/**
	 * 备注
	 */
	@Column(length=512)
	private String remark;
	
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
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
		Role other = (Role) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
}
