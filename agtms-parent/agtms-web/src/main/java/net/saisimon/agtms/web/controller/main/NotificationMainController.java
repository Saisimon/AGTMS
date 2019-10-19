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
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.web.service.main.NotificationMainService;

/**
 * 消息通知主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/notification/main")
@ControllerInfo(value="notification.management", link="/notification/main")
public class NotificationMainController {
	
	@Autowired
	private NotificationMainService notificationMainService;
	
	@PostMapping("/grid")
	public Result grid() {
		return notificationMainService.grid();
	}
	
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@RequestBody Map<String, Object> body) {
		return notificationMainService.list(body);
	}
	
	@Operate(type=OperateTypes.REMOVE)
	@PostMapping("/remove")
	public Result remove(@RequestParam(name = "id") Long id) {
		return notificationMainService.remove(id);
	}
	
	@PostMapping("/batch/grid")
	public Result batchGrid(@RequestParam("type") String type, @RequestParam("func") String func) {
		return notificationMainService.batchGrid(type, func);
	}
	
	@Operate(type=OperateTypes.BATCH_REMOVE)
	@PostMapping("/batch/remove")
	public Result batchRemove(@RequestBody Map<String, Object> body) {
		return notificationMainService.batchRemove(body);
	}

}
