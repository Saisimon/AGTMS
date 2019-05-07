package net.saisimon.agtms.core.enums;

/**
 * 图片格式枚举
 * 
 * @author saisimon
 *
 */
public enum ImageFormats {
	
	/**
	 * jpg
	 */
	JPG(".jpg"),
	/**
	 * gif
	 */
	GIF(".gif"),
	/**
	 * png
	 */
	PNG(".png"),
	/**
	 * bmp
	 */
	BMP(".bmp"),
	/**
	 * 未知类型
	 */
	UNKNOWN("");
	
	private String suffix;
	
	ImageFormats(String suffix) {
		this.suffix = suffix;
	}
	
	public String getSuffix() {
		return this.suffix;
	}
	
}
