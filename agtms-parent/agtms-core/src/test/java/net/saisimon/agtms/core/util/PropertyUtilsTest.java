package net.saisimon.agtms.core.util;

import org.junit.Assert;
import org.junit.Test;

public class PropertyUtilsTest {

	@Test
	public void testFetchYaml() {
		Assert.assertNull(PropertyUtils.fetchYaml(null, null));
		Assert.assertEquals("", PropertyUtils.fetchYaml("extra.class", ""));
		Assert.assertEquals("/tmp/classes", PropertyUtils.fetchYaml("extra.class.path", ""));
	}
	
	@Test
	public void testFetchProperties() {
		Assert.assertNull(PropertyUtils.fetchProperties(null, null));
		Assert.assertEquals("", PropertyUtils.fetchProperties("extra.class", ""));
		Assert.assertEquals("/tmp/classes", PropertyUtils.fetchProperties("extra.class.path", ""));
	}
	
}
