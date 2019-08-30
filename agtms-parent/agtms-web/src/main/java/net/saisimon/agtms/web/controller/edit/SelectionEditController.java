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
import net.saisimon.agtms.web.dto.req.SelectionParam;
import net.saisimon.agtms.web.service.edit.SelectionEditService;

/**
 * 下拉列表编辑控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/selection/edit")
@ControllerInfo(value="selection.management", link="/selection/main")
public class SelectionEditController {
	
	@Autowired
	private SelectionEditService selectionEditService;
	
	@ResourceInfo(func= { Functions.CREATE, Functions.EDIT })
	@PostMapping("/grid")
	public Result grid(@RequestParam(name = "id", required = false) Long id) {
		return selectionEditService.grid(id);
	}
	
	@ResourceInfo(func= Functions.VIEW)
	@PostMapping("/template")
	public Result template(@RequestParam(name = "id") Long id) {
		return selectionEditService.template(id);
	}
	
	@ResourceInfo(func= Functions.VIEW)
	@PostMapping("/search")
	public Result search(@RequestParam(name = "sign") String sign, @RequestParam(name = "keyword", required = false) String keyword) {
		return selectionEditService.search(sign, keyword);
	}
	
	@ResourceInfo(func= { Functions.CREATE, Functions.EDIT })
	@Operate(type=OperateTypes.EDIT)
	@PostMapping("/save")
	public Result save(@Validated @RequestBody SelectionParam body, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		return selectionEditService.save(body);
	}
	
}
