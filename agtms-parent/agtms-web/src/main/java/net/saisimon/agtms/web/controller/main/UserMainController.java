package net.saisimon.agtms.web.controller.main;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.annotation.ResourceInfo;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.web.service.main.UserMainService;

/**
 * 用户主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/user/main")
@ControllerInfo(value="user.management", link="/user/main")
public class UserMainController {
	
	@Autowired
	private UserMainService userMainService;
	
	@ResourceInfo(func=Functions.VIEW)
	@PostMapping("/grid")
	public Result grid() {
		return userMainService.grid();
	}
	
	@ResourceInfo(func=Functions.VIEW)
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@RequestBody Map<String, Object> body) {
		return userMainService.list(body);
	}
	
	@ResourceInfo(func=Functions.LOCK)
	@Operate(type=OperateTypes.EDIT, value="lock")
	@PostMapping("/lock")
	public Result lock(@RequestParam("id") Long id) {
		return userMainService.lock(id);
	}
	
	@ResourceInfo(func=Functions.UNLOCK)
	@Operate(type=OperateTypes.EDIT, value="unlock")
	@PostMapping("/unlock")
	public Result unlock(@RequestParam("id") Long id) {
		return userMainService.unlock(id);
	}
	
	@ResourceInfo(func=Functions.RESET_PASSWORD)
	@Operate(type=OperateTypes.EDIT, value="reset.password")
	@PostMapping("/reset/password")
	public Result resetPassword(@RequestParam("id") Long id) {
		return userMainService.resetPassword(id);
	}
	
	@ResourceInfo(func=Functions.GRANT)
	@PostMapping("/batch/grid")
	public Result batchGrid(@RequestParam("type") String type, @RequestParam("func") String func) {
		return userMainService.batchGrid(type, func);
	}
	
	@ResourceInfo(func=Functions.GRANT)
	@Operate(type=OperateTypes.BATCH_EDIT, value="grant")
	@PostMapping("/grant")
	public Result grant(@RequestBody Map<String, Object> body) {
		return userMainService.grant(body);
	}
	
}
