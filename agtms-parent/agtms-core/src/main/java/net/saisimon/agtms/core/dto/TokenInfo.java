package net.saisimon.agtms.core.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Token 信息
 * 
 * @author saisimon
 *
 */
@Data
public class TokenInfo implements Serializable {
	
	private static final long serialVersionUID = -6183223245993366757L;
	
	/**
	 * 用户ID
	 */
	private Long userId;
	
	/**
	 * 令牌
	 */
	private String token;
	
	/**
	 * 过期时间
	 */
	private Long expireTime;
	
}
