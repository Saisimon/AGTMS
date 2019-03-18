package net.saisimon.agtms.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

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

}
