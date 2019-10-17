package net.saisimon.agtms.core.service;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

public interface ObjectStorageService {
	
	static final String DEFAULT_TYPE = "local";

	String type();
	
	String upload(InputStream input, String path, String filename) throws IOException;
	
	void fetch(HttpServletResponse response, String path, String filename) throws IOException;
	
}
