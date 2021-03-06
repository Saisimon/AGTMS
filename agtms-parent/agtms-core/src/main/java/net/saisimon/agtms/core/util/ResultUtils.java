package net.saisimon.agtms.core.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.saisimon.agtms.core.dto.PageResult;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.dto.SimpleResult;

/**
 * 结果相关工具类
 * 
 * @author saisimon
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultUtils {
	
	public static final int SUCCESS_CODE = 0;
	public static final String SUCCESS_MESSAGE = "success";
	public static final int ERROR_CODE = 500;
	public static final String ERROR_MESSAGE = "error";
	
	private static final Result SUCCESS = simpleSuccess(null);

	/**
	 * 指定结果对象状态
	 * 
	 * @param result 结果对象
	 * @return 状态
	 */
	public static boolean isSuccess(Result result) {
		return result != null && result.getCode() == SUCCESS_CODE;
	}
	
	/**
	 * 创建空简单结果对象
	 * 
	 * @return 结果对象
	 */
	public static Result simpleSuccess() {
		return SUCCESS;
	}
	
	/**
	 * 创建简单结果对象
	 * 
	 * @param data 结果中的数据
	 * @return 结果对象
	 */
	public static <T> Result simpleSuccess(T data) {
		SimpleResult<T> result = new SimpleResult<>();
		result.setCode(SUCCESS_CODE);
		result.setMessage(SUCCESS_MESSAGE);
		result.setData(data);
		return result;
	}
	
	/**
	 * 创建分页结果对象
	 * 
	 * @param rows 数据集合
	 * @param total 总数
	 * @return 分页结果对象
	 */
	public static <T> Result pageSuccess(Iterable<T> rows, boolean more) {
		PageResult<T> pageResult = new PageResult<>();
		pageResult.setCode(SUCCESS_CODE);
		pageResult.setMessage(SUCCESS_MESSAGE);
		if (rows != null) {
			pageResult.setRows(rows);
		}
		pageResult.setMore(more);
		return pageResult;
	}
	
	/**
	 * 默认错误结果对象
	 * 
	 * @return 错误结果对象
	 */
	public static Result error() {
		return error(ERROR_CODE, ERROR_MESSAGE);
	}
	
	/**
	 * 创建错误结果对象
	 * 
	 * @param code 错误码
	 * @param message 错误消息
	 * @param messageArgs 错误消息的参数
	 * @return 错误结果对象
	 */
	public static Result error(int code, String message, Object... messageArgs) {
		Result result = new Result();
		result.setCode(code);
		result.setMessage(message);
		result.setMessageArgs(messageArgs);
		return result;
	}
	
}
