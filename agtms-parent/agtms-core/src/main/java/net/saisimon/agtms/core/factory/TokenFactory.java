package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.exception.AgtmsException;
import net.saisimon.agtms.core.token.Token;

/**
 * 令牌构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class TokenFactory implements BeanPostProcessor {
	
	private static final List<Token> TOKENS = new ArrayList<>();
	
	public static Token get() {
		if (TOKENS.size() > 0) {
			return TOKENS.get(0);
		}
		throw new AgtmsException("获取 Token 失败");
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof Token)) {
			return bean;
		}
		Token token = (Token) bean;
		TOKENS.add(token);
		TOKENS.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
