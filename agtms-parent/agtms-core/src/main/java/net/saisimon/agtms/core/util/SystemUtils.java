package net.saisimon.agtms.core.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeUtility;

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
	public static <T> List<T> transform(final Object obj, final Class<T> clazz) {
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
	
	public static Object parseFieldValue(Object fieldValue, String fieldClassName) throws ClassNotFoundException {
		if (fieldValue != null && fieldClassName != null) {
			fieldClassName = handleFieldType(fieldClassName);
			Class<?> fieldClass = Class.forName(fieldClassName);
			if (Integer.class == fieldClass || int.class == fieldClass) {
				return Integer.valueOf(fieldValue.toString());
			} else if (Long.class == fieldClass || long.class == fieldClass) {
				return Long.valueOf(fieldValue.toString());
			} else if (Double.class == fieldClass || double.class == fieldClass) {
				return Double.valueOf(fieldValue.toString());
			}
		}
		return fieldValue;
	}
	
	public static String handleFieldType(String type) {
		if ("date".equalsIgnoreCase(type) || "datetime".equalsIgnoreCase(type)) {
			return "java.lang.String";
		}
		return type;
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
