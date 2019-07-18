package net.saisimon.agtms.core.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import lombok.Getter;
import lombok.Setter;

public class SystemUtilsTest {
	
	@Test
	public void testIsEmail() {
		String email = null;
		boolean result = SystemUtils.isEmail(email);
		Assert.assertFalse(result);
		
		email = "";
		result = SystemUtils.isEmail(email);
		Assert.assertFalse(result);
		
		email = "aaa";
		result = SystemUtils.isEmail(email);
		Assert.assertFalse(result);
		
		email = "aaa@a.cn.dd";
		result = SystemUtils.isEmail(email);
		Assert.assertTrue(result);
		
		email = "aaa@a.cn@dd";
		result = SystemUtils.isEmail(email);
		Assert.assertFalse(result);
		
		email = "!@a.cn.dd";
		result = SystemUtils.isEmail(email);
		Assert.assertTrue(result);
		
		email = "a@!.cn";
		result = SystemUtils.isEmail(email);
		Assert.assertFalse(result);
		
		email = "a@a!.cn";
		result = SystemUtils.isEmail(email);
		Assert.assertFalse(result);
		
		email = "a@a!cn";
		result = SystemUtils.isEmail(email);
		Assert.assertFalse(result);
		
		email = "a@acn.!";
		result = SystemUtils.isEmail(email);
		Assert.assertFalse(result);
		
		email = "a@acn.";
		result = SystemUtils.isEmail(email);
		Assert.assertFalse(result);
	}

	@Test
	public void testIsURL() {
		String url = null;
		boolean result = SystemUtils.isEmail(url);
		Assert.assertFalse(result);
		
		url = "";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "aa";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "/aa";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "/aa/aaa";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "aa.com";
		result = SystemUtils.isURL(url);
		Assert.assertTrue(result);
		
		url = "www.aa.com";
		result = SystemUtils.isURL(url);
		Assert.assertTrue(result);
		
		url = "http://www.aa.com";
		result = SystemUtils.isURL(url);
		Assert.assertTrue(result);
		
		url = "https://www.aa.com";
		result = SystemUtils.isURL(url);
		Assert.assertTrue(result);
		
		url = "https://www.aa.com/";
		result = SystemUtils.isURL(url);
		Assert.assertTrue(result);
		
		url = "https://www.aa.com//";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "https://www.aa.com./";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "https://www.a!a.com/";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "///www.aa.com";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "aaa://www.aa.com";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "http://www..com";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "http://www";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "file:///www.aa.com";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "ftp://www.aa.com/file/aa.xml";
		result = SystemUtils.isURL(url);
		Assert.assertTrue(result);
		
		url = "https://www.aa.com/?id=2";
		result = SystemUtils.isURL(url);
		Assert.assertTrue(result);
		
		url = "www.google.com.hk";
		result = SystemUtils.isURL(url);
		Assert.assertTrue(result);
		
		url = "?.google.com";
		result = SystemUtils.isURL(url);
		Assert.assertFalse(result);
		
		url = "https://www.aa.com/?id=2&name=";
		result = SystemUtils.isURL(url);
		Assert.assertTrue(result);
		
		url = "https://www.aa.com/aaa?id=2&name=%20";
		result = SystemUtils.isURL(url);
		Assert.assertTrue(result);
		
		url = "http://127.0.0.1:8080/?id=2&name=%20";
		result = SystemUtils.isURL(url);
		Assert.assertTrue(result);
		
		url = "http://192.168.8.1/html/home.html";
		result = SystemUtils.isURL(url);
		Assert.assertTrue(result);
	}

