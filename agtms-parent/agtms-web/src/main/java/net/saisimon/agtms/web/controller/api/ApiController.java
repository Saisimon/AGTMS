package net.saisimon.agtms.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.web.service.main.TaskMainService;
import net.saisimon.agtms.web.service.main.UserMainService;

/**
 * 接口控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping(path = "/api")
public class ApiController {
	
	@Autowired
	private UserMainService userMainService;
	@Autowired
	private TaskMainService taskMainService;
	
	/**
	 * 验证 uid 与 token 是否正确并且匹配
	 * 
	 * @param uid 用户ID
	 * @param token 令牌
	 * @return uid 与 token 正确并且匹配返回 true， 否则返回 false
	 */
	@PostMapping("/check/token")
	public boolean checkToken(@RequestParam("uid") String uid, @RequestParam("token") String token) {
		return userMainService.checkToken(uid, token);
	}
	
	/**
	 * 根据任务 ID 取消任务
	 * 
	 * @param taskId 任务 ID
	 * @return 取消任务结果
	 */
	@PostMapping("/cancel/task")
	public void cancel(@RequestParam("taskId") Long taskId) {
		taskMainService.cancelTask(taskId);
	}
	
}
