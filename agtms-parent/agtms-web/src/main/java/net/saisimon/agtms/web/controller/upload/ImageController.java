package net.saisimon.agtms.web.controller.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.ImageFormats;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.BaseController;

@RestController
@RequestMapping("/image")
@ControllerInfo("image")
@Slf4j
public class ImageController extends BaseController {
	
	@Operate(type=OperateTypes.UPLOAD)
	@PostMapping("/upload")
	public Result upload(@RequestParam("image") MultipartFile image) {
		try {
			ImageFormats imageFormat = FileUtils.imageFormat(image.getInputStream());
			if (imageFormat == ImageFormats.UNKNOWN) {
				return ErrorMessage.Common.UNSUPPORTED_FORMAT;
			}
			String md5 = DigestUtils.md5Hex(image.getInputStream());
			String path = md5.substring(0, 2) + "/" + md5.substring(2, 4) + "/" + md5.substring(4) + imageFormat.getSuffix();
			File file = new File(Constant.File.UPLOAD_PATH + "/image/" + path);
			if (!file.exists()) {
				FileUtils.createDir(file.getParentFile());
				FileOutputStream output = new FileOutputStream(file);
				IOUtils.copy(image.getInputStream(), output);
				output.flush();
			}
			String url = "/image/res/" + path;
			return ResultUtils.simpleSuccess(url);
		} catch (IOException e) {
			log.error("Upload Failed", e);
			return ErrorMessage.Common.UPLOAD_FAILED;
		}
	}
	
	@GetMapping("/res/{first}/{second}/{third}")
	public void res(@PathVariable("first") String first, @PathVariable("second") String second, @PathVariable("third") String third) throws IOException {
		String path = first + "/" + second + "/" + third;
		File file = new File(Constant.File.UPLOAD_PATH + "/image/" + path);
		if (!file.exists()) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		ImageFormats imageFormat = FileUtils.imageFormat(third);
		switch (imageFormat) {
		case JPG:
			response.setContentType("image/jpeg");
			break;
		case PNG:
			response.setContentType("image/png");
			break;
		case GIF:
			response.setContentType("image/gif");
			break;
		case BMP:
			response.setContentType("image/bmp");
			break;
		default:
			response.setContentType("application/octet-stream");
			break;
		}
		try (InputStream input = new FileInputStream(file)) {
			IOUtils.copy(input, response.getOutputStream());
		}
	}
	
}
