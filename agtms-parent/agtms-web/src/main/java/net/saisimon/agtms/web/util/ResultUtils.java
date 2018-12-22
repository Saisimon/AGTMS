package net.saisimon.agtms.web.util;

import net.saisimon.agtms.web.dto.resp.common.PageResult;
import net.saisimon.agtms.web.dto.resp.common.Result;
import net.saisimon.agtms.web.dto.resp.common.SimpleResult;

public class ResultUtils {
	
	public static final int SUCCESS_CODE = 0;
	public static final String SUCCESS_MESSAGE = "success";

	public static boolean isSuccess(Result result) {
		return result != null && result.getCode() == SUCCESS_CODE;
	}
	
	public static Result success() {
		return success(null);
	}
	
	public static <T> Result success(T data) {
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
	
	public static Result error(int code, String message, Object... messageArgs) {
		Result result = new Result();
		result.setCode(code);
		result.setMessage(message);
		result.setMessageArgs(messageArgs);
		return result;
	}
	
}