	@Test
	public void testToJson() {
		Object obj = null;
		String json = SystemUtils.toJson(obj);
		Assert.assertNull(json);
		
		json = SystemUtils.toJson("json");
		Assert.assertEquals("\"json\"", json);
		
		json = SystemUtils.toJson(new String[]{});
		Assert.assertEquals("[]", json);
		
		json = SystemUtils.toJson(new String[]{"aaa", "bbb"});
		Assert.assertEquals("[\"aaa\",\"bbb\"]", json);
		
		json = SystemUtils.toJson(Arrays.asList("aaa", "bbb"));
		Assert.assertEquals("[\"aaa\",\"bbb\"]", json);
		
		json = SystemUtils.toJson(new HashMap<>());
		Assert.assertEquals("{}", json);
		
		Map<Integer, String> map = new HashMap<>();
		map.put(1, "aaa");
		map.put(2, "bbb");
		json = SystemUtils.toJson(map);
		Assert.assertEquals("{\"1\":\"aaa\",\"2\":\"bbb\"}", json);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFromJson() {
		String json = null;
		Object obj = SystemUtils.fromJson(json, null);
		Assert.assertNull(obj);
		
		json = "";
		obj = SystemUtils.fromJson(json, null);
		Assert.assertNull(obj);
		
		json = "[]";
		ArrayList<String> list = SystemUtils.fromJson(json, ArrayList.class, String.class);
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
		
		json = "[\"aaa\", \"bbb\"]";
		list = SystemUtils.fromJson(json, ArrayList.class, String.class);
		Assert.assertNotNull(list);
		Assert.assertEquals(2, list.size());
		Assert.assertEquals("bbb", list.get(1));
		
		json = "{}";
		HashMap<String, String> map = SystemUtils.fromJson(json, HashMap.class, String.class, String.class);
		Assert.assertNotNull(map);
		Assert.assertEquals(0, map.size());
		
		json = "{\"1\": \"aaa\",\"2\": \"bbb\"}";
		HashMap<Integer, String> map2 = SystemUtils.fromJson(json, HashMap.class, Integer.class, String.class);
		Assert.assertNotNull(map2);
		Assert.assertEquals(2, map2.size());
		Assert.assertEquals("bbb", map2.get(2));
		
		json = "{\"name\": \"test\"}";
		Entity entity = SystemUtils.fromJson(json, Entity.class);
		Assert.assertNotNull(entity);
		Assert.assertEquals("test", entity.getName());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFromJsonWithException() {
		SystemUtils.fromJson("{", HashMap.class, String.class, String.class);
	}

	@Test
	public void testHumpToCode() {
		String str = null;
		str = SystemUtils.humpToCode(str, null);
		Assert.assertNull(str);
		
		str = SystemUtils.humpToCode(str, ".");
		Assert.assertNull(str);
		
		str = "";
		str = SystemUtils.humpToCode(str, null);
		Assert.assertEquals("", str);
		
		str = "";
		str = SystemUtils.humpToCode(str, ".");
		Assert.assertEquals("", str);
		
		str = "test";
		str = SystemUtils.humpToCode(str, null);
		Assert.assertEquals("test", str);
		
		str = "test";
		str = SystemUtils.humpToCode(str, ".");
		Assert.assertEquals("test", str);
		
		str = "Test";
		str = SystemUtils.humpToCode(str, ".");
		Assert.assertEquals(".test", str);
		
		str = "TEST";
		str = SystemUtils.humpToCode(str, ".");
		Assert.assertEquals(".t.e.s.t", str);
		
		str = "testHumpToCode";
		str = SystemUtils.humpToCode(str, ".");
		Assert.assertEquals("test.hump.to.code", str);
		
		str = "testHumpToCode";
		str = SystemUtils.humpToCode(str, "_");
		Assert.assertEquals("test_hump_to_code", str);
	}

	@Test
	public void testTransformList() {
		List<Integer> ids = Arrays.asList(1, 2, 3);
		String[] names = new String[]{"aaa", "bbb"};
		Map<String, Object> map = new HashMap<>();
		map.put("ids", ids);
		map.put("names", names);
		map.put("test", "test");
		
		Object obj = map.get("obj");
		List<Object> objList = SystemUtils.transformList(obj);
		Assert.assertNull(objList);
		
		Object idsObj = map.get("ids");
		List<Integer> idsObjList = SystemUtils.transformList(idsObj);
		Assert.assertNotNull(idsObjList);
		Assert.assertEquals(3, idsObjList.size());
		Assert.assertEquals(2, idsObjList.get(1).intValue());
		
		Object namesObj = map.get("names");
		List<String> namesObjList = SystemUtils.transformList(namesObj);
		Assert.assertNotNull(namesObjList);
		Assert.assertEquals(2, namesObjList.size());
		Assert.assertEquals("aaa", namesObjList.get(0));
		
		Object testObj = map.get("test");
		List<String> testObjList = SystemUtils.transformList(testObj);
		Assert.assertNull(testObjList);
	}
	
	@Test
	public void testToUpperCase() {
		Assert.assertNull(SystemUtils.toUpperCase(null));
		Assert.assertEquals("", SystemUtils.toUpperCase(""));
		Assert.assertEquals("  ", SystemUtils.toUpperCase("  "));
		Assert.assertEquals("A", SystemUtils.toUpperCase("A"));
		Assert.assertEquals("A", SystemUtils.toUpperCase("a"));
		Assert.assertEquals("1", SystemUtils.toUpperCase("1"));
	}
	
	@Test
	public void testToLowerCase() {
		Assert.assertNull(SystemUtils.toLowerCase(null));
		Assert.assertEquals("", SystemUtils.toLowerCase(""));
		Assert.assertEquals("  ", SystemUtils.toLowerCase("  "));
		Assert.assertEquals("a", SystemUtils.toLowerCase("A"));
		Assert.assertEquals("a", SystemUtils.toLowerCase("a"));
		Assert.assertEquals("1", SystemUtils.toLowerCase("1"));
	}
	
	@Test
	public void testIsNotEmpty() {
		Assert.assertFalse(SystemUtils.isNotEmpty(null));
		Assert.assertFalse(SystemUtils.isNotEmpty(""));
		Assert.assertTrue(SystemUtils.isNotEmpty(" "));
		Assert.assertTrue(SystemUtils.isNotEmpty("A"));
	}
	
	@Test
	public void testGetInterfaceGenericClass() {
		Assert.assertNull(SystemUtils.getInterfaceGenericClass(null, Object.class, 0));
		Assert.assertNull(SystemUtils.getInterfaceGenericClass(Entity.class, null, 0));
		Assert.assertNull(SystemUtils.getInterfaceGenericClass(Entity.class, Object.class, -1));
		Assert.assertNull(SystemUtils.getInterfaceGenericClass(Entity.class, Object.class, 0));
		Assert.assertEquals(Entity.class, SystemUtils.getInterfaceGenericClass(EntityService.class, BaseService.class, 0));
		Assert.assertEquals(Long.class, SystemUtils.getInterfaceGenericClass(EntityService.class, BaseService.class, 1));
		Assert.assertNull(SystemUtils.getInterfaceGenericClass(EntityService.class, BaseService.class, 2));
		Assert.assertEquals(String.class, SystemUtils.getInterfaceGenericClass(EntityService.class, SimpleService.class, 0));
	}
	
	@Test
	public void testTestFuture() {
		Assert.assertNull(SystemUtils.removeTaskFuture(null));
		Assert.assertNull(SystemUtils.removeTaskFuture(1L));
		SystemUtils.putTaskFuture(1L, null);
	}
	
	@Test
	public void testEncodeDownloadContentDisposition() throws UnsupportedEncodingException {
		Assert.assertNull(SystemUtils.encodeDownloadContentDisposition(null, null));
		String filename = "测试.xls";
		String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142";
		Assert.assertEquals("attachment; filename=\""+ new String(filename.getBytes("UTF-8"), "ISO8859-1") +"\"", SystemUtils.encodeDownloadContentDisposition(ua, filename));
		ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.1.1 Safari/605.1.15";
		Assert.assertEquals("attachment; filename=\""+ new String(filename.getBytes("UTF-8"), "ISO8859-1") +"\"", SystemUtils.encodeDownloadContentDisposition(ua, filename));
		ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:67.0) Gecko/20100101 Firefox/67.0";
		Assert.assertEquals("attachment; filename*=UTF-8''%E6%B5%8B%E8%AF%95.xls", SystemUtils.encodeDownloadContentDisposition(ua, filename));
		ua = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0)";
		Assert.assertEquals("attachment; filename=\"%E6%B5%8B%E8%AF%95.xls\"", SystemUtils.encodeDownloadContentDisposition(ua, filename));
		ua = "Opera/9.27 (Windows NT 5.2; U; zh-cn)";
		Assert.assertEquals("attachment; filename*=UTF-8''%E6%B5%8B%E8%AF%95.xls", SystemUtils.encodeDownloadContentDisposition(ua, filename));
	}
	
	@Setter
	@Getter
	public static class Entity {
		
		private Long id;
		
		private String name;
		
	}
	
	public static interface BaseService<B, C> {
		
	}
	
	public static interface SimpleService<B> {
		
	}
	
	public static abstract class AbstractEntityService implements BaseService<Entity, Long> {}
	
	public static class EntityService extends AbstractEntityService implements SimpleService<String> {}
	
}
