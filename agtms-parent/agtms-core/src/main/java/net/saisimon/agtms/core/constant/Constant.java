package net.saisimon.agtms.core.constant;

/**
 * 常量类
 * 
 * @author saisimon
 *
 */
public class Constant {
	
	public static final Long SYSTEM_OPERATORID = 0L;
	
	public static final String MONGODBID = "_id";
	public static final String ID = "id";
	public static final String OPERATORID = "operatorId";
	public static final String CREATETIME = "createTime";
	public static final String UPDATETIME = "updateTime";
	
	public static class Param {
		public static final String TOTAL = "total";
		public static final String ROWS = "rows";
		public static final String UPDATE = "update";
		public static final String FILTER = "filter";
		public static final String PAGEABLE = "pageable";
		public static final String PROPERTIES = "properties";
		public static final String INDEX = "index";
		public static final String SIZE = "size";
		public static final String SORT = "sort";
		public static final String PARAM = "param";
		public static final String COUNT = "count";
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
		public static final String LREGEX = "$lregex";
		public static final String RREGEX = "$rregex";
		public static final String NREGEX = "$nregex";
		public static final String LNREGEX = "$lnregex";
		public static final String RNREGEX = "$rnregex";
		public static final String IN = "$in";
		public static final String NIN = "$nin";
		public static final String EXISTS = "$exists";
	}
	
	public static class File {
		
		public static final String EXPORT_PATH = "export";
		public static final String IMPORT_PATH = "import";
		public static final String UPLOAD_PATH = "upload";
		
	}
	
	public static class Cache {
		
		public static final String RESOURCE_IDS_NAME = "getResourceIds";
		public static final String USER_IDS_NAME = "getUserIds";
		
	}
	
}
