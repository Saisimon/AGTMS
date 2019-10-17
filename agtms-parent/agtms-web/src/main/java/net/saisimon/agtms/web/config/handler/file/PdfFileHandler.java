package net.saisimon.agtms.web.config.handler.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import freemarker.template.TemplateException;
import net.saisimon.agtms.core.enums.FileTypes;
import net.saisimon.agtms.core.handler.FileHandler;
import net.saisimon.agtms.core.property.BasicProperties;
import net.saisimon.agtms.web.util.FileUtils;

@Component
public class PdfFileHandler implements FileHandler {
	
	@Autowired
	private BasicProperties basicProperties;
	
	private static String fontPath = null;

	@Override
	public void populate(File file, List<List<Object>> datas) throws IOException {
		try (OutputStream out = new FileOutputStream(file)) {
			try {
				FileUtils.toPDF(out, handleDatas(datas), getFont(), basicProperties.getPdfFontFamily());
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
		String path = basicProperties.getPdfFontPath();
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
	
	private List<List<Object>> handleDatas(List<List<Object>> datas) {
		if (CollectionUtils.isEmpty(datas)) {
			return datas;
		}
		List<List<Object>> newDatas = new ArrayList<>();
		for (int i = 0; i < datas.size(); i++) {
			List<Object> data = datas.get(i);
			if (data == null) {
				continue;
			}
			List<Object> newData = new ArrayList<>();
			for (int j = 0; j < data.size(); j++) {
				Object val = data.get(j);
				if (val != null && val instanceof String) {
					val = "<![CDATA[" + (String) val + "]]>";
				}
				newData.add(val);
			}
			newDatas.add(newData);
		}
		return newDatas;
	}

}
