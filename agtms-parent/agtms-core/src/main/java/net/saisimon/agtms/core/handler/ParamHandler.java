package net.saisimon.agtms.core.handler;

/**
 * 参数处理器接口
 * 
 * @author saisimon
 *
 * @param <T> 参数处理后的类型
 */
public interface ParamHandler<T> {
	
	T handler(Object param);
	
}
