package net.saisimon.agtms.core.service;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * 对象存储服务接口
 * 
 * @author saisimon
 *
 */
public interface ObjectStorageService {
	
	/**
	 * 默认为本地实现类型
	 */
	static final String DEFAULT_TYPE = "local";

	/**
	 * 对象存储服务实现类型
	 * 
	 * @return 对象存储服务实现类型
	 */
	String type();
	
	/**
	 * 上传文件
	 * 
	 * @param input 文件流
	 * @param path 文件中间路径
	 * @param filename 文件名
	 * @return 返回文件 URL
	 * @throws IOException 上传文件异常
	 */
	String upload(InputStream input, String path, String filename) throws IOException;
	
	/**
	 * 获取文件
	 * 
	 * @param response 响应
	 * @param path 文件中间路径
	 * @param filename 文件名
	 * @throws IOException 获取文件异常
	 */
	void fetch(HttpServletResponse response, String path, String filename) throws IOException;
	
}
