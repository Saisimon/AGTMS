package net.saisimon.agtms.web.controller.main;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
import net.saisimon.agtms.web.service.main.TaskMainService;

/**
 * 任务主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/task/main")
@ControllerInfo(value="task.management", link="/task/main")
public class TaskMainController {
	
	@Autowired
	private TaskMainService taskMainService;
	
	@ResourceInfo(func=Functions.VIEW)
	@PostMapping("/grid")
	public Result grid() {
		return taskMainService.grid();
	}
	
	@ResourceInfo(func=Functions.VIEW)
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@RequestBody Map<String, Object> body) {
		return taskMainService.list(body);
	}
	
	@Operate(type=OperateTypes.QUERY, value="download")
	@GetMapping("/download")
	public void download(@RequestParam(name = "id") Long id, @RequestParam(name = "uuid") String uuid) throws IOException {
		taskMainService.download(id, uuid);
	}
	
	@ResourceInfo(func=Functions.EDIT)
	@Operate(type=OperateTypes.QUERY, value="cancel")
	@PostMapping("/cancel")
	public Result cancel(@RequestParam(name = "id") Long id) {
		return taskMainService.cancel(id);
	}
	
	@ResourceInfo(func=Functions.REMOVE)
	@Operate(type=OperateTypes.REMOVE)
	@PostMapping("/remove")
	public Result remove(@RequestParam(name = "id") Long id) {
		return taskMainService.remove(id);
	}
	
	@ResourceInfo(func=Functions.BATCH_REMOVE)
	@PostMapping("/batch/grid")
	public Result batchGrid(@RequestParam("type") String type, @RequestParam("func") String func) {
		return taskMainService.batchGrid(type, func);
	}
	
	@ResourceInfo(func=Functions.BATCH_REMOVE)
	@Operate(type=OperateTypes.BATCH_REMOVE)
	@PostMapping("/batch/remove")
	public Result batchRemove(@RequestBody Map<String, Object> body) {
		return taskMainService.batchRemove(body);
	}

}
