package net.saisimon.agtms.core.domain.entity;

import java.io.Serializable;

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
@Table(name=UserToken.TABLE_NAME)
@Document(collection=UserToken.TABLE_NAME)
public class UserToken implements Serializable {
	
	private static final long serialVersionUID = -481086455612777941L;
	
	public static final String TABLE_NAME = "agtms_user_token";

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
	
}
