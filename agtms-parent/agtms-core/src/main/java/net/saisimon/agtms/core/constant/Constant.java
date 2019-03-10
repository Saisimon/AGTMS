package net.saisimon.agtms.core.constant;

public class Constant {
	
	public static final String MONGODBID = "_id";
	public static final String ID = "id";
	public static final String OPERATORID = "operatorId";
	public static final String CREATETIME = "createTime";
	public static final String UPDATETIME = "updateTime";
	
	public static class Param {
		public static final String TOTAL = "total";
		public static final String CONTENT = "content";
		public static final String DOMAIN = "domain";
		public static final String FIELDS = "fields";
		public static final String UPDATE = "update";
		public static final String FILTER = "filter";
		public static final String PAGEABLE = "pageable";
		public static final String INDEX = "index";
		public static final String SIZE = "size";
		public static final String SORT = "sort";
	}
	
	public static class Operator {
		public static final String AND = "$and";
		public static final String OR = "$or";
		public static final String NOR = "$nor";
		public static final String EQ = "$eq";
		public static final String NE = "$ne";
		public static final String LT = "$lt";
		public static final String LTE = "$lte";
		public static final String GT = "$gt";
		public static final String GTE = "$gte";
		public static final String REGEX = "$regex";
		public static final String IN = "$in";
		public static final String NIN = "$nin";
		public static final String ALL = "$all";
		public static final String EXISTS = "$exists";
	}
	
	public static class File {
		
		public static final String XLS = "xls";
		public static final String XLSX = "xlsx";
		public static final String CSV = "csv";
		
		public static final String XLS_SUFFIX = ".xls";
		public static final String XLSX_SUFFIX = ".xlsx";
		public static final String CSV_SUFFIX = ".csv";
		
		public static final String EXPORT_PATH = System.getProperty("java.io.tmpdir") + "export";
		public static final String IMPORT_PATH = System.getProperty("java.io.tmpdir") + "import";
		
	}
	
}
