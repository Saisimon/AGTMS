package net.saisimon.agtms.web.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.ui.freemarker.SpringTemplateLoader;
import org.springframework.util.CollectionUtils;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.saisimon.agtms.core.enums.FileTypes;
import net.saisimon.agtms.core.enums.ImageFormats;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 文件相关工具类
 * 
 * @author saisimon
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtils {

	private static final int XLS_MAX_ROWS = 65535;
	private static final int XLSX_MAX_ROWS = 1048575;
	
	public static final Map<String, String> CONTENT_TYPE_MAP = new HashMap<>();
	static {
		CONTENT_TYPE_MAP.put(FileTypes.XLS.getType(), "application/vnd.ms-excel");
		CONTENT_TYPE_MAP.put(FileTypes.XLSX.getType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		CONTENT_TYPE_MAP.put(FileTypes.CSV.getType(), "application/csv");
		CONTENT_TYPE_MAP.put(FileTypes.PDF.getType(), "application/pdf");
	}
	
	private static Configuration cfg = null;

	/**
	 * 将指定数据写入指定输出流
	 * 
	 * @param out   输出流
	 * @param datas  数据集合
	 * @throws IOException 写入异常
	 */
	public static void toXLS(OutputStream out, List<List<Object>> datas) throws IOException {
		try (Workbook wb = new HSSFWorkbook()) {
			fillWorkbook(wb, datas);
			wb.write(out);
		}
	}

	/**
	 * 将指定数据写入指定输出流
	 * 
	 * @param out   输出流
	 * @param datas  数据集合
	 * @throws IOException 写入异常
	 */
	public static void toXLSX(OutputStream out, List<List<Object>> datas) throws IOException {
		try (Workbook wb = new SXSSFWorkbook()) {
			fillWorkbook(wb, datas);
			wb.write(out);
		}
	}
	
	/**
	 * 将指定数据写入指定输出流
	 * 
	 * @param out      输出流
	 * @param datas     数据集合
	 * @throws IOException 写入异常
	 */
	public static void toCSV(OutputStream out, List<List<Object>> datas) throws IOException {
		try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {
			if (!CollectionUtils.isEmpty(datas)) {
				for (int i = 0; i < datas.size(); i++) {
					List<Object> data = datas.get(i);
					String[] dataArray = new String[data.size()];
					for (int j = 0; j < data.size(); j++) {
						dataArray[j] = cellString(data.get(j));
					}
					writer.writeNext(dataArray);
				}
			}
		}
	}

	/**
	 * 将指定数据写入指定输出流
	 * 
	 * @param out   输出流
	 * @param datas 数据集合
	 * @param fonts 字体文件路径
	 * @throws IOException       写入异常
	 * @throws TemplateException 模板解析异常
	 */
	public static void toPDF(OutputStream out, List<List<Object>> datas, String fontPath, String fontFamily) throws IOException, TemplateException {
		if (cfg == null) {
			cfg = initConfiguration();
		}
		ITextRenderer renderer = new ITextRenderer();
		if (fontPath != null) {
			addFont(renderer, fontPath);
		}
		Template temp = cfg.getTemplate("list.ftl");
		try (Writer writer = new StringWriter()) {
			Map<String, Object> dataModel = new HashMap<>();
			dataModel.put("font", fontFamily);
			dataModel.put("datas", datas);
			temp.process(dataModel, writer);
			renderer.setDocumentFromString(writer.toString());
			renderer.layout();
			renderer.createPDF(out);
		}
	}
	
	/**
	 * 合并多个 Excel 文件
	 * 
	 * @param mergedFile 合并后的文件
	 * @param files      待合并的文件
	 * @param isXlsx     合并文件类型是否是 xlsx
	 * @throws IOException       合并异常
	 */
	public static void mergeExcel(File mergedFile, List<File> files, boolean isXlsx) throws IOException {
		if (mergedFile == null || files == null) {
			return;
		}
		try (Workbook mergedWorkbook = isXlsx ? new SXSSFWorkbook() : new HSSFWorkbook();
				FileOutputStream out = new FileOutputStream(mergedFile)) {
			Sheet newSheet = mergedWorkbook.createSheet();
			int start = 0;
			for (File file : files) {
				if (file == null) {
					continue;
				}
				try (Workbook oldWorkbook = isXlsx ? new XSSFWorkbook(new FileInputStream(file)) : new HSSFWorkbook(new FileInputStream(file))) {
					int oldSheetSize = oldWorkbook.getNumberOfSheets();
					for (int i = 0; i < oldSheetSize; i++) {
						Sheet oldSheet = oldWorkbook.getSheetAt(i);
						int oldRowSize = oldSheet.getLastRowNum();
						for (int j = 0; j < oldRowSize; j++) {
							if (start == (isXlsx ? XLSX_MAX_ROWS : XLS_MAX_ROWS)) {
								newSheet = mergedWorkbook.createSheet();
								start = newSheet.getLastRowNum();
							}
							Row oldRow = oldSheet.getRow(j);
							Row newRow = newSheet.createRow(start);
							copyRow(oldRow, newRow);
							start++;
						}
					}
				}
			}
			mergedWorkbook.write(out);
		}
	}
	
	/**
	 * 合并多个 CSV 文件
	 * 
	 * @param mergedCsv 合并后的文件
	 * @param csvs      待合并的文件
	 * @throws IOException       合并异常
	 */
	public static void mergeCSV(File mergedCsv, List<File> csvs) throws IOException {
		if (mergedCsv == null || csvs == null) {
			return;
		}
		try (CSVWriter writer = new CSVWriter(new FileWriter(mergedCsv))) {
			for (File csv : csvs) {
				if (csv == null) {
					continue;
				}
				try (CSVReader reader = new CSVReaderBuilder(new FileReader(csv)).withCSVParser(new CSVParserBuilder().build()).build()) {
					String[] nextLineAsTokens = null;
					do {
						nextLineAsTokens = reader.readNext();
						if (nextLineAsTokens == null) {
							break;
						}
						writer.writeNext(nextLineAsTokens);
					} while (true);
				}
			}
		}
	}
	
	/**
	 * 合并多个 PDF 文件
	 * 
	 * @param mergedPdf 合并后的文件
	 * @param pdfs      待合并的文件
	 * @throws DocumentException 读取 PDF 文件异常
	 * @throws IOException       合并异常
	 */
	public static void mergePDF(File mergedPdf, List<File> pdfs) throws DocumentException, IOException {
		if (mergedPdf == null || pdfs == null) {
			return;
		}
		Document doc = new Document();
		PdfCopy copy = new PdfCopy(doc, new FileOutputStream(mergedPdf));
		doc.open();
		for (File pdf : pdfs) {
			if (pdf == null) {
				continue;
			}
			PdfReader pdfreader = new PdfReader(new FileInputStream(pdf));
			int n = pdfreader.getNumberOfPages();
			for (int i = 1; i <= n; i++) {
				copy.addPage(copy.getImportedPage(pdfreader, i));
			}
			copy.freeReader(pdfreader);
		}
		doc.close();
		copy.close();
	}

	/**
	 * 解析输入流，获取数据集合大小
	 * 
	 * @param in 输入流
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
	 * 解析输入流，获取数据集合大小
	 * 
	 * @param in 输入流
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
	 * 解析输入流，获取数据集合大小
	 * 
	 * @param in        输入流
	 * @param separator 分隔符
	 * @return 数据集合大小
	 * @throws IOException 解析读取异常
	 */
	public static int sizeCSV(InputStream in) throws IOException {
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
	 * 解析输入流，获取数据集合
	 * 
	 * @param in 输入流
	 * @return 数据集合，key 为工作表名称
	 * @throws IOException 解析读取异常
	 */
	public static Map<String, List<List<String>>> fromXLS(InputStream in) throws IOException {
		Map<String, List<List<String>>> result = new LinkedHashMap<>();
		try (Workbook workbook = new HSSFWorkbook(in)) {
			fillDatas(workbook, result);
		}
		return result;
	}

	/**
	 * 解析输入流，获取数据集合
	 * 
	 * @param in 输入流
	 * @return 数据集合，key 为工作表名称
	 * @throws IOException 解析读取异常
	 */
	public static Map<String, List<List<String>>> fromXLSX(InputStream in) throws IOException {
		Map<String, List<List<String>>> result = new LinkedHashMap<>();
		try (Workbook workbook = new XSSFWorkbook(in)) {
			fillDatas(workbook, result);
		}
		return result;
	}

	/**
	 * 解析输入流，获取数据集合
	 * 
	 * @param in 输入流
	 * @return 数据集合
	 * @throws IOException 解析读取异常
	 */
	public static List<List<String>> fromCSV(InputStream in) throws IOException {
		try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(in)).withCSVParser(new CSVParserBuilder().build()).build()) {
			List<List<String>> result = new ArrayList<>();
			String[] nextLineAsTokens = null;
			do {
				nextLineAsTokens = reader.readNext();
				if (nextLineAsTokens == null) {
					break;
				}
				List<String> datas = new ArrayList<>(nextLineAsTokens.length);
				for (String data : nextLineAsTokens) {
					datas.add(data);
				}
				result.add(datas);
			} while (true);
			return result;
		}
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
	 * @param name 图片文件名
	 * @return 图片格式
	 */
	public static ImageFormats imageFormat(String name) {
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
	 * 创建指定文件
	 * 
	 * @param path   文件所在路径
	 * @param name   文件名称
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
		FileUtil.mkParentDirs(file);
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

	private static Configuration initConfiguration() throws IOException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);
		cfg.setTemplateLoader(new SpringTemplateLoader(new DefaultResourceLoader(), "classpath:/templates/"));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
		cfg.setWrapUncheckedExceptions(true);
		return cfg;
	}

	private static void addFont(ITextRenderer renderer, String... fonts) throws DocumentException, IOException {
		ITextFontResolver fontResolver = renderer.getFontResolver();
		for (String font : fonts) {
			if (font == null) {
				continue;
			}
			try {
				fontResolver.addFont(font, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			} catch (Exception e) {
				throw new IllegalArgumentException(String.format("%s字体添加失败", font), e);
			}
		}
	}
	
	private static void copyRow(Row oldRow, Row newRow) {
		newRow.setHeight(oldRow.getHeight());
		for (int i = oldRow.getFirstCellNum(); i <= oldRow.getLastCellNum(); i++) {
			Cell oldCell = oldRow.getCell(i);
			if (null != oldCell) {
				copyCell(oldCell, newRow.createCell(i));
			}
		}
	}
	
	private static void copyCell(Cell oldCell, Cell newCell) {
		switch (oldCell.getCellTypeEnum()) {
			case FORMULA:
				newCell.setCellFormula(oldCell.getCellFormula());
				break;
			case NUMERIC:
				if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(oldCell)) {
					newCell.setCellValue(DateUtil.formatDate(oldCell.getDateCellValue()));
				} else {
					newCell.setCellValue(oldCell.getNumericCellValue());
				}
				break;
			case BLANK:
				newCell.setCellValue(oldCell.getStringCellValue());
				break;
			case BOOLEAN:
				newCell.setCellValue(oldCell.getBooleanCellValue());
				break;
			case STRING:
				newCell.setCellValue(oldCell.getStringCellValue());
				break;
			default:
				break;
		}
	}

}
