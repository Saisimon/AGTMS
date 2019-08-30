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
import net.saisimon.agtms.web.dto.req.UserParam;
import net.saisimon.agtms.web.service.edit.UserEditService;

/**
 * 用户编辑控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/user/edit")
@ControllerInfo(value="user.management", link="/user/main")
public class UserEditController {
	
	@Autowired
	private UserEditService userEditService;
	
	@ResourceInfo(func= { Functions.CREATE, Functions.EDIT })
	@PostMapping("/grid")
	public Result grid(@RequestParam(name = "id", required = false) Long id) {
		return userEditService.grid(id);
	}
	
	@ResourceInfo(func= { Functions.CREATE, Functions.EDIT })
	@Operate(type=OperateTypes.EDIT)
	@PostMapping("/save")
	public Result save(@Validated @RequestBody UserParam body, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		return userEditService.save(body);
	}
	
}
