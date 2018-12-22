package net.saisimon.agtms.core.constant;

public final class FileConstant {
	
	public static final String XLS = "xls";
	public static final String XLSX = "xlsx";
	public static final String CSV = "csv";
	public static final String JSON = "json";
	
	public static final String XLS_SUFFIX = ".xls";
	public static final String XLSX_SUFFIX = ".xlsx";
	public static final String CSV_SUFFIX = ".csv";
	public static final String JSON_SUFFIX = ".json";
	
	public static final String EXPORT_PATH = System.getProperty("java.io.tmpdir") + "export";
	public static final String IMPORT_PATH = System.getProperty("java.io.tmpdir") + "import";
	
	public static final int MAX_IMPORT_SIZE = 100 * 1024 * 1024;
	
}
