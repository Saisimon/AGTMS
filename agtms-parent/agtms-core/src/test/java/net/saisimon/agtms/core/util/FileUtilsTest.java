package net.saisimon.agtms.core.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Test;

import net.saisimon.agtms.core.enums.ImageFormats;

public class FileUtilsTest {
	
	@Test
	public void testImageFormatByInputStream() throws Exception {
		InputStream input = null;
		Assert.assertEquals(ImageFormats.UNKNOWN, FileUtils.imageFormat(input));
		
		input = new ByteArrayInputStream(Hex.decodeHex("FFD8FF"));
		Assert.assertEquals(ImageFormats.JPG, FileUtils.imageFormat(input));
		
		input = new ByteArrayInputStream(Hex.decodeHex("89504E47"));
		Assert.assertEquals(ImageFormats.PNG, FileUtils.imageFormat(input));
		
		input = new ByteArrayInputStream(Hex.decodeHex("47494638"));
		Assert.assertEquals(ImageFormats.GIF, FileUtils.imageFormat(input));
		
		input = new ByteArrayInputStream(Hex.decodeHex("424D"));
		Assert.assertEquals(ImageFormats.BMP, FileUtils.imageFormat(input));
		
		input = new ByteArrayInputStream(Hex.decodeHex("1234"));
		Assert.assertEquals(ImageFormats.UNKNOWN, FileUtils.imageFormat(input));
	}
	
	@Test
	public void testImageFormatByString() {
		Assert.assertEquals(ImageFormats.UNKNOWN, FileUtils.imageFormat((String) null));
		Assert.assertEquals(ImageFormats.UNKNOWN, FileUtils.imageFormat("test.log"));
		Assert.assertEquals(ImageFormats.JPG, FileUtils.imageFormat("test.jpg"));
		Assert.assertEquals(ImageFormats.PNG, FileUtils.imageFormat("test.png"));
		Assert.assertEquals(ImageFormats.GIF, FileUtils.imageFormat("test.gif"));
		Assert.assertEquals(ImageFormats.BMP, FileUtils.imageFormat("test.bmp"));
	}
	
}
