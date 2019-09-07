package net.saisimon.agtms.core.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.exception.AgtmsException;
import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.property.AgtmsProperties;

@RunWith(SpringRunner.class)
public class DomainUtilsTest {
	
	private static final String NAMESPACE = "test";
	private static final String NAME = "Test";
	
	@Autowired
	private DomainGenerater domainGenerater;
	
	@Test(expected = AgtmsException.class)
	public void testGetFieldWithNull() {
		DomainUtils.getField(null, "aaa");
	}
	
	@Test
	public void testGetField() throws Exception {
		try {
			Map<String, String> map = new HashMap<>();
			map.put("aaa", String.class.getName());
			Class<Domain> domainClass = domainGenerater.generate(NAMESPACE, map, NAME);
			Domain domain = domainClass.newInstance();
			Assert.assertNull(DomainUtils.getField(domain, "aaa"));
		} finally {
			domainGenerater.removeDomainClass(NAMESPACE, NAME);
		}
	}
	
	@Test(expected = AgtmsException.class)
	public void testSetFieldWithNull() {
		DomainUtils.setField(null, "aaa", null, String.class);
	}
	
	@Test
	public void testSetField() throws Exception {
		try {
			Map<String, String> map = new HashMap<>();
			map.put("aaa", String.class.getName());
			Class<Domain> domainClass = domainGenerater.generate(NAMESPACE, map, NAME);
			Domain domain = domainClass.newInstance();
			DomainUtils.setField(domain, "aaa", null, String.class);
		} finally {
			domainGenerater.removeDomainClass(NAMESPACE, NAME);
		}
	}
	
	@Test
	public void testfillCommonFieldsWithNull() {
		DomainUtils.fillCommonFields(null, null, null);
	}
	
	@Test
	public void testParseFieldValue() {
		Assert.assertNull(DomainUtils.parseFieldValue(null, null));
		Assert.assertNull(DomainUtils.parseFieldValue("", Classes.LONG.getKey()));
		Assert.assertNull(DomainUtils.parseFieldValue("", Classes.DOUBLE.getKey()));
		Assert.assertNull(DomainUtils.parseFieldValue("", Classes.DATE.getKey()));
		Date expected = DateUtil.parse("1970-01-01 23:59:59.999", FastDateFormat.getInstance(DatePattern.NORM_DATETIME_MS_PATTERN, TimeZone.getTimeZone("UTC"))).toJdkDate();
		Assert.assertEquals(expected, DomainUtils.parseFieldValue("1970-01-01T23:59:59.999Z", Classes.DATE.getKey()));
		expected = DateUtil.parse("1970-01-01", FastDateFormat.getInstance(DatePattern.NORM_DATE_PATTERN, TimeZone.getTimeZone("UTC"))).toJdkDate();
		Assert.assertEquals(expected, DomainUtils.parseFieldValue("1970-01-01", Classes.DATE.getKey()));
	}
	
	@Test(expected = AgtmsException.class)
	public void testParseFieldValueWithException() {
		DomainUtils.parseFieldValue("aaaa", Classes.DATE.getKey());
	}
	
	@Test
	public void testEncrypt() {
		Assert.assertNull(DomainUtils.encrypt(null));
		Assert.assertNull(DomainUtils.encrypt("test"));
	}
	
	@Test
	public void testDecrypt() {
		Assert.assertNull(DomainUtils.decrypt(null));
		Assert.assertNull(DomainUtils.decrypt("test"));
	}
	
	@Configuration
	public static class Config {
		
		@Bean
		public DomainGenerater domainGenerater() {
			return new DomainGenerater(new AgtmsProperties());
		}
		
	}
	
}
