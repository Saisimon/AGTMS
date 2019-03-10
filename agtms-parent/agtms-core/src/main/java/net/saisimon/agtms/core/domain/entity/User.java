package net.saisimon.agtms.core.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
@ToString(exclude={"password", "remark"})
@Entity
@Table(name="agtms_user")
@Document(collection="agtms_user")
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 登陆名
	 */
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
	private String nickName;
	
	/**
	 * 联系方式
	 */
	@Column(length=20)
	private String cellphone;
	
	/**
	 * 电子邮箱
	 */
	@Column(length=50, nullable=false, unique=true)
	private String email;
	
	/**
	 * 创建时间
	 */
	@Column(nullable=false, columnDefinition="BIGINT(15)")
	private Long createTime;
	
	/**
	 * 更新时间
	 */
	@Column(nullable=false, columnDefinition="BIGINT(15)")
	private Long updateTime;
	
	/**
	 * 备注
	 */
	@JsonIgnore
	@Column(length=500)
	private String remark;
	
	/**
	 * 上次操作
	 */
	@Column(length=50)
	private String lastOperation;
	
	/**
	 * 上次登陆时间
	 */
	@Column(nullable=false, columnDefinition="BIGINT(15)")
	private Long lastLoginTime;
	
}
