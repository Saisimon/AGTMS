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
		public static final Result SERVER_ERROR = ResultUtils.error(501, "server.error");
		public static final Result MISSING_REQUIRED_FIELD = ResultUtils.error(502, "missing.required.field");
		public static final Result PARAM_ERROR = ResultUtils.error(503, "param.error");
		public static final Result PERMISSION_DENIED = ResultUtils.error(504, "permission.denied");
		public static final Result UNSUPPORTED_FORMAT = ResultUtils.error(505, "unsupported.format");
		public static final Result UPLOAD_FAILED = ResultUtils.error(506, "upload.failed");
		public static final Result FIELD_LENGTH_OVERFLOW = ResultUtils.error(507, "field.length.overflow");
		public static final Result FIELD_FORMAT_NOT_CORRECT = ResultUtils.error(508, "field.format.incorrect");
		public static final Result UNSUPPORTED_FILE_TYPE = ResultUtils.error(509, "unsupported.file.type");
	}
	
	public static class User {
		public static final Result USERNAME_OR_PASSWORD_NOT_CORRECT = ResultUtils.error(1001, "username.or.password.incorrect");
		public static final Result ACCOUNT_ALREADY_EXISTS = ResultUtils.error(1002, "account.already.exists");
		public static final Result EMAIL_FORMAT_ERROR = ResultUtils.error(1003, "email.format.error");
		public static final Result ACCOUNT_NOT_EXIST = ResultUtils.error(1004, "account.not.exist");
		public static final Result ACCOUNT_LOCKED = ResultUtils.error(1005, "account.locked");
		public static final Result OLD_PASSWORD_NOT_CORRECT = ResultUtils.error(1006, "old.password.incorrect");
		public static final Result PASSWORD_NEED_RESET = ResultUtils.error(1007, "password.need.reset");
	}
	
	public static class Navigation {
		public static final Result NAVIGATION_NOT_EXIST = ResultUtils.error(2001, "navigation.not.exist");
		public static final Result NAVIGATION_ALREADY_EXISTS = ResultUtils.error(2002, "navigation.already.exists");
		public static final Result NAVIGATION_MAX_DEPTH_LIMIT = ResultUtils.error(2003, "navigation.max.depth.limit");
	}
	
	public static class Template {
		public static final Result TEMPLATE_NOT_EXIST = ResultUtils.error(3001, "template.not.exist");
		public static final Result TEMPLATE_ALREADY_EXISTS = ResultUtils.error(3002, "template.already.exists");
		public static final Result TEMPLATE_NO_FUNCTION = ResultUtils.error(3003, "template.no.function");
		public static final Result TEMPLATE_CREATE_FAILED = ResultUtils.error(3005, "template.create.failed");
		public static final Result TEMPLATE_MODIFY_FAILED = ResultUtils.error(3006, "template.modify.failed");
	}
	
	public static class Domain {
		public static final Result DOMAIN_NOT_EXIST = ResultUtils.error(4001, "domain.not.exist");
		public static final Result DOMAIN_ALREADY_EXISTS = ResultUtils.error(4002, "domain.already.exists");
		public static final Result DOMAIN_SAVE_FAILED = ResultUtils.error(4003, "domain.save.failed");
	}
	
	public static class Task {
		public static final Result TASK_NOT_EXIST = ResultUtils.error(5001, "task.not.exist");
		public static final Result TASK_CANCEL = ResultUtils.error(5002, "task.cancel");
		public static final Result TASK_MAX_SIZE_LIMIT = ResultUtils.error(5003, "task.max.size.limit");
		public static final Result TASK_CANCEL_FAILED = ResultUtils.error(5004, "task.cancel.failed");
		
		public static class Export {
			public static final Result TASK_EXPORT_FAILED = ResultUtils.error(5101, "export.failed");
			public static final Result TASK_EXPORT_MAX_SIZE_LIMIT = ResultUtils.error(5102, "export.max.size.limit");
		}
		
		public static class Import {
			public static final Result TASK_IMPORT_FAILED = ResultUtils.error(5201, "import.failed");
			public static final Result TASK_IMPORT_MAX_SIZE_LIMIT = ResultUtils.error(5202, "import.max.size.limit");
			public static final Result TASK_IMPORT_SIZE_EMPTY = ResultUtils.error(5203, "import.size.empty");
			public static final Result TASK_IMPORT_FILE_MAX_SIZE_LIMIT = ResultUtils.error(5204, "import.file.max.size.limit");
		}
	}
	
	public static class Selection {
		public static final Result SELECTION_NOT_EXIST = ResultUtils.error(6001, "selection.not.exist");
		public static final Result SELECTION_ALREADY_EXISTS = ResultUtils.error(6002, "selection.already.exists");
	}
	
	public static class Role {
		public static final Result ROLE_NOT_EXIST = ResultUtils.error(7001, "role.not.exist");
		public static final Result ROLE_ALREADY_EXISTS = ResultUtils.error(7002, "role.already.exists");
		public static final Result ROLE_MAX_DEPTH_LIMIT = ResultUtils.error(7003, "role.max.depth.limit");
	}
	
	public static class Notification {
		public static final Result NOTIFICATION_NOT_EXIST = ResultUtils.error(8001, "notification.not.exist");
	}
	
}
