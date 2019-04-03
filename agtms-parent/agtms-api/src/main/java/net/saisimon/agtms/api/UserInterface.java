package net.saisimon.agtms.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.saisimon.agtms.api.hystrix.UserHystrixFallback;

/**
 * 用户接口
 * 
 * @author saisimon
 *
 */
@FeignClient(name = "agtms-web", fallback = UserHystrixFallback.class)
public interface UserInterface {
	
	/**
	 * 验证 uid 与 token 是否正确并且匹配
	 * 
	 * @param uid 用户ID
	 * @param token 令牌
	 * @return uid 与 token 正确并且匹配返回 true， 否则返回 false
	 */
	@PostMapping("/api/check/token")
	boolean checkToken(@RequestParam("uid") String uid, @RequestParam("token") String token);
	
}
