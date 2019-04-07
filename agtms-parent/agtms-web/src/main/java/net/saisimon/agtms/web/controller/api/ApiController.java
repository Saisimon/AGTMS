package net.saisimon.agtms.web.controller.api;

import java.util.concurrent.Future;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.util.NumberUtil;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.util.SystemUtils;

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
		if (!NumberUtil.isLong(uid)) {
			return false;
		}
		UserToken userToken = TokenFactory.get().getToken(Long.valueOf(uid), true);
		if (userToken == null) {
			return false;
		}
		return token.equals(userToken.getToken());
	}
	
	/**
	 * 根据任务 ID 取消任务
	 * 
	 * @param taskId 任务 ID
	 */
	@GetMapping("/cancel/task")
	public void cancel(@RequestParam("taskId") Long taskId) {
		Future<?> future = SystemUtils.removeTaskFuture(taskId);
		if (future != null) {
			future.cancel(true);
		}
	}
	
}
