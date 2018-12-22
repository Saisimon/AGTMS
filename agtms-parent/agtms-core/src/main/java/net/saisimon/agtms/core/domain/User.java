package net.saisimon.agtms.core.domain;

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
	
	@Column(length=50, nullable=false, unique=true)
	private String loginName;
	
	@Column(length=10, nullable=false)
	private String salt;
	
	@JsonIgnore
	@Column(length=128, nullable=false)
	private String password;
	
	@Column(length=50)
	private String nickName;
	
	@Column(length=20)
	private String cellphone;
	
	@Column(length=50, nullable=false, unique=true)
	private String email;
	
	@Column(nullable=false, columnDefinition="bigint(15)")
	private Long createTime;
	
	@Column(nullable=false, columnDefinition="bigint(15)")
	private Long updateTime;
	
	@JsonIgnore
	@Column(length=500)
	private String remark;
	
	@Column(length=50)
	private String lastOperation;
	
	@Column(nullable=false, columnDefinition="bigint(15)")
	private Long lastLoginTime;
	
}
