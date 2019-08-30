package net.saisimon.agtms.web.service.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.io.FileUtil;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.ImageFormats;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.util.FileUtils;

/**
 * 图片信息服务
 * 
 * @author saisimon
 *
 */
@Service
public class ImageInfoService {
	
	@Autowired
	private AgtmsProperties agtmsProperties;
	@Autowired
	protected HttpServletResponse response;
	
	public Result upload(MultipartFile image) throws IOException {
		ImageFormats imageFormat = FileUtils.imageFormat(image.getInputStream());
		if (imageFormat == ImageFormats.UNKNOWN) {
			return ErrorMessage.Common.UNSUPPORTED_FORMAT;
		}
		String md5 = DigestUtils.md5Hex(image.getInputStream());
		String first = md5.substring(0, 2);
		String second = md5.substring(2, 4);
		String third = md5.substring(4) + imageFormat.getSuffix();
		StringBuilder uploadFilePath = new StringBuilder();
		uploadFilePath.append(agtmsProperties.getFilepath())
			.append(File.separatorChar).append(Constant.File.UPLOAD_PATH)
			.append(File.separatorChar).append("image")
			.append(File.separatorChar).append(first)
			.append(File.separatorChar).append(second)
			.append(File.separatorChar).append(third);
		File file = new File(uploadFilePath.toString());
		if (!file.exists()) {
			FileUtil.mkParentDirs(file);
			FileOutputStream output = new FileOutputStream(file);
			IOUtils.copy(image.getInputStream(), output);
			output.flush();
		}
		StringBuilder uploadUri= new StringBuilder();
		uploadUri.append("/image/res/").append(first).append("/").append(second).append("/").append(third);
		return ResultUtils.simpleSuccess(uploadUri.toString());
	}
	
	public void res(String first, String second, String third) throws IOException {
		StringBuilder uploadFilePath = new StringBuilder();
		uploadFilePath.append(agtmsProperties.getFilepath())
			.append(File.separatorChar).append(Constant.File.UPLOAD_PATH)
			.append(File.separatorChar).append("image")
			.append(File.separatorChar).append(first)
			.append(File.separatorChar).append(second)
			.append(File.separatorChar).append(third);
		File file = new File(uploadFilePath.toString());
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
