package net.saisimon.agtms.core.util;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.saisimon.agtms.core.cache.Cache;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.factory.CacheFactory;

public class AuthUtils {
	
	private static final long MAX_TOKEN_TIMEOUT = 30 * 60 * 1000;
	public static final String AUTHORIZE_UID = "X-UID";
	
	private AuthUtils() {
		throw new IllegalAccessError();
	}
	
	public static String createToken() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replaceAll("\\-", "");
	}
	
	public static String getUid() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			return null;
		}
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		if (request == null) {
			return null;
		}
		return getUid(request);
	}
	
	public static String getUid(HttpServletRequest request) {
		String uid = request.getHeader(AUTHORIZE_UID);
		if (StringUtils.isBlank(uid)) {
			uid = request.getParameter(AUTHORIZE_UID);
		}
		return uid;
	}
	
	public static UserInfo getUserInfo() {
		return getUserInfo(getUid());
	}
	
	public static UserInfo getUserInfo(String uid) {
		if (uid == null) {
			return null;
		}
		return CacheFactory.get().get(uid.toString(), UserInfo.class);
	}
	
	public static void setUserInfo(String uid, UserInfo userInfo) {
		if (uid == null) {
			return;
		}
		Cache cache = CacheFactory.get();
		if (userInfo == null) {
			cache.delete(uid);
		} else {
			cache.set(uid, userInfo, MAX_TOKEN_TIMEOUT);
		}
	}
	
}
