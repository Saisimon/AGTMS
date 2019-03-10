package net.saisimon.agtms.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

import cn.hutool.core.date.DateUtil;

public final class FileUtils {
	
	private FileUtils() {
		throw new IllegalAccessError();
	}
	
	public static void toXLS(File file, List<List<Object>> datas, boolean append) throws IOException {
		if (file == null) {
			return;
		}
		try(Workbook wb = append ? new HSSFWorkbook(new FileInputStream(file)) : new HSSFWorkbook();
				FileOutputStream fileOut = new FileOutputStream(file)) {
			fillWorkbook(wb, datas);
			wb.write(fileOut);
		}
	}

	public static void toXLSX(File file, List<List<Object>> datas, boolean append) throws IOException {
		if (file == null) {
			return;
		}
		try(Workbook wb = append ? new XSSFWorkbook(new FileInputStream(file)) : new XSSFWorkbook();
				FileOutputStream fileOut = new FileOutputStream(file)) {
			fillWorkbook(wb, datas);
			wb.write(fileOut);
		}
	}
	
	public static void toCSV(File file, List<List<Object>> datas, String separator, boolean append) throws IOException {
		if (file == null) {
			return;
		}
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath(), append))) {
			if (null == separator) {
				separator = ",";
			}
			if (!CollectionUtils.isEmpty(datas)) {
				StringBuilder buffer = new StringBuilder();
				for (int i = 0; i < datas.size(); i++) {
					List<Object> data = datas.get(i);
					for (int j = 0; j < data.size(); j++) {
						Object obj = data.get(j);
						buffer.append(cellString(obj));
						if (j != data.size() - 1) {
							buffer.append(separator);
						}
					}
					bw.write(buffer.toString());
					bw.newLine();
					buffer.setLength(0);
				}
			}
			bw.flush();
		}
	}
	
	public static int sizeXLS(InputStream in) throws IOException {
		int size = 0;
		try (Workbook workbook = new HSSFWorkbook(in)) {
			for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
				Sheet sheet = workbook.getSheetAt(sheetIdx);
				if (sheet == null) {
					continue;
				}
				size += sheet.getLastRowNum();
			}
		}
		return size;
	}
	
	public static int sizeXLSX(InputStream in) throws IOException {
		int size = 0;
		try (Workbook workbook = new XSSFWorkbook(in)) {
			for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
				Sheet sheet = workbook.getSheetAt(sheetIdx);
				if (sheet == null) {
					continue;
				}
				size += sheet.getLastRowNum();
			}
		}
		return size;
	}
	
	public static int sizeCSV(InputStream in, String separator) throws IOException {
		int size = 0;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				if (StringUtils.isBlank(line)) {
					continue;
				}
				size++;
			}
		}
		return size;
	}
	
	public static Map<String, List<List<String>>> fromXLS(FileInputStream in) throws IOException {
		Map<String, List<List<String>>> result = new LinkedHashMap<>();
		try (Workbook workbook = new HSSFWorkbook(in)) {
			fillDatas(workbook, result);
		}
		return result;
	}

	public static Map<String, List<List<String>>> fromXLSX(FileInputStream in) throws IOException {
		Map<String, List<List<String>>> result = new LinkedHashMap<>();
		try (Workbook workbook = new XSSFWorkbook(in)) {
			fillDatas(workbook, result);
		}
		return result;
	}
	
	public static List<List<String>> fromCSV(FileInputStream in, String separator) throws IOException {
		List<List<String>> result = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				if (StringUtils.isBlank(line)) {
					continue;
				}
				String[] dataArray = line.split(separator);
				List<String> datas = new ArrayList<>(dataArray.length);
				for (String data : dataArray) {
					datas.add(data);
				}
				result.add(datas);
			}
		}
		return result;
	}
	
	public static void createDir(File dir) throws IOException {
		if (dir != null && !dir.exists()) {
			createDir(dir.getParentFile());
			dir.mkdirs();
		}
	}
	
	public static File createFile(String path, String name, String suffix) throws IOException {
		File file = new File(path + File.separator + name + suffix);
		createDir(file.getParentFile());
		return file;
	}
	
	private static void fillWorkbook(Workbook wb, List<List<Object>> datas) {
		if (!CollectionUtils.isEmpty(datas)) {
			Sheet sheet = null;
			int sheetSize = wb.getNumberOfSheets();
			if (sheetSize > 0) {
				sheet = wb.getSheetAt(sheetSize - 1);
			} else {
				sheet = wb.createSheet();
			}
			int start = sheet.getLastRowNum();
			Row row = sheet.getRow(start);
			if (row != null) {
				start++;
			}
			for (int i = 0; i < datas.size(); i++) {
				if (start == 65535) {
					sheet = wb.createSheet();
					start = sheet.getLastRowNum();
				}
				Row headRow = sheet.createRow(start);
				List<Object> data = datas.get(i);
				for (int j = 0; j < data.size(); j++) {
					Cell cell = headRow.createCell(j);
					Object obj = data.get(j);
					setValue(cell, obj);
				}
				start++;
			}
		}
	}
	
	private static void setValue(Cell cell, Object obj) {
		List<Object> list = SystemUtils.transformList(obj, Object.class);
		if (list != null) {
			String value = list.stream().map(Object::toString).collect(Collectors.joining(";"));
			cell.setCellValue(value);
			cell.setCellType(CellType.STRING);
		} else if (obj instanceof Integer) {
			Integer value = (Integer) obj;
			cell.setCellValue(value);
			cell.setCellType(CellType.NUMERIC);
		} else if (obj instanceof Double) {
			Double value = (Double) obj;
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(value);
		} else if (obj instanceof Float) {
			Float value = (Float) obj;
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(value);
		} else if (obj instanceof Long) {
			Long value = (Long) obj;
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(value);
		} else if (obj instanceof Date) {
			Date value = (Date) obj;
			cell.setCellValue(DateUtil.formatDate(value));
			cell.setCellType(CellType.STRING);
		} else if (obj instanceof Calendar) {
			Calendar value = (Calendar) obj;
			cell.setCellValue(DateUtil.formatDate(DateUtil.date(value)));
			cell.setCellType(CellType.STRING);
		} else if (obj instanceof RichTextString) {
			RichTextString value = (RichTextString) obj;
			cell.setCellValue(value);
		} else if (obj != null) {
			cell.setCellValue(obj.toString());
			cell.setCellType(CellType.STRING);
		} else {
			cell.setCellValue("");
			cell.setCellType(CellType.BLANK);
		}
	}
	
	private static String cellString(Object obj) {
		String value = "";
		List<Object> list = SystemUtils.transformList(obj, Object.class);
		if (list != null) {
			value = list.stream().map(Object::toString).collect(Collectors.joining(";"));
		} else if (obj instanceof RichTextString) {
			RichTextString v = (RichTextString) obj;
			value = v.getString();
		} else {
			value = obj.toString();
		}
		return value;
	}
	
	private static void fillDatas(Workbook workbook, Map<String, List<List<String>>> result) {
		for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
			Sheet sheet = workbook.getSheetAt(sheetIdx);
			if (sheet == null) {
				continue;
			}
			int rowCount = sheet.getLastRowNum() + 1;
			List<List<String>> datas = new ArrayList<>(rowCount);
			for (int rowIdx = sheet.getFirstRowNum(); rowIdx < rowCount; rowIdx++) {
				Row row = sheet.getRow(rowIdx);
				if (row == null) {
					continue;
				}
				int cellCount = row.getLastCellNum();
				List<String> data = new ArrayList<>(cellCount);
				for (int cellIdx = row.getFirstCellNum(); cellIdx < cellCount; cellIdx++) {
					Cell cell = row.getCell(cellIdx);
					if (cell == null) {
						continue;
					}
					String value;
					switch (cell.getCellTypeEnum()) {
					case NUMERIC:
						Double val = cell.getNumericCellValue();
						if (new Double(val.longValue()).doubleValue() == val.doubleValue()) {
							value = String.valueOf(val.intValue());
						} else {
							value = val.toString();
						}
						break;
					case STRING:
						value = cell.getStringCellValue();
						break;
					case BOOLEAN:
						value = String.valueOf(cell.getBooleanCellValue());
						break;
					default:
						value = "";
						break;
					}
					data.add(value);
				}
				datas.add(data);
			}
			result.put(sheet.getSheetName(), datas);
		}
	}
	
}
