package net.saisimon.agtms.core.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户基本信息
 * 
 * @author saisimon
 *
 */
@Data
public class UserInfo implements Serializable {
	
	private static final long serialVersionUID = -6183223245993366757L;
	
	/**
	 * 用户ID
	 */
	private Long userId;
	
	/**
	 * 用户登录名
	 */
	private String loginName;
	
	/**
	 * 令牌
	 */
	private String token;
	
}
