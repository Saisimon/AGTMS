package net.saisimon.agtms.core.enums;

/**
 * 图片格式枚举
 * 
 * @author saisimon
 *
 */
public enum ImageFormats {
	
	JPG(".jpg"),
	GIF(".gif"),
	PNG(".png"),
	BMP(".bmp"),
	UNKNOWN("");
	
	private String suffix;
	
	ImageFormats(String suffix) {
		this.suffix = suffix;
	}
	
	public String getSuffix() {
		return this.suffix;
	}
	
}
