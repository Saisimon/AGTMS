package net.saisimon.agtms.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import cn.hutool.core.util.NetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统相关工具类
 * 
 * @author saisimon
 *
 */
@Slf4j
public final class SystemUtils extends StringUtils {
	
	private final static Pattern EMAIL_PATTERN = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", Pattern.CASE_INSENSITIVE);
	private static final Pattern URL_PATTERN = Pattern.compile("^((https|http|ftp)?://)?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([0-9a-z_!~*'()-]+\\.)*([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\.[a-z]{2,6})(:[0-9]{1,4})?((/?)|(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$");
	private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");
	
	private static final Map<Long, Future<?>> TASK_FUTURE_MAP = new ConcurrentHashMap<>();
	
	public static final Executor executor = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() * 2 + 1);
	
	private SystemUtils() {}
	
	public static boolean isNotEmpty(Object str) {
		return !isEmpty(str);
	}

	public static boolean isBlank(String str) {
		return !isNotBlank(str);
	}

	public static boolean isNotBlank(String str) {
		return hasText(str);
	}

	public static String toLowerCase(String str) {
		return toCase(str, true);
	}

	public static String toUpperCase(String str) {
		return toCase(str, false);
	}
	
	/**
	 * 获取本地可用端口（从 0xA000 ~ 0xFFFF）
	 * 
	 * @return 本地可用端口
	 */
	public static int getAvailableLocalPort() {
		for (int i = 0xA000; i <= 0xFFFF; i++) {
			if (NetUtil.isUsableLocalPort(i)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 判断输入邮箱地址有效性
	 * 
	 * @param email 邮箱地址
	 * @return 邮箱地址有效性
	 */
	public static boolean isEmail(String email) {
		if (isBlank(email)) {
			return false;
		}
		return EMAIL_PATTERN.matcher(email).matches();
	}
	
	/**
	 * 判断输入链接地址有效性
	 * 
	 * @param url 链接地址
	 * @return 链接地址有效性
	 */
	public static boolean isURL(String url) {
		if (isBlank(url)) {
			return false;
		}
		return URL_PATTERN.matcher(url).matches();
	}
	
	/**
	 * 将指定输入对象转成 JSON 字符串
	 * 
	 * @param object 输入对象
	 * @return JSON 字符串
	 */
	public static String toJson(Object object) {
		if (object == null) {
			return null;
		}
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(Include.NON_NULL);
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("生成 json 失败", e);
			return null;
		}
	}
	
	/**
	 * 将指定输入 JSON 字符串转成指定类型的对象
	 * 
	 * @param json JSON 字符串
	 * @param clazz 对象类型
	 * @param genericClasses 对象泛型类型
	 * @return 指定类型的对象
	 */
	public static <T> T fromJson(String json, Class<T> clazz, Class<?>... genericClasses) {
		if (isBlank(json)) {
			return null;
		}
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			JavaType javaType = parse(clazz, genericClasses);
			return objectMapper.readValue(json, javaType);
		} catch (IOException e) {
			throw new IllegalArgumentException("解析 Json 失败", e);
		}
	}
	
	/**
	 * 将指定输入的驼峰字符串转成按分隔符分隔的字符串
	 * 
	 * @param str 输入的驼峰字符串
	 * @param separator 分隔符
	 * @return 转换后的字符串
	 */
	public static String humpToCode(String str, String separator) {
		if (str == null) {
			return null;
		}
		String sep = "";
		if (separator != null) {
			sep = separator;
		}
		Matcher matcher = HUMP_PATTERN.matcher(str);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(buffer, sep + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	/**
	 * 将指定输入对象转化为列表对象，输入对象不是列表或数组时返回空
	 * 
	 * @param obj 输入对象
	 * @return 列表对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> transformList(final Object obj) {
		List<T> fields = null;
		if (obj != null) {
			if (obj.getClass().isArray()) {
				fields = Arrays.asList((T[])obj);
			} else if (obj instanceof List) {
				fields = (List<T>) obj;
			}
		}
		return fields;
	}
	
	/**
	 * 根据不同的 userAgent 返回下载时的Content-Disposition响应头信息，避免中文文件名乱码
	 * 
	 * @param userAgent 用户代理
	 * @param filename 文件名
	 * @return Content-Disposition 响应头信息
	 */
	public static String encodeDownloadContentDisposition(String userAgent, String filename) {
		if (filename == null) {
			return null;
		}
		String ua = userAgent;
		String contentDisposition;
		try {
			String newFilename = URLEncoder.encode(filename, "UTF8");
			contentDisposition = "attachment; filename=\"" + newFilename + "\"";
			if (ua != null) {
				ua = ua.toLowerCase();
				if (ua.indexOf("msie") != -1) {
					contentDisposition = "attachment; filename=\"" + newFilename + "\"";
				} else if (ua.indexOf("opera") != -1) {
					contentDisposition = "attachment; filename*=UTF-8''" + newFilename;
				} else if (ua.indexOf("safari") != -1 || ua.indexOf("applewebkit") != -1) {
					contentDisposition = "attachment; filename=\"" + new String(filename.getBytes("UTF-8"), "ISO8859-1") + "\"";
				} else if (ua.indexOf("mozilla") != -1) {
					contentDisposition = "attachment; filename*=UTF-8''" + newFilename;
				}
			}
		} catch (UnsupportedEncodingException e) {
			contentDisposition = "attachment; filename=\"" + filename + "\"";
		}
		return contentDisposition;
	}
	
	/**
	 * 获取 HTTP 请求中的 IP 地址
	 *
	 * @param request HTTP请求
	 * @return IP 地址
	 */
	public static String getIPAddress(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		String ipAddresses = request.getHeader("X-Forwarded-For");
		if (!checkIPAddress(ipAddresses)) {
			ipAddresses = request.getHeader("Proxy-Client-IP");
		}
		if (!checkIPAddress(ipAddresses)) {
			ipAddresses = request.getHeader("WL-Proxy-Client-IP");
		}
		if (!checkIPAddress(ipAddresses)) {
			ipAddresses = request.getHeader("HTTP_CLIENT_IP");
		}
		if (!checkIPAddress(ipAddresses)) {
			ipAddresses = request.getHeader("X-Real-IP");
		}
		String ip = null;
		if (checkIPAddress(ipAddresses)) {
			ip = ipAddresses.split(",")[0];
		}
		if (!checkIPAddress(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 根据子类获取父接口指定的泛型类型，未找到指定泛型时抛出运行时异常
	 * 
	 * @param subClass 子类类型
	 * @param targetInterfaceClass 父接口类型
	 * @param genericIndex 泛型下标
	 * @return 泛型类型
	 */
	public static Class<?> getInterfaceGenericClass(Class<?> subClass, Class<?> targetInterfaceClass, int genericIndex) {
		if (subClass == null || targetInterfaceClass == null || genericIndex < 0) {
			return null;
		}
		Class<?> clazz = subClass;
		while (clazz != null && clazz != Object.class) {
			Type[] types = clazz.getGenericInterfaces();
			for (Type type : types) {
				if (type.getTypeName().contains(targetInterfaceClass.getName())) {
					Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
					if (genericIndex >= typeArguments.length) {
						return null;
					}
					Type typeArgument = typeArguments[genericIndex];
					if (typeArgument instanceof Class) {
						return (Class<?>) typeArgument;
					}
					return null;
				}
			}
			Class<?>[] interfaces = clazz.getInterfaces();
			if (interfaces != null) {
				for (Class<?> inter : interfaces) {
					Class<?> result = getInterfaceGenericClass(inter, targetInterfaceClass, genericIndex);
					if (result != null) {
						return result;
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}
	
	/**
	 * 向指定响应中发送对象
	 * 
	 * @param response 响应
	 * @param obj 待发送对象
	 * @throws IOException 发送异常
	 */
	public static void sendObject(HttpServletResponse response, Object obj) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		try (PrintWriter out = response.getWriter()) {
			out.append(toJson(obj));
		}
	}
	
	public static void putTaskFuture(Long taskId, Future<?> future) {
		if (taskId != null && future != null) {
			TASK_FUTURE_MAP.put(taskId, future);
		}
	}
	
	public static Future<?> removeTaskFuture(Long taskId) {
		if (taskId != null) {
			return TASK_FUTURE_MAP.remove(taskId);
		}
		return null;
	}
	
	private static JavaType parse(Class<?> targetClass, Class<?>... genericClasses) {
		if (genericClasses == null) {
			return TypeFactory.defaultInstance().constructType(targetClass);
		} else {
			List<Class<?>> genericClassList = new ArrayList<>(Arrays.asList(genericClasses));
			return construct(targetClass, parse(genericClassList, genericClassList.iterator()));
		}
	}

	private static List<JavaType> parse(List<Class<?>> genericClassList, Iterator<Class<?>> it) {
		List<JavaType> javaTypes = new ArrayList<>();
		while (it.hasNext()) {
			Class<?> cls = it.next();
			int genericLenght = cls.getTypeParameters().length;
			if (genericLenght > 0) {
				List<JavaType> subJavaTypes = parse(genericClassList.subList(1, genericClassList.size()), it);
				JavaType javaType = construct(cls, subJavaTypes.subList(0, genericLenght));
				javaTypes.add(javaType);
				javaTypes.addAll(subJavaTypes.subList(genericLenght, subJavaTypes.size()));
			} else {
				JavaType javaType = TypeFactory.defaultInstance().constructType(cls);
				javaTypes.add(javaType);
			}
		}
		return javaTypes;
	}

	private static JavaType construct(Class<?> cls, List<JavaType> javaTypes) {
		return TypeFactory.defaultInstance().constructParametricType(cls, javaTypes.toArray(new JavaType[javaTypes.size()]));
	}
	
	private static String toCase(String str, boolean lower) {
		if (isBlank(str)) {
			return str;
		}
		char[] cs = new char[str.length()];
		if (lower) {
			for (int i = 0; i < cs.length; i++) {
				cs[i] = Character.toLowerCase(str.charAt(i));
			}
		} else {
			for (int i = 0; i < cs.length; i++) {
				cs[i] = Character.toUpperCase(str.charAt(i));
			}
		}
		return new String(cs);
	}
	
	private static boolean checkIPAddress(String ipAddresses) {
		return ipAddresses != null && ipAddresses.length() > 0 && !"unknown".equalsIgnoreCase(ipAddresses);
	}
	
}
