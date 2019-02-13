package net.saisimon.agtms.web.controller.selection;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.domain.tag.Select;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.factory.SelectionFactory;
import net.saisimon.agtms.core.selection.Selection;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.BaseController;

/**
 * 下拉列表控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/selection")
public class SelectionController extends BaseController {
	
	/**
	 * 获取指定关键字的下拉列表信息
	 * 
	 * @param key 下拉列表对应的关键字
	 * @return 下拉列表响应
	 * @see net.saisimon.agtms.core.enums.Selections
	 */
	@PostMapping("/{key}")
	public Result info(@PathVariable("key") String key) {
		Selection selection = SelectionFactory.getSelection(key);
		if (selection == null) {
			return ErrorMessage.Common.PARAM_ERROR;
		}
		return ResultUtils.simpleSuccess(Select.buildOptions(selection.select()));
	}
	
}
