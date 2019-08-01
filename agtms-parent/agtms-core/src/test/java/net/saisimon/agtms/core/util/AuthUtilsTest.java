package net.saisimon.agtms.core.util;

import org.junit.Assert;
import org.junit.Test;

public class AuthUtilsTest {
	
	@Test
	public void testAes() throws Exception {
		Assert.assertNull(AuthUtils.aesEncrypt(null, null));
		Assert.assertEquals("agtms", AuthUtils.aesEncrypt("agtms", null));
		Assert.assertNull(AuthUtils.aesDecrypt(null, null));
		Assert.assertEquals("agtms", AuthUtils.aesDecrypt("agtms", null));
		String text = "测试-test";
		String key = "agtms";
		String ciphertext = AuthUtils.aesEncrypt(text, key);
		Assert.assertEquals(text, AuthUtils.aesDecrypt(ciphertext, key));
	}
	
}
