package net.saisimon.agtms.web.config.handler.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import net.saisimon.agtms.core.enums.FileTypes;
import net.saisimon.agtms.core.handler.FileHandler;
import net.saisimon.agtms.web.util.FileUtils;

/**
 * XLSX 文件处理实现类
 * 
 * @author saisimon
 *
 */
@Component
public class XlsxFileHandler implements FileHandler {

	@Override
	public void populate(File file, List<List<Object>> datas) throws IOException {
		try (OutputStream out = new FileOutputStream(file)) {
			FileUtils.toExcel(out, datas, true);
		}
	}
	
	@Override
	public List<List<String>> fetch(File file) throws IOException {
		try (InputStream in = new FileInputStream(file)) {
			List<List<String>> datas = new ArrayList<>();
			Map<String, List<List<String>>> dataXLSXMap = FileUtils.fromExcel(in, true);
			for (List<List<String>> value : dataXLSXMap.values()) {
				if (value.size() > 1) {
					if (datas.isEmpty()) {
						datas.add(value.get(0));
					}
					datas.addAll(value.subList(1, value.size()));
				}
			}
			return datas;
		}
	}
	
	@Override
	public int size(MultipartFile file) throws IOException {
		try (InputStream in = file.getInputStream()) {
			return FileUtils.sizeExcel(in, true);
		}
	}
	
	@Override
	public void merge(File mergedFile, List<File> files) throws IOException {
		FileUtils.mergeExcel(mergedFile, files, true);
	}

	@Override
	public FileTypes type() {
		return FileTypes.XLSX;
	}

}
