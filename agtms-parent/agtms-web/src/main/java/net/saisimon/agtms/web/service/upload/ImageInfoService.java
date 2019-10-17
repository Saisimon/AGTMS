package net.saisimon.agtms.web.service.upload;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.ImageFormats;
import net.saisimon.agtms.core.factory.ObjectStorageServiceFactory;
import net.saisimon.agtms.core.property.OssProperties;
import net.saisimon.agtms.core.service.ObjectStorageService;
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
@Slf4j
public class ImageInfoService {
	
	@Autowired
	private OssProperties ossProperties;
	@Autowired
	protected HttpServletResponse response;
	
	public Result upload(MultipartFile image) {
		try {
			ImageFormats imageFormat = FileUtils.imageFormat(image.getInputStream());
			if (imageFormat == ImageFormats.UNKNOWN) {
				return ErrorMessage.Common.UNSUPPORTED_FORMAT;
			}
			String filename = UUID.randomUUID().toString() + imageFormat.getSuffix();
			ObjectStorageService oss = ObjectStorageServiceFactory.get(ossProperties.getType());
			String path = oss.upload(image.getInputStream(), "image", filename);
			if (path == null) {
				return ErrorMessage.Common.UPLOAD_FAILED;
			}
			return ResultUtils.simpleSuccess(path);
		} catch (IOException e) {
			log.error("upload failed", e);
			return ErrorMessage.Common.UPLOAD_FAILED;
		}
	}
	
	public void fetch(String filename) throws IOException {
		ImageFormats imageFormat = FileUtils.imageFormat(filename);
		if (imageFormat == ImageFormats.UNKNOWN) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		setContentType(imageFormat);
		ObjectStorageService oss = ObjectStorageServiceFactory.get(ossProperties.getType());
		oss.fetch(response, "image", filename);
	}
	
	private void setContentType(ImageFormats imageFormat) {
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
	}
	
}
