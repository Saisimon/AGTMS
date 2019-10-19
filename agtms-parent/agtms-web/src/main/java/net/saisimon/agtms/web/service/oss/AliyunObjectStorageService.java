package net.saisimon.agtms.web.service.oss;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.property.OssProperties;
import net.saisimon.agtms.core.service.ObjectStorageService;

/**
 * Aliyun 对象存储服务实现
 * <pre>
 * agtms:
 *   oss:
 *     type: aliyun
 *     endpoint: &lt;endpoint&gt;
 *     accessKeyId: &lt;yourAccessKeyId&gt;
 *     accessKeySecret: &lt;yourAccessKeySecret&gt;
 *     bucket: &lt;yourBucketName&gt;
 * </pre>
 * 
 * @author saisimon
 *
 */
@Service
@Slf4j
public class AliyunObjectStorageService implements ObjectStorageService {
	
	private static final String ENDPOINT = "endpoint";
	private static final String ACCESS_KEY_ID = "accessKeyId";
	private static final String ACCESS_KEY_SECRET = "accessKeySecret";
	private static final String BUCKET = "bucket";
	
	@Autowired
	private OssProperties ossProperties;

	@Override
	public String type() {
		return "aliyun";
	}

	@Override
	public String upload(InputStream input, String path, String filename) throws IOException {
		Map<String, String> properties = ossProperties.getProperties();
		if (CollectionUtils.isEmpty(properties)) {
			return null;
		}
		String bucket = properties.get(BUCKET);
		OSS ossClient = buildOSS(properties);
		try {
			ossClient.putObject(bucket, path + "/" + filename, input);
		} catch (Exception e) {
			log.error("put object failed", e);
			return null;
		} finally {
			if (ossClient != null) {
				ossClient.shutdown();
			}
		}
		return "/resources/" + path + "/" + filename;
	}
	
	@Override
	public void fetch(HttpServletResponse response, String path, String filename) throws IOException {
		Map<String, String> properties = ossProperties.getProperties();
		if (CollectionUtils.isEmpty(properties)) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		String bucket = properties.get(BUCKET);
		OSS ossClient = buildOSS(properties);
		try {
			OSSObject ossObject = ossClient.getObject(bucket, "image/" + filename);
			try (InputStream input = ossObject.getObjectContent()) {
				IOUtils.copy(input, response.getOutputStream());
			}
		} catch (Exception e) {
			log.error("get object failed", e);
			response.sendError(HttpStatus.NOT_FOUND.value());
		} finally {
			if (ossClient != null) {
				ossClient.shutdown();
			}
		}
	}
	
	private OSS buildOSS(Map<String, String> properties) {
		String endpoint = properties.get(ENDPOINT);
		String accessKeyId = properties.get(ACCESS_KEY_ID);
		String accessKeySecret = properties.get(ACCESS_KEY_SECRET);
		return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
	}

}
