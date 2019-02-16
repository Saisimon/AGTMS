package net.saisimon.agtms.web.constant;

import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.util.ResultUtils;

/**
 * 错误消息响应
 * 
 * @author saisimon
 *
 */
public class ErrorMessage {
	
	public static class Common {
		public static final Result SERVER_ERROR = ResultUtils.error(500, "server.error");
		public static final Result MISSING_REQUIRED_FIELD = ResultUtils.error(501, "missing.required.field");
		public static final Result PARAM_ERROR = ResultUtils.error(502, "param.error");
	}
	
	public static class User {
		public static final Result USERNAME_OR_PASSWORD_NOT_CORRECT = ResultUtils.error(1001, "username.or.password.not.correct");
		public static final Result ACCOUNT_ALREADY_EXISTS = ResultUtils.error(1002, "account.already.exists");
	}
	
	public static class Navigation {
		public static final Result NAVIGATION_NOT_EXIST = ResultUtils.error(2001, "navigation.not.exist");
		public static final Result NAVIGATION_ALREADY_EXISTS = ResultUtils.error(2002, "navigation.already.exists");
	}
	
	public static class Template {
		public static final Result TEMPLATE_NOT_EXIST = ResultUtils.error(3001, "template.not.exist");
		public static final Result TEMPLATE_ALREADY_EXISTS = ResultUtils.error(3002, "template.already.exists");
		public static final Result TEMPLATE_NO_FUNCTION = ResultUtils.error(3003, "template.no.function");
		public static final Result TEMPLATE_SIZE_ERROR = ResultUtils.error(3003, "template.size.error");
	}
	
	public static class Domain {
		public static final Result DOMAIN_NOT_EXIST = ResultUtils.error(4001, "domain.not.exist");
		public static final Result DOMAIN_ALREADY_EXISTS = ResultUtils.error(4002, "domain.already.exists");
		public static final Result DOMAIN_SAVE_FAILED = ResultUtils.error(4003, "domain.save.failed");
	}
	
	public static class Task {
		public static final Result TASK_NOT_EXIST = ResultUtils.error(5001, "task.not.exist");
		public static final Result TASK_CANCEL = ResultUtils.error(5002, "task.cancel");
		
		public static class EXPORT {
			public static final Result TASK_EXPORT_FAILED = ResultUtils.error(5101, "export.failed");
			public static final Result TASK_XLS_FILE_MAX_SIZE_LIMIT = ResultUtils.error(5102, "xls.file.max.size.limit");
		}
		
		public static class IMPORT {
			public static final Result TASK_IMPORT_FAILED = ResultUtils.error(5201, "import.failed");
		}
	}
	
}
