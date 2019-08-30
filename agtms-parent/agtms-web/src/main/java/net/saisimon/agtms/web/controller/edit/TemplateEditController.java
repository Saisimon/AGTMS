package net.saisimon.agtms.web.controller.edit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.annotation.ResourceInfo;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.web.service.edit.TemplateEditService;

/**
 * 模板编辑控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/template/edit")
@ControllerInfo(value="template.management", link="/template/main")
public class TemplateEditController {
	
	@Autowired
	private TemplateEditService templateEditService;
	
	@ResourceInfo(func= { Functions.CREATE, Functions.EDIT })
	@PostMapping("grid")
	public Result grid(@RequestParam(name = "id", required = false) Long id) {
		return templateEditService.grid(id);
	}

	@ResourceInfo(func= { Functions.CREATE, Functions.EDIT })
	@Operate(type=OperateTypes.EDIT)
	@PostMapping("/save")
	public Result save(@RequestBody Template template) throws GenerateException {
		return templateEditService.save(template);
	}
	
}
