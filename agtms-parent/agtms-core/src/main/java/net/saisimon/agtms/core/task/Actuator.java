package net.saisimon.agtms.core.task;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.saisimon.agtms.core.domain.entity.Task;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 执行器接口
 * 
 * @author saisimon
 *
 * @param <P>
 */
public interface Actuator<P> {
	
	/**
	 * 根据输入参数执行操作
	 * 
	 * @param param 输入参数
	 * @return 执行结果
	 * @throws Exception 执行异常
	 */
	Result execute(P param) throws Exception;
	
	/**
	 * 构建任务内容说明
	 * 
	 * @param task 任务
	 * @return 任务内容
	 */
	String taskContent(Task task);
	
	/**
	 * 任务结果输出
	 * 
	 * @param task 任务
	 * @param request http 请求
	 * @param response http 响应
	 */
	void download(Task task, HttpServletRequest request, HttpServletResponse response) throws IOException;
	
	/**
	 * 获取执行器标志
	 * 
	 * @return 执行器标志
	 */
	Sign sign();
	
	/**
	 * 获取执行器的参数类型
	 * 
	 * @return 执行器的参数类型
	 */
	@SuppressWarnings("unchecked")
	default Class<P> getParamClass() {
		return (Class<P>) SystemUtils.getInterfaceGenericClass(getClass(), Actuator.class, 0);
	}
	
}
