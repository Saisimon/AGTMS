package net.saisimon.agtms.web.controller.edit;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
import net.saisimon.agtms.web.service.edit.ManagementEditService;

/**
 * 自定义对象管理编辑控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/management/edit/{key}")
@ControllerInfo(value="management", link="/management/main")
public class ManagementEditController {
	
	@Autowired
	private ManagementEditService managementEditService;
	
	@ResourceInfo(func= { Functions.CREATE, Functions.EDIT })
	@PostMapping("/grid")
	public Result grid(@PathVariable("key") String key, @RequestParam(name = "id", required = false) Long id) {
		return managementEditService.grid(key, id);
	}
	
	@ResourceInfo(func= { Functions.CREATE, Functions.EDIT })
	@Operate(type=OperateTypes.EDIT)
	@PostMapping("/save")
	public Result save(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		return managementEditService.save(key, body);
	}
	
}
