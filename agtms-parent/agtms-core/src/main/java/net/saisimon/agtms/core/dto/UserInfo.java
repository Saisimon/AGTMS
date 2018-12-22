package net.saisimon.agtms.core.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserInfo implements Serializable {
	
	private static final long serialVersionUID = -6183223245993366757L;
	
	private Long userId;
	
	private String loginName;
	
	private String nickName;
	
	private String cellphone;
	
	private String email;
	
	private String remoteAddr;
	
	private String createTime;
	
	private String updateTime;
	
	private String lastOperation;
	
	private String lastLoginTime;
	
}
