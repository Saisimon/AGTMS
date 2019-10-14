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
import net.saisimon.agtms.web.service.main.RoleMainService;

/**
 * 角色主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/role/main")
@ControllerInfo(value="role.management", link="/role/main")
public class RoleMainController {
	
	@Autowired
	private RoleMainService roleMainService;
	
	@ResourceInfo(func=Functions.VIEW)
	@PostMapping("/grid")
	public Result grid() {
		return roleMainService.grid();
	}
	
	@ResourceInfo(func=Functions.VIEW)
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@RequestBody Map<String, Object> body) {
		return roleMainService.list(body);
	}
	
	@ResourceInfo(func=Functions.REMOVE)
	@Operate(type=OperateTypes.REMOVE)
	@PostMapping("/remove")
	public Result remove(@RequestParam(name = "id") Long id) {
		return roleMainService.remove(id);
	}
	
	@ResourceInfo(func= { Functions.BATCH_REMOVE, Functions.GRANT })
	@PostMapping("/batch/grid")
	public Result batchGrid(@RequestParam("type") String type, @RequestParam("func") String func) {
		return roleMainService.batchGrid(type, func);
	}
	
	@ResourceInfo(func=Functions.BATCH_REMOVE)
	@Operate(type=OperateTypes.BATCH_REMOVE)
	@PostMapping("/batch/remove")
	public Result batchRemove(@RequestBody Map<String, Object> body) {
		return roleMainService.batchRemove(body);
	}
	
	@ResourceInfo(func=Functions.GRANT)
	@Operate(type=OperateTypes.EDIT, value="grant")
	@PostMapping("/grant")
	public Result grant(@RequestBody Map<String, Object> body) {
		return roleMainService.grant(body);
	}
	
}
