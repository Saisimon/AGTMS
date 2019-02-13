package net.saisimon.agtms.web.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.util.AuthUtils;

/**
 * 接口控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping(path = "/api")
public class ApiController {
	
	/**
	 * 验证 uid 与 token 是否正确并且匹配
	 * 
	 * @param uid 用户ID
	 * @param token 令牌
	 * @return uid 与 token 正确并且匹配返回 true， 否则返回 false
	 */
	@PostMapping("/check/token")
	public boolean checkToken(@RequestParam("uid") String uid, @RequestParam("token") String token) {
		UserInfo userInfo = AuthUtils.getUserInfo(uid);
		if (userInfo == null) {
			return false;
		}
		return token.equals(userInfo.getToken());
	}
	
}
