package net.saisimon.agtms.zuul.config.handler.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理
 * 
 * @author saisimon
 *
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public void exception(HttpServletRequest request, Exception exception) {
		log.error(request.getRequestURI(), exception);
	}
	
}
