package net.saisimon.agtms.web.service.oss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import cn.hutool.core.io.FileUtil;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.property.BasicProperties;
import net.saisimon.agtms.core.service.ObjectStorageService;

/**
 * 本地对象存储服务实现
 * 
 * @author saisimon
 *
 */
@Service
public class LocalObjectStorageService implements ObjectStorageService {
	
	@Autowired
	private BasicProperties basicProperties;

	@Override
	public String type() {
		return DEFAULT_TYPE;
	}

	@Override
	public String upload(InputStream input, String path, String filename) throws IOException {
		File file = new File(buildImagePath(path, filename));
		FileUtil.mkParentDirs(file);
		try (FileOutputStream output = new FileOutputStream(file)) {
			IOUtils.copy(input, output);
			output.flush();
		}
		return "/resources/" + path + "/" + filename;
	}

	@Override
	public void fetch(HttpServletResponse response, String path, String filename) throws IOException {
		File file = new File(buildImagePath(path, filename));
		if (!file.exists()) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		try (InputStream input = new FileInputStream(file)) {
			IOUtils.copy(input, response.getOutputStream());
		}
	}
	
	private String buildImagePath(String path, String filename) {
		StringBuilder uploadFilePath = new StringBuilder();
		uploadFilePath.append(basicProperties.getFilepath())
			.append(File.separatorChar).append(Constant.File.UPLOAD_PATH)
			.append(File.separatorChar).append(path)
			.append(File.separatorChar).append(filename);
		return uploadFilePath.toString();
	}

}
