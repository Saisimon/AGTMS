package net.saisimon.agtms.core.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="agtms_user_token")
@Document(collection="agtms_user_token")
public class UserToken {
	
	/**
	 * 用户 ID
	 */
	@Id
	@org.springframework.data.annotation.Id
	private Long userId;
	
	/**
	 * Token 令牌
	 */
	@Column(length=50)
	private String token;
	
	/**
	 * Token 过期时间
	 */
	@Column
	private Long expireTime;
	
	/**
	 * 管理员
	 */
	@Column
	private Boolean admin;
	
}
