package net.saisimon.agtms.core.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.hutool.core.util.RandomUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * 认证相关工具类
 * 
 * @author saisimon
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthUtils {
	
	private static final long MAX_TOKEN_TIMEOUT = 30 * 60 * 1000;
	public static final String AUTHORIZE_TOKEN = "X-TOKEN";
	public static final String AUTHORIZE_UID = "X-UID";
	
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
	public static Long getUid() {
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
	public static Long getUid(HttpServletRequest request) {
		String uid = request.getHeader(AUTHORIZE_UID);
		if (SystemUtils.isBlank(uid)) {
			uid = request.getParameter(AUTHORIZE_UID);
		}
		if (SystemUtils.isBlank(uid)) {
			return null;
		}
		try {
			return Long.valueOf(uid);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * 获取当前线程的请求中的 Token
	 * 
	 * @return Token
	 */
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
	
	/**
	 * 获取请求中的 Token
	 * 
	 * @param request HTTP 请求
	 * @return Token 
	 */
	public static String getToken(HttpServletRequest request) {
		String token = request.getHeader(AUTHORIZE_TOKEN);
		if (SystemUtils.isBlank(token)) {
			token = request.getParameter(AUTHORIZE_TOKEN);
		}
		return token;
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
	
	/**
	 * AES 加密
	 * 
	 * @param text
	 * @param key
	 * @return
	 */
	public static String aesEncrypt(String text, String key) throws Exception {
		if (text == null || key == null) {
			return text;
		}
		Cipher cipher = buildAESCipher(Cipher.ENCRYPT_MODE, key);
		return Base64.encodeBase64String(cipher.doFinal(text.getBytes("UTF-8")));
	}
	
	/**
	 * AES 解密
	 * 
	 * @param ciphertext
	 * @param key
	 * @return
	 */
	public static String aesDecrypt(String ciphertext, String key) throws Exception {
		if (ciphertext == null || key == null) {
			return ciphertext;
		}
		Cipher cipher = buildAESCipher(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(Base64.decodeBase64(ciphertext)), "UTF-8");
	}
	
	private static Cipher buildAESCipher(int mode, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		Cipher result = Cipher.getInstance("AES");
		result.init(mode, new SecretKeySpec(DigestUtils.md5(key), "AES"));
		return result;
	}
	
}
