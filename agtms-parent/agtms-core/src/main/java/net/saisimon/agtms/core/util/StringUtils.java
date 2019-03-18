package net.saisimon.agtms.core.util;

/**
 * 字符串相关工具类
 * 
 * @author saisimon
 *
 */
public class StringUtils extends org.springframework.util.StringUtils {

	private StringUtils() {
		throw new IllegalAccessError();
	}

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

}
