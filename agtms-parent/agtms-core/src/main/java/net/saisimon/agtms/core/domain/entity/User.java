package net.saisimon.agtms.core.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户实体对象
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
@ToString(exclude={"password", "salt", "remark"})
@Entity
@Table(name="agtms_user")
@Document(collection="agtms_user")
public class User implements Serializable {
	
	private static final long serialVersionUID = 7086947167059509219L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 登陆名
	 */
	@Indexed
	@Column(length=50, nullable=false, unique=true)
	private String loginName;
	
	/**
	 * 盐
	 */
	@Column(length=10, nullable=false)
	private String salt;
	
	/**
	 * 密码（密文）
	 */
	@JsonIgnore
	@Column(length=128, nullable=false)
	private String password;
	
	/**
	 * 昵称
	 */
	@Column(length=50)
	private String nickname;
	
	/**
	 * 联系方式
	 */
	@Column(length=20)
	private String cellphone;
	
	/**
	 * 电子邮箱
	 */
	@Column(length=50)
	private String email;
	
	/**
	 * 头像
	 */
	@Column(length=200)
	private String avatar;
	
	/**
	 * 用户创建时间
	 */
	@Column
	private Date createTime;
	
	/**
	 * 用户更新时间
	 */
	@Column
	private Date updateTime;
	
	/**
	 * 备注
	 */
	@Column(length=500)
	private String remark;
	
	/**
	 * 上次登陆时间
	 */
	@Column
	private Date lastLoginTime;
	
	/**
	 * 管理员
	 */
	@Column
	private boolean admin;
	
	/**
	 * 用户状态
	 */
	@Column
	private Integer status;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((loginName == null) ? 0 : loginName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (loginName == null) {
			if (other.loginName != null)
				return false;
		} else if (!loginName.equals(other.loginName))
			return false;
		return true;
	}
	
}
