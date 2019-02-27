package net.saisimon.agtms.core.util;

import net.saisimon.agtms.core.dto.PageResult;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.dto.SimpleResult;

public class ResultUtils {
	
	public static final int SUCCESS_CODE = 0;
	public static final String SUCCESS_MESSAGE = "success";
	public static final int ERROR_CODE = 500;
	public static final String ERROR_MESSAGE = "error";

	public static boolean isSuccess(Result result) {
		return result != null && result.getCode() == SUCCESS_CODE;
	}
	
	public static Result simpleSuccess() {
		return simpleSuccess(null);
	}
	
	public static <T> Result simpleSuccess(T data) {
		SimpleResult<T> result = new SimpleResult<>();
		result.setCode(SUCCESS_CODE);
		result.setMessage(SUCCESS_MESSAGE);
		result.setData(data);
		return result;
	}
	
	public static <T> Result pageSuccess(Iterable<T> rows, long total) {
		PageResult<T> pageResult = new PageResult<>();
		pageResult.setCode(SUCCESS_CODE);
		pageResult.setMessage(SUCCESS_MESSAGE);
		if (rows != null) {
			pageResult.setRows(rows);
		}
		pageResult.setTotal(total);
		return pageResult;
	}
	
	public static Result error() {
		return error(ERROR_CODE, ERROR_MESSAGE);
	}
	
	public static Result error(int code, String message, Object... messageArgs) {
		Result result = new Result();
		result.setCode(code);
		result.setMessage(message);
		result.setMessageArgs(messageArgs);
		return result;
	}
	
}
