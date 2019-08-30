package net.saisimon.agtms.web.controller.upload;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.web.service.upload.ImageInfoService;

/**
 * 图片控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/image")
@ControllerInfo("image")
public class ImageController {
	
	@Autowired
	private ImageInfoService imageInfoService;
	
	/**
	 * 图片上传
	 * 
	 * @param image 图片文件
	 * @return 图片上传结果
	 * @throws IOException 上传异常
	 */
	@Operate(type=OperateTypes.UPLOAD)
	@PostMapping("/upload")
	public Result upload(@RequestParam("image") MultipartFile image) throws IOException {
		return imageInfoService.upload(image);
	}
	
	/**
	 * 获取图片
	 * 
	 * @param first 一级路径
	 * @param second 二级路径
	 * @param third 三级路径
	 * @throws IOException 获取异常
	 */
	@GetMapping("/res/{first}/{second}/{third}")
	public void res(@PathVariable("first") String first, @PathVariable("second") String second, @PathVariable("third") String third) throws IOException {
		imageInfoService.res(first, second, third);
	}
	
}
