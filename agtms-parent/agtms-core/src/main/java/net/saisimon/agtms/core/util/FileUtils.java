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

import org.apache.commons.codec.binary.Hex;
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
import net.saisimon.agtms.core.enums.ImageFormats;

/**
 * 文件相关工具类
 * 
 * @author saisimon
 *
 */
public final class FileUtils {
	
	private FileUtils() {
		throw new IllegalAccessError();
	}
	
	/**
	 * 将指定数据写入指定XLS文件
	 * 
	 * @param file XLS文件
	 * @param datas 数据集合
	 * @param append 是否追加
	 * @throws IOException 写入异常
	 */
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

	/**
	 * 将指定数据写入指定XLSX文件
	 * 
	 * @param file XLSX文件
	 * @param datas 数据集合
	 * @param append 是否追加
	 * @throws IOException 写入异常
	 */
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
	
	/**
	 * 将指定数据写入指定CSV文件
	 * 
	 * @param file CSV文件
	 * @param datas 数据集合
	 * @param separator 分隔符，默认为逗号
	 * @param append 是否追加
	 * @throws IOException 写入异常
	 */
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
	
	/**
	 * 解析输入XLS文件流，获取数据集合大小
	 * 
	 * @param in XLS文件流
	 * @return 数据集合大小
	 * @throws IOException 解析读取异常
	 */
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
	
	/**
	 * 解析输入XLSX文件流，获取数据集合大小
	 * 
	 * @param in XLSX文件流
	 * @return 数据集合大小
	 * @throws IOException 解析读取异常
	 */
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
	
	/**
	 * 解析输入CSV文件流，获取数据集合大小
	 * 
	 * @param in CSV文件流
	 * @param separator 分隔符
	 * @return 数据集合大小
	 * @throws IOException 解析读取异常
	 */
	public static int sizeCSV(InputStream in, String separator) throws IOException {
		int size = 0;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				if (SystemUtils.isBlank(line)) {
					continue;
				}
				size++;
			}
		}
		return size;
	}
	
	/**
	 * 解析输入XLS文件流，获取数据集合
	 * 
	 * @param in XLS文件流
	 * @return 数据集合，key为工作表名称
	 * @throws IOException 解析读取异常
	 */
	public static Map<String, List<List<String>>> fromXLS(FileInputStream in) throws IOException {
		Map<String, List<List<String>>> result = new LinkedHashMap<>();
		try (Workbook workbook = new HSSFWorkbook(in)) {
			fillDatas(workbook, result);
		}
		return result;
	}

	/**
	 * 解析输入XLSX文件流，获取数据集合
	 * 
	 * @param in XLSX文件流
	 * @return 数据集合，key为工作表名称
	 * @throws IOException 解析读取异常
	 */
	public static Map<String, List<List<String>>> fromXLSX(FileInputStream in) throws IOException {
		Map<String, List<List<String>>> result = new LinkedHashMap<>();
		try (Workbook workbook = new XSSFWorkbook(in)) {
			fillDatas(workbook, result);
		}
		return result;
	}
	
	/**
	 * 解析输入CSV文件流，获取数据集合
	 * 
	 * @param in CSV文件流
	 * @param separator 分隔符，默认为逗号
	 * @return 数据集合
	 * @throws IOException 解析读取异常
	 */
	public static List<List<String>> fromCSV(FileInputStream in, String separator) throws IOException {
		List<List<String>> result = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			if (separator == null) {
				separator = ",";
			}
			String line = null;
			while ((line = br.readLine()) != null) {
				if (SystemUtils.isBlank(line)) {
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
	
	/**
	 * 判断输入流的图片格式
	 * 
	 * @param input 输入流
	 * @return 图片格式
	 * @throws IOException 读取异常
	 */
	public static ImageFormats imageFormat(InputStream input) throws IOException {
		if (input == null) {
			return ImageFormats.UNKNOWN;
		}
		try {
			byte[] bs = new byte[4];
			input.read(bs, 0, bs.length);
			String hex = Hex.encodeHexString(bs, false);
			if (hex.contains("FFD8FF")) {
				return ImageFormats.JPG;
			} else if (hex.contains("89504E47")) {
				return ImageFormats.PNG;
			} else if (hex.contains("47494638")) {
				return ImageFormats.GIF;
			} else if (hex.contains("424D")) {
				return ImageFormats.BMP;
			} else {
				return ImageFormats.UNKNOWN;
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	/**
	 * 根据文件名称判断图片格式
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static ImageFormats imageFormat(String name) throws IOException {
		if (SystemUtils.isBlank(name)) {
			return ImageFormats.UNKNOWN;
		}
		for (ImageFormats imageFormats : ImageFormats.values()) {
			if (name.endsWith(imageFormats.getSuffix())) {
				return imageFormats;
			}
		}
		return ImageFormats.UNKNOWN;
	}
	
	/**
	 * 创建指定文件夹
	 * 
	 * @param dir 文件夹
	 * @throws IOException 创建文件夹异常
	 */
	public static void createDir(File dir) throws IOException {
		if (dir != null && !dir.exists()) {
			createDir(dir.getParentFile());
			dir.mkdirs();
		}
	}
	
	/**
	 * 创建指定文件
	 * 
	 * @param path 文件所在路径
	 * @param name 文件名称
	 * @param suffix 文件后缀名
	 * @return 文件
	 * @throws IOException 创建文件异常
	 */
	public static File createFile(String path, String name, String suffix) throws IOException {
		if (SystemUtils.isBlank(path) || SystemUtils.isBlank(name)) {
			return null;
		}
		String filePath = path + File.separator + name;
		if (SystemUtils.isNotBlank(suffix)) {
			filePath += suffix;
		}
		File file = new File(filePath);
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
		List<Object> list = SystemUtils.transformList(obj);
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
		List<Object> list = SystemUtils.transformList(obj);
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
