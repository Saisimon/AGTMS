package net.saisimon.agtms.core.util;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.dto.TokenInfo;
import net.saisimon.agtms.core.factory.TokenFactory;

/**
 * 用户相关工具类
 * 
 * @author saisimon
 *
 */
public class AuthUtils {
	
	private static final long MAX_TOKEN_TIMEOUT = 30 * 60 * 1000;
	public static final String AUTHORIZE_TOKEN = "X-TOKEN";
	public static final String AUTHORIZE_UID = "X-UID";
	
	private AuthUtils() {
		throw new IllegalAccessError();
	}
	
	/**
	 * 创建 Token，去掉“-”的UUID字符串
	 * 
	 * @return Token
	 */
	public static String createToken() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replaceAll("\\-", "");
	}
	
	/**
	 * 获取过期时间（当前时间 + 30分钟）
	 * 单位：ms
	 * 
	 * @return
	 */
	public static long getExpireTime() {
		return System.currentTimeMillis() + MAX_TOKEN_TIMEOUT;
	}
	
	/**
	 * 获取当前线程的请求中的用户ID
	 * 
	 * @return 用户ID
	 */
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
	
	/**
	 * 获取请求中的用户ID
	 * 
	 * @param request HTTP 请求
	 * @return 用户ID
	 */
	public static String getUid(HttpServletRequest request) {
		String uid = request.getHeader(AUTHORIZE_UID);
		if (StringUtils.isBlank(uid)) {
			uid = request.getParameter(AUTHORIZE_UID);
		}
		return uid;
	}
	
	/**
	 * 获取 Token 信息
	 * 
	 * @return 用户信息
	 * @see net.saisimon.agtms.core.dto.TokenInfo
	 */
	public static TokenInfo getTokenInfo() {
		return getTokenInfo(getUid());
	}
	
	/**
	 * 根据指定用户ID的 Token 信息
	 * 
	 * @param uid 用户ID
	 * @return Token 信息
	 * @see net.saisimon.agtms.core.dto.TokenInfo
	 */
	public static TokenInfo getTokenInfo(String uid) {
		if (uid == null || !NumberUtil.isLong(uid)) {
			return null;
		}
		return TokenFactory.get().getTokenInfo(Long.valueOf(uid));
	}
	
	/**
	 * 设置指定用户ID的 Token 信息
	 * 
	 * @param uid 用户ID
	 * @param tokenInfo Token信息
	 * @see net.saisimon.agtms.core.dto.TokenInfo
	 */
	public static void setTokenInfo(String uid, TokenInfo tokenInfo) {
		if (uid == null || !NumberUtil.isLong(uid)) {
			return;
		}
		TokenFactory.get().setTokenInfo(Long.valueOf(uid), tokenInfo);
	}
	
	/**
	 * 创建10位随机盐
	 * 
	 * @return 随机盐
	 */
	public static String createSalt() {
		return RandomUtil.randomString(10);
	}

	/**
	 * 加盐的 sha-256 摘要
	 * 
	 * @param password 待摘要字符串
	 * @param salt 随机盐
	 * @return 摘要
	 */
	public static String hmac(String password, String salt) {
		return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, salt).hmacHex(password).toUpperCase();
	}
	
	public static TokenInfo buildTokenInfo(User user) {
		if (user == null) {
			return null;
		}
		TokenInfo userInfo = new TokenInfo();
		userInfo.setUserId(user.getId());
		userInfo.setExpireTime(user.getExpireTime());
		userInfo.setToken(user.getToken());
		return userInfo;
	}
	
}
