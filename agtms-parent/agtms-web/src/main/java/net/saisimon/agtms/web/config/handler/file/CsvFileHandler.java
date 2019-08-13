package net.saisimon.agtms.web.config.handler.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import net.saisimon.agtms.core.enums.FileTypes;
import net.saisimon.agtms.core.handler.FileHandler;
import net.saisimon.agtms.web.util.FileUtils;

@Component
public class CsvFileHandler implements FileHandler {

	@Override
	public void populate(File file, List<List<Object>> datas) throws IOException {
		try (OutputStream out = new FileOutputStream(file)) {
			FileUtils.toCSV(out, datas);
		}
	}
	
	@Override
	public List<List<String>> fetch(File file) throws IOException {
		try (InputStream in = new FileInputStream(file)) {
			return FileUtils.fromCSV(in);
		}
	}
	
	@Override
	public int size(MultipartFile file) throws IOException {
		try (InputStream in = file.getInputStream()) {
			return FileUtils.sizeCSV(in);
		}
	}
	
	@Override
	public void merge(File mergedFile, List<File> files) throws IOException {
		if (mergedFile == null || files == null) {
			return;
		}
		FileUtils.mergeCSV(mergedFile, files);
	}

	@Override
	public FileTypes type() {
		return FileTypes.CSV;
	}

}
