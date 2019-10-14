package net.saisimon.agtms.web.controller.main;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.annotation.ResourceInfo;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.ExportParam;
import net.saisimon.agtms.web.service.main.ManagementMainService;

/**
 * 自定义对象管理主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/management/main/{key}")
@ControllerInfo(value="management", link="/management/main")
public class ManagementMainController {
	
	@Autowired
	private ManagementMainService managementMainService;
	
	@ResourceInfo(func=Functions.VIEW)
	@PostMapping("/grid")
	public Result grid(@PathVariable("key") String key) {
		return managementMainService.grid(key);
	}
	
	@ResourceInfo(func=Functions.VIEW)
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		return managementMainService.list(key, body);
	}

	@ResourceInfo(func=Functions.REMOVE)
	@Operate(type=OperateTypes.REMOVE)
	@PostMapping("/remove")
	public Result remove(@PathVariable("key") String key, @RequestParam(name = "id") Long id) {
		return managementMainService.remove(key, id);
	}
	
	@ResourceInfo(func={ Functions.BATCH_EDIT, Functions.BATCH_REMOVE, Functions.EXPORT, Functions.IMPORT })
	@PostMapping("/batch/grid")
	public Result batchGrid(@PathVariable("key") String key, @RequestParam("type") String type, @RequestParam("func") String func) {
		return managementMainService.batchGrid(key, type, func);
	}
	
	@ResourceInfo(func=Functions.BATCH_EDIT)
	@Operate(type=OperateTypes.BATCH_EDIT)
	@PostMapping("/batch/save")
	public Result batchSave(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		return managementMainService.batchSave(key, body);
	}

	@ResourceInfo(func=Functions.BATCH_REMOVE)
	@Operate(type=OperateTypes.BATCH_REMOVE)
	@PostMapping("/batch/remove")
	public Result batchRemove(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		return managementMainService.batchRemove(key, body);
	}
	
	@ResourceInfo(func=Functions.EXPORT)
	@Operate(type=OperateTypes.EXPORT)
	@PostMapping("/batch/export")
	public Result batchExport(@PathVariable("key") String key, @Validated @RequestBody ExportParam body, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		return managementMainService.batchExport(key, body);
	}
	
	@ResourceInfo(func=Functions.IMPORT)
	@Operate(type=OperateTypes.IMPORT)
	@PostMapping("/batch/import")
	public Result batchImport(@PathVariable("key") String key, 
			@RequestParam(name="importFileName", required=false) String importFileName, 
			@RequestParam("importFileType") String importFileType, 
			@RequestParam("importFields") List<String> importFields, 
			@RequestParam("importFiles") MultipartFile[] importFiles) {
		return managementMainService.batchImport(key, importFileName, importFileType, importFields, importFiles);
	}
	
}
