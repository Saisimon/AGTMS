package net.saisimon.agtms.web.controller.upload;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@ControllerInfo("image")
public class ImageController {
	
	@Autowired
	private ImageInfoService imageInfoService;
	
	/**
	 * 图片上传
	 * 
	 * @param image 图片文件
	 * @return 图片上传结果
	 */
	@Operate(type=OperateTypes.UPLOAD)
	@PostMapping("/image/upload")
	public Result upload(@RequestParam("image") MultipartFile image) {
		return imageInfoService.upload(image);
	}
	
	/**
	 * 获取图片
	 * 
	 * @param filename 文件名
	 * @throws IOException 获取异常
	 */
	@GetMapping("/resources/image/{filename}")
	public void fetch(@PathVariable("filename") String filename) throws IOException {
		imageInfoService.fetch(filename);
	}
	
}
