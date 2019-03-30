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
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统相关工具类
 * 
 * @author saisimon
 *
 */
@Slf4j
public final class SystemUtils {
	
	private static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?");
	private static final Pattern URL_PATTERN = Pattern.compile("^((https|http|ftp|rtsp|mms)?://)?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([0-9a-z_!~*'()-]+\\.)*([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\.[a-z]{2,6})(:[0-9]{1,4})?((/?)|(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$");
	private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");
	
	private static final Map<Long, Future<?>> TASK_FUTURE_MAP = new ConcurrentHashMap<>();
	
	private SystemUtils() {
		throw new IllegalAccessError();
	}
	
	/**
	 * 判断输入邮箱地址有效性
	 * 
	 * @param email 邮箱地址
	 * @return 邮箱地址有效性
	 */
	public static boolean isEmail(String email) {
		if (StringUtils.isBlank(email)) {
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
		if (StringUtils.isBlank(url)) {
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
		if (StringUtils.isBlank(json)) {
			return null;
		}
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			JavaType javaType = parse(clazz, genericClasses);
			return objectMapper.readValue(json, javaType);
		} catch (IOException e) {
			log.error("解析 json 失败", e);
			return null;
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
		String contentDisposition;
		try {
			String newFilename = URLEncoder.encode(filename, "UTF8");
			contentDisposition = "attachment; filename=\"" + newFilename + "\"";
			if (userAgent != null) {
				userAgent = userAgent.toLowerCase();
				if (userAgent.indexOf("msie") != -1) {
					contentDisposition = "attachment; filename=\"" + newFilename + "\"";
				} else if (userAgent.indexOf("opera") != -1) {
					contentDisposition = "attachment; filename*=UTF-8''" + newFilename;
				} else if (userAgent.indexOf("safari") != -1) {
					contentDisposition = "attachment; filename=\"" + new String(filename.getBytes("UTF-8"), "ISO8859-1") + "\"";
				} else if (userAgent.indexOf("applewebkit") != -1) {
					newFilename = MimeUtility.encodeText(filename, "UTF8", "B");
					contentDisposition = "attachment; filename=\"" + newFilename + "\"";
				} else if (userAgent.indexOf("mozilla") != -1) {
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
		String ip = null;
		String unknown = "unknown";
		String ipAddresses = request.getHeader("X-Forwarded-For");
		if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			ipAddresses = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			ipAddresses = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			ipAddresses = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			ipAddresses = request.getHeader("X-Real-IP");
		}
		if (ipAddresses != null && ipAddresses.length() != 0) {
			ip = ipAddresses.split(",")[0];
		}
		if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
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
				if (type.getTypeName().startsWith(targetInterfaceClass.getName())) {
					ParameterizedType pt = (ParameterizedType) type;
					return (Class<?>) pt.getActualTypeArguments()[genericIndex];
				}
			}
			Class<?>[] interfaces = clazz.getInterfaces();
			if (interfaces != null) {
				for (Class<?> inter : interfaces) {
					return getInterfaceGenericClass(inter, targetInterfaceClass, genericIndex);
				}
			}
			clazz = clazz.getSuperclass();
		}
		throw new RuntimeException("get interface generic class failed.");
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
		TASK_FUTURE_MAP.put(taskId, future);
	}
	
	public static Future<?> removeTaskFuture(Long taskId) {
		return TASK_FUTURE_MAP.remove(taskId);
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
	
}
