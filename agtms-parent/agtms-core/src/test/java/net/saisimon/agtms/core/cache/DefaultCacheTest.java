package net.saisimon.agtms.core.cache;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultCacheTest {
	
	private static final String TEST = "test";
	private static final String KEY = "key";
	private static Cache cache;
	
	@BeforeClass
	public static void before() {
		cache = new DefaultCache();
	}
	
	@Test
	public void test() throws InterruptedException {
		cache.set(KEY, TEST, 0);
		String test = cache.get(KEY, String.class);
		Assert.assertEquals(TEST, test);
		
		cache.delete(KEY);
		test = cache.get(KEY, String.class);
		Assert.assertNull(test);
		
		cache.set(KEY, null, 0);
		test = cache.get(KEY, String.class);
		Assert.assertNull(test);
		
		cache.set(KEY, TEST, 1000);
		test = cache.get(KEY, String.class);
		Assert.assertEquals(TEST, test);
		
		Thread.sleep(1001);
		test = cache.get(KEY, String.class);
		Assert.assertNull(test);
		
		cache.set(KEY, TEST, 1000);
		Thread.sleep(500);
		test = cache.get(KEY, String.class);
		Thread.sleep(501);
		test = cache.get(KEY, String.class);
		Assert.assertEquals(TEST, test);
		cache.delete(KEY);
		
		cache.set(KEY, TEST, 1000);
		Thread.sleep(500);
		test = cache.get(KEY, false, String.class);
		Thread.sleep(501);
		test = cache.get(KEY, false, String.class);
		Assert.assertNull(test);
		cache.delete(KEY);
		
		cache.set(null, null, 0);
		Object val = cache.get(null, Object.class);
		Assert.assertNull(val);
	}
	
}
