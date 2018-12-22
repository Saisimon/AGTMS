package net.saisimon.agtms.core.util;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

public class PropertyUtils {
	
	private PropertyUtils() {
		throw new IllegalAccessError();
	}
	
	public static Object fetchProperties(PropertySourceLoader loader, String name, String key) {
        try {
            ClassPathResource classPathResource = new ClassPathResource(name);
            List<PropertySource<?>> propertySources = loader.load(name, classPathResource);
            if (propertySources.size() == 0) {
                return null;
            }
            return propertySources.get(0).getProperty(key);
        } catch (IOException e) {
            return null;
        }
    }
	
	/**
	 * 读取 application.yml 文件的属性
	 * 
	 */
	public static Object fetchYaml(String key, Object defaultValue) {
		Object value = fetchProperties(new YamlPropertySourceLoader(), "application.yml", key);
		return value == null ? defaultValue : value;
    }
	
	/**
	 * 读取 application.properties 文件的属性
	 * 
	 */
	public static Object fetchProperty(String key, Object defaultValue) {
		Object value = fetchProperties(new PropertiesPropertySourceLoader(), "application.properties", key);
		return value == null ? defaultValue : value;
    }
	
}
