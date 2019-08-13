package net.saisimon.agtms.web.config.handler.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import freemarker.template.TemplateException;
import net.saisimon.agtms.core.enums.FileTypes;
import net.saisimon.agtms.core.handler.FileHandler;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.web.util.FileUtils;

@Component
public class PdfFileHandler implements FileHandler {
	
	@Autowired
	private AgtmsProperties agtmsProperties;
	
	private static String fontPath = null;

	@Override
	public void populate(File file, List<List<Object>> datas) throws IOException {
		try (OutputStream out = new FileOutputStream(file)) {
			try {
				FileUtils.toPDF(out, datas, getFont(), agtmsProperties.getPdfFontFamily());
			} catch (TemplateException e) {
				throw new IOException("Freemarker 模板解析错误", e);
			}
		}
	}
	
	@Override
	public List<List<String>> fetch(File file) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int size(MultipartFile file) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void merge(File mergedFile, List<File> files) throws IOException {
		if (mergedFile == null || files == null) {
			return;
		}
		FileUtils.mergePDF(mergedFile, files);
	}

	@Override
	public FileTypes type() {
		return FileTypes.PDF;
	}
	
	private synchronized String getFont() {
		if (fontPath != null) {
			return fontPath;
		}
		String path = agtmsProperties.getPdfFontPath();
		if (path != null && new File(path).exists()) {
			fontPath = path;
			return fontPath;
		}
		if (SystemUtils.IS_OS_MAC) {
			path = "/Library/Fonts/Arial Unicode.ttf";
		} else if (SystemUtils.IS_OS_WINDOWS) {
			path = "C:/Windows/Fonts/ARIALUNI.TTF";
		}
		if (path != null && new File(path).exists()) {
			fontPath = path;
		}
		return fontPath;
	}

}
