package net.saisimon.agtms.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
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

import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SystemUtils {
	
	private static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?");
	private static final Pattern URL_PATTERN = Pattern.compile("[a-zA-z]+://[^\\s]*");
	private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");
	private static final Map<Long, Future<?>> taskFutureMap = new ConcurrentHashMap<>();
	
	private SystemUtils() {
		throw new IllegalAccessError();
	}
	
	public static boolean isEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return false;
		}
		return EMAIL_PATTERN.matcher(email).matches();
	}
	
	public static boolean isURL(String url) {
		if (StringUtils.isBlank(url)) {
			return false;
		}
		return URL_PATTERN.matcher(url).matches();
	}
	
	public static String toJson(Object object) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(Include.NON_NULL);
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("生成 json 失败", e);
			return null;
		}
	}
	
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
	
	public static String getClasspath() throws URISyntaxException {
		URL url = ClassUtils.getDefaultClassLoader().getResource("");
		return url.toURI().getPath();
	}
	
	public static RuntimeException convertReflectionExceptionToUnchecked(Exception exception) {
		if (exception instanceof IllegalAccessException
				|| exception instanceof IllegalArgumentException
				|| exception instanceof NoSuchMethodException) {
			return new IllegalArgumentException(exception);
		}
		if (exception instanceof InvocationTargetException) {
			return new RuntimeException(((InvocationTargetException) exception).getTargetException());
		}
		if (exception instanceof RuntimeException) {
			return (RuntimeException) exception;
		}
		return new RuntimeException("Unexpected Checked Exception.", exception);
	}
	
	public static String humpToCode(String str, String separator) {
		Matcher matcher = HUMP_PATTERN.matcher(str);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(buffer, separator + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> transformList(final Object obj, final Class<T> clazz) {
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
	
	public static String encodeDownloadContentDisposition(String userAgent, String filename) {
		String contentDisposition;
		try {
			String newFilename = URLEncoder.encode(filename, "UTF8");
			contentDisposition = "attachment; filename=\"" + newFilename + "\"";
			if (userAgent != null) {
				userAgent = userAgent.toLowerCase();
				if (userAgent.indexOf("msie") != -1) { // IE浏览器，只能采用URLEncoder编码
					contentDisposition = "attachment; filename=\"" + newFilename + "\"";
				} else if (userAgent.indexOf("opera") != -1) { // Opera浏览器只能采用filename*
					contentDisposition = "attachment; filename*=UTF-8''" + newFilename;
				} else if (userAgent.indexOf("safari") != -1) { // Safari浏览器，只能采用ISO编码的中文输出
					contentDisposition = "attachment; filename=\"" + new String(filename.getBytes("UTF-8"), "ISO8859-1") + "\"";
				} else if (userAgent.indexOf("applewebkit") != -1) { // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
					newFilename = MimeUtility.encodeText(filename, "UTF8", "B");
					contentDisposition = "attachment; filename=\"" + newFilename + "\"";
				} else if (userAgent.indexOf("mozilla") != -1) { // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
					contentDisposition = "attachment; filename*=UTF-8''" + newFilename;
				}
			}
		} catch (UnsupportedEncodingException e) {
			contentDisposition = "attachment; filename=\"" + filename + "\"";
		}
		return contentDisposition;
	}
	
	public static String getIPAddress(HttpServletRequest request) {
		String ip = null;
		String unknown = "unknown";
		String ipAddresses = request.getHeader("X-Forwarded-For"); // X-Forwarded-For：Squid 服务代理
		if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			ipAddresses = request.getHeader("Proxy-Client-IP"); // Proxy-Client-IP：apache 服务代理
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			ipAddresses = request.getHeader("WL-Proxy-Client-IP"); // WL-Proxy-Client-IP：weblogic 服务代理
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			ipAddresses = request.getHeader("HTTP_CLIENT_IP"); // HTTP_CLIENT_IP：有些代理服务器
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			ipAddresses = request.getHeader("X-Real-IP"); // X-Real-IP：nginx服务代理
		}
		if (ipAddresses != null && ipAddresses.length() != 0) {
			ip = ipAddresses.split(",")[0]; // 有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
		}
		if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			ip = request.getRemoteAddr(); // 还是不能获取到，最后再通过request.getRemoteAddr();获取
		}
		return ip;
	}
	
	public static Class<?> getInterfaceGenericClass(Class<?> subClass, Class<?> targetInterfaceClass, int genericIndex) {
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
	
	public static void sendObject(HttpServletResponse response, Object obj) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		try (PrintWriter out = response.getWriter()) {
			out.append(toJson(obj));
		}
	}
	
	public static void putTaskFuture(Long taskId, Future<?> future) {
		taskFutureMap.put(taskId, future);
	}
	
	public static Future<?> removeTaskFuture(Long taskId) {
		return taskFutureMap.remove(taskId);
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
