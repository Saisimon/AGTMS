package net.saisimon.agtms.web.dto.resp;

import lombok.Data;

@Data
public class UserTokenInfo {

	private String userId;

	private String token;

	private Long expireTime;

	private Integer status;

	private String loginName;

	private String avatar;

}