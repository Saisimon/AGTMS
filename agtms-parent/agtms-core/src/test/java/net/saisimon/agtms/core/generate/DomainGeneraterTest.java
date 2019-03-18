package net.saisimon.agtms.core.generate;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.generate.DomainGenerater;

public class DomainGeneraterTest {
	
	private static final String NAMESPACE = "test";
	private static final String NAME = "Test";
	
	@Test
	public void test() throws GenerateException {
		Class<Domain> domainClass = DomainGenerater.generate(NAMESPACE, null, NAME);
		Assert.assertNull(domainClass);
		
		Map<String, String> fieldMap = new HashMap<>();
		domainClass = DomainGenerater.generate(NAMESPACE, fieldMap, null);
		Assert.assertNull(domainClass);
		
		domainClass = DomainGenerater.generate(null, fieldMap, NAME);
		Assert.assertNull(domainClass);
		
		domainClass = DomainGenerater.generate(NAMESPACE, fieldMap, NAME);
		Assert.assertNotNull(domainClass);
		
		fieldMap.put("name", String.class.getName());
		fieldMap.put("age", Integer.class.getName());
		domainClass = DomainGenerater.generate(NAMESPACE, fieldMap, NAME);
		Assert.assertNotNull(domainClass);
	}
	
	@Test(expected = GenerateException.class)
	public void testGenerateException() throws GenerateException {
		Map<String, String> fieldMap = new HashMap<>();
		fieldMap.put("name", "xxx");
		DomainGenerater.generate(NAMESPACE, fieldMap, NAME);
	}
	
	@Test
	public void testRemoveDomainClass() throws GenerateException {
		DomainGenerater.generate(NAMESPACE, new HashMap<>(), NAME);
		boolean result = DomainGenerater.removeDomainClass(null, NAME);
		Assert.assertFalse(result);
		
		result = DomainGenerater.removeDomainClass(NAMESPACE, null);
		Assert.assertFalse(result);
		
		result = DomainGenerater.removeDomainClass(NAMESPACE, NAME);
		Assert.assertTrue(result);
	}
	
	@After
	public void after() {
		DomainGenerater.removeDomainClass(NAMESPACE, NAME);
	}
	
}
