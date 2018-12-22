package net.saisimon.agtms.core.util;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.saisimon.agtms.core.cache.Cache;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.factory.CacheFactory;

public class TokenUtils {
	
	private static final long MAX_TOKEN_TIMEOUT = 30 * 60 * 1000;
	public static final String TOKEN_SIGN = "X-TOKEN";
	
	private TokenUtils() {
		throw new IllegalAccessError();
	}
	
	public static String createToken() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replaceAll("\\-", "");
	}
	
	public static String getToken() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			return null;
		}
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		if (request == null) {
			return null;
		}
		return getToken(request);
	}
	
	public static String getToken(HttpServletRequest request) {
		String token = request.getHeader(TOKEN_SIGN);
		if (StringUtils.isBlank(token)) {
			token = request.getParameter(TOKEN_SIGN);
		}
		return token;
	}
	
	public static void refresh(String token) {
		if (StringUtils.isBlank(token)) {
			return;
		}
		CacheFactory.get().get(token, UserInfo.class);
	}
	
	public static UserInfo getUserInfo() {
		return getUserInfo(getToken());
	}
	
	public static UserInfo getUserInfo(String token) {
		if (StringUtils.isBlank(token)) {
			return null;
		}
		return CacheFactory.get().get(token, UserInfo.class);
	}
	
	public static void setUserInfo(String token, UserInfo userInfo) {
		if (StringUtils.isBlank(token)) {
			return;
		}
		Cache cache = CacheFactory.get();
		if (userInfo == null) {
			cache.delete(token);
		} else {
			cache.set(token, userInfo, MAX_TOKEN_TIMEOUT);
		}
	}
	
}
