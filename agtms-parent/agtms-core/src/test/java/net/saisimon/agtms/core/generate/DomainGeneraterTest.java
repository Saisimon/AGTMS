package net.saisimon.agtms.core.generate;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.property.BasicProperties;

@RunWith(SpringRunner.class)
public class DomainGeneraterTest {
	
	private static final String NAMESPACE = "test";
	private static final String NAME = "Test";
	
	@Autowired
	private DomainGenerater domainGenerater;
	
	@Test
	public void test() throws GenerateException {
		Class<Domain> domainClass = domainGenerater.generate(NAMESPACE, null, NAME);
		Assert.assertNull(domainClass);
		
		Map<String, String> fieldMap = new HashMap<>();
		domainClass = domainGenerater.generate(NAMESPACE, fieldMap, null);
		Assert.assertNull(domainClass);
		
		domainClass = domainGenerater.generate(null, fieldMap, NAME);
		Assert.assertNull(domainClass);
		
		domainClass = domainGenerater.generate(NAMESPACE, fieldMap, NAME);
		Assert.assertNotNull(domainClass);
		
		fieldMap.put("name", String.class.getName());
		fieldMap.put("age", Integer.class.getName());
		domainClass = domainGenerater.generate(NAMESPACE, fieldMap, NAME);
		Assert.assertNotNull(domainClass);
	}
	
	@Test(expected = GenerateException.class)
	public void testGenerateException() throws GenerateException {
		Map<String, String> fieldMap = new HashMap<>();
		fieldMap.put("name", "xxx");
		domainGenerater.generate(NAMESPACE, fieldMap, NAME);
	}
	
	@Test
	public void testRemoveDomainClass() throws GenerateException {
		domainGenerater.generate(NAMESPACE, new HashMap<>(), NAME);
		boolean result = domainGenerater.removeDomainClass(null, NAME);
		Assert.assertFalse(result);
		
		result = domainGenerater.removeDomainClass(NAMESPACE, null);
		Assert.assertFalse(result);
		
		result = domainGenerater.removeDomainClass(NAMESPACE, NAME);
		Assert.assertTrue(result);
	}
	
	@After
	public void after() {
		domainGenerater.removeDomainClass(NAMESPACE, NAME);
	}
	
	@Configuration
	public static class Config {
		
		@Bean
		public DomainGenerater domainGenerater() {
			return new DomainGenerater(new BasicProperties());
		}
		
	}
	
}
