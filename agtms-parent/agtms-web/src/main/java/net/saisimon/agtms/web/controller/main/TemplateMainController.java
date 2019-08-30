package net.saisimon.agtms.web.controller.main;

import java.util.List;
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
import net.saisimon.agtms.web.service.main.TemplateMainService;

/**
 * 模板主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/template/main")
@ControllerInfo(value="template.management", link="/template/main")
public class TemplateMainController {
	
	@Autowired
	private TemplateMainService templateMainService;
	
	@ResourceInfo(func=Functions.VIEW)
	@PostMapping("/grid")
	public Result grid() {
		return templateMainService.grid();
	}
	
	@ResourceInfo(func=Functions.VIEW)
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@RequestBody Map<String, Object> body) {
		return templateMainService.list(body);
	}
	
	@ResourceInfo(func=Functions.REMOVE)
	@Operate(type=OperateTypes.REMOVE)
	@PostMapping("/remove")
	public Result remove(@RequestParam(name = "id") Long id) {
		return templateMainService.remove(id);
	}
	
	@ResourceInfo(func=Functions.BATCH_REMOVE)
	@Operate(type=OperateTypes.BATCH_REMOVE)
	@PostMapping("/batch/remove")
	public Result batchRemove(@RequestBody List<Long> ids) {
		return templateMainService.batchRemove(ids);
	}
	
}
