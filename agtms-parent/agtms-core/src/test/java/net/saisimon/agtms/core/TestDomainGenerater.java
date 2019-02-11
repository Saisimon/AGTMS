package net.saisimon.agtms.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StopWatch;

import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.generate.DomainGenerater;

public class TestDomainGenerater {
	
	private Map<String, String> fieldMap;
	private String domainName = "Test";
	private String namespace = "test";
	
	@Before
	public void init() {
		fieldMap = new HashMap<>();
		fieldMap.put("id",  Long.class.getName());
		fieldMap.put("name",  String.class.getName());
		fieldMap.put("age", Integer.class.getName());
		fieldMap.put("price", Double.class.getName());
		fieldMap.put("character", Character.class.getName());
		fieldMap.put("aaa", List.class.getName());
		fieldMap.put("bbb", Map.class.getName());
		fieldMap.put("ccc", int[].class.getName());
	}

	@Test
	public void testGenerateMapOfStringStringStringBoolean() throws Exception {
		StopWatch sw = new StopWatch();
		sw.start("1");
		Class<Domain> domainClass = DomainGenerater.generate(namespace, fieldMap, domainName, false);
		sw.stop();
		System.out.println(domainClass.getClassLoader());
		sw.start("2");
		domainClass = DomainGenerater.generate(namespace, fieldMap, domainName, false);
		sw.stop();
		System.out.println(domainClass.getClassLoader());
		sw.start("3");
		domainClass = DomainGenerater.generate(namespace, fieldMap, domainName, true);
		sw.stop();
		System.out.println(domainClass.getClassLoader());
		fieldMap.put("other", String.class.getName());
		sw.start("4");
		domainClass = DomainGenerater.generate(namespace, fieldMap, domainName, false);
		sw.stop();
		System.out.println(domainClass.getClassLoader());
		sw.start("5");
		domainClass = DomainGenerater.generate(namespace, fieldMap, domainName, true);
		sw.stop();
		System.out.println(domainClass.getClassLoader());
		System.out.println(sw.prettyPrint());
		/*Domain domain = domainClass.newInstance();
		domain.setField("id", 1L, Long.class);
		Assert.assertEquals((long)1, domain.getField("id"));
		domain.setField("name", "Test", String.class);
		Assert.assertEquals("Test", domain.getField("name"));
		domain.setField("age", 18, Integer.class);
		Assert.assertEquals(18, domain.getField("age"));
		domain.setField("character", 'a', Character.class);
		Assert.assertEquals('a', domain.getField("character"));
		List<String> list = new ArrayList<>();
		list.add("Test");
		domain.setField("aaa", list, List.class);
		Assert.assertEquals(list, domain.getField("aaa"));
		Map<String, String> map = new HashMap<>();
		map.put("test", "Test");
		domain.setField("bbb", map, Map.class);
		Assert.assertEquals(map, domain.getField("bbb"));
		int[] ints = new int[] {1,2};
		domain.setField("ccc", ints, int[].class);
		Assert.assertArrayEquals(ints, (int[])domain.getField("ccc"));*/
	}
	
	@After
	public void destroy() {
		DomainGenerater.removeDomainClass(namespace, domainName);
	}
	
}
