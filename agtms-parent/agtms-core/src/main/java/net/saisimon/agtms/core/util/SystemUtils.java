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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.domain.Task;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.factory.ActuatorFactory;
import net.saisimon.agtms.core.factory.TaskServiceFactory;
import net.saisimon.agtms.core.service.TaskService;
import net.saisimon.agtms.core.task.Actuator;

@Slf4j
public final class SystemUtils {
	
	private static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?");
	private static final Pattern URL_PATTERN = Pattern.compile("[a-zA-z]+://[^\\s]*");
	private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");
	
	private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
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
	
	public static <P> void submitTask(final Task task) {
		if (task == null) {
			return;
		}
		Future<?> future = executor.submit(() -> {
			TaskService taskService = TaskServiceFactory.get();
			task.setHandleStatus(HandleStatuses.PROCESSING.getStatus());
			taskService.saveOrUpdate(task);
			try {
				@SuppressWarnings("unchecked")
				Actuator<P> actuator = (Actuator<P>) ActuatorFactory.get(task.getTaskType());
				if (actuator == null) {
					task.setHandleStatus(HandleStatuses.FAILURE.getStatus());
					task.setHandleTime(new Date());
					taskService.saveOrUpdate(task);
					return;
				}
				Class<P> paramClass = actuator.getParamClass();
				P param = fromJson(task.getTaskParam(), paramClass);
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				Result result = actuator.execute(param);
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				task.setHandleTime(new Date());
				if (ResultUtils.isSuccess(result)) {
					task.setHandleStatus(HandleStatuses.SUCCESS.getStatus());
					task.setHandleResult(result.getMessage());
					task.setTaskParam(SystemUtils.toJson(param));
				} else {
					task.setHandleStatus(HandleStatuses.FAILURE.getStatus());
					if (result != null) {
						task.setHandleResult(result.getMessage());
					}
				}
				taskService.saveOrUpdate(task);
			} catch (InterruptedException e) {
				log.warn("任务被中断");
			} catch (Throwable e) {
				task.setHandleStatus(HandleStatuses.FAILURE.getStatus());
				task.setHandleTime(new Date());
				taskService.saveOrUpdate(task);
				log.error("任务执行异常", e);
			} finally {
				taskFutureMap.remove(task.getId());
			}
		});
		taskFutureMap.put(task.getId(), future);
	}
	
	public static <P> void downloadTask(final Task task, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (task == null || task.getHandleStatus() != HandleStatuses.SUCCESS.getStatus()) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		@SuppressWarnings("unchecked")
		Actuator<P> actuator = (Actuator<P>) ActuatorFactory.get(task.getTaskType());
		if (actuator == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		actuator.download(task, request, response);
	}
	
	public static void cancelTask(final Task task) {
		if (task.getHandleStatus() != HandleStatuses.PROCESSING.getStatus()) {
			return;
		}
		Future<?> future = taskFutureMap.remove(task.getId());
		if (future != null && !future.isDone() && !future.isCancelled()) {
			future.cancel(true);
			TaskService taskService = TaskServiceFactory.get();
			try {
				future.get();
			} catch (CancellationException e) {
				task.setHandleStatus(HandleStatuses.CANCEL.getStatus());
				task.setHandleTime(new Date());
				taskService.saveOrUpdate(task);
				log.warn("手动取消任务");
			} catch (Throwable e) {
				task.setHandleStatus(HandleStatuses.FAILURE.getStatus());
				task.setHandleTime(new Date());
				taskService.saveOrUpdate(task);
				log.error("任务执行异常", e);
			}
		}
	}
	
	public static void sendObject(HttpServletResponse response, Object obj) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		try (PrintWriter out = response.getWriter()) {
			out.append(toJson(obj));
		}
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
