package net.saisimon.agtms.web.controller.main;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.ResourceInfo;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.web.service.main.OperationMainService;

/**
 * 操作记录主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/operation/main")
@ControllerInfo(value="operation.management", link="/operation/main")
public class OperationMainController {
	
	@Autowired
	private OperationMainService operationMainService;
	
	@ResourceInfo(func=Functions.VIEW)
	@PostMapping("/grid")
	public Result grid() {
		return operationMainService.grid();
	}
	
	@ResourceInfo(func=Functions.VIEW)
	@PostMapping("/list")
	public Result list(@RequestBody Map<String, Object> body) {
		return operationMainService.list(body);
	}

}
