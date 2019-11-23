package net.saisimon.agtms.web.controller.index;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.web.service.index.IndexInfoService;

/**
 * 首页信息控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping(path = "/index")
@ControllerInfo("index")
public class IndexController {
	
	@Autowired
	private IndexInfoService indexInfoService;
	
	/**
	 * 获取统计信息
	 * 
	 * @return 统计信息结果
	 */
	@PostMapping("/statistics")
	public Result statistics() {
		return indexInfoService.statistics();
	}
	
}
