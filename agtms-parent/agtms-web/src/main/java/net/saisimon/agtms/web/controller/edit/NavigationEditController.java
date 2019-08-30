package net.saisimon.agtms.web.controller.edit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.NavigationParam;
import net.saisimon.agtms.web.service.edit.NavigationEditService;

/**
 * 导航编辑控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/navigation/edit")
@ControllerInfo(value="navigation.management", link="/navigation/main")
public class NavigationEditController {
	
	@Autowired
	private NavigationEditService navigationEditService;
	
	@ResourceInfo(func= { Functions.CREATE, Functions.EDIT })
	@PostMapping("/grid")
	public Result grid(@RequestParam(name = "id", required = false) Long id) {
		return navigationEditService.grid(id);
	}
	
	@ResourceInfo(func= { Functions.CREATE, Functions.EDIT })
	@Operate(type=OperateTypes.EDIT)
	@PostMapping("/save")
	public Result save(@Validated @RequestBody NavigationParam body, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		return navigationEditService.save(body);
	}
	
}
