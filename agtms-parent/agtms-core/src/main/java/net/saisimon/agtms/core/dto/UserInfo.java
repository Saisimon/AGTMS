package net.saisimon.agtms.core.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserInfo implements Serializable {
	
	private static final long serialVersionUID = -6183223245993366757L;
	
	private Long userId;
	
	private String loginName;
	
	private String token;
	
}
