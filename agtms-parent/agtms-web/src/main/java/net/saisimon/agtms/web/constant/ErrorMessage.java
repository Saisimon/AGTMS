package net.saisimon.agtms.web.constant;

import net.saisimon.agtms.web.dto.resp.common.Result;
import net.saisimon.agtms.web.util.ResultUtils;

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
	}
	
}
