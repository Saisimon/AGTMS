 package net.saisimon.agtms.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import net.saisimon.agtms.core.annotation.Mapping;

public final class MappingUtils {
	
	private MappingUtils() {
		throw new IllegalAccessError();
	}
	
	public static <T> Map<String, Object> entityToMap(T entity) {
		Map<String, Object> result = new HashMap<>();
		ReflectionUtils.doWithFields(entity.getClass(), field -> {
			if ("id".equalsIgnoreCase(field.getName())) {
				field.setAccessible(true);
				result.put(field.getName(), ReflectionUtils.getField(field, entity));
			} else {
				Mapping map = field.getAnnotation(Mapping.class);
				if (map != null) {
					field.setAccessible(true);
					result.put(field.getName(), ReflectionUtils.getField(field, entity));
				}
			}
		});
		return result;
	}
	
	public static <T> T mapToEntity(Class<T> entityClass, Map<String, Object> fieldMap) throws Exception {
		T entity = entityClass.newInstance();
		if (entityClass != null && fieldMap != null) {
			for (Entry<String, Object> entry : fieldMap.entrySet()) {
				Field field = ReflectionUtils.findField(entityClass, entry.getKey());
				if (field != null) {
					field.setAccessible(true);
					Object val = entry.getValue();
					if (val != null) {
						if (field.getType() == Long.class || field.getType() == long.class) {
							val = Long.valueOf(val.toString());
						}
						ReflectionUtils.setField(field, entity, val);
					}
				}
			}
		}
		return entity;
	}
	
	public static List<List<String>> getMapping(Class<?> entityClass) {
		String[][] mapss = new String[128][];
		if (entityClass != null) {
			ReflectionUtils.doWithFields(entityClass, field -> {
				Mapping map = field.getAnnotation(Mapping.class);
				if (map != null && map.columnIndex() >= 0 && map.fieldIndex() >= 0) {
					String[] maps = mapss[map.columnIndex()];
					if (maps == null) {
						maps = new String[128];
						mapss[map.columnIndex()] = maps;
					}
					String name = field.getName();
					if (StringUtils.isNotBlank(map.mapped())) {
						name += '.' + map.mapped();
					}
					maps[map.fieldIndex()] = name;
				}
			});
		}
		List<List<String>> result = new ArrayList<>();
		for (int i = 0; i < 128; i++) {
			if (mapss[i] != null) {
				List<String> list = new ArrayList<>();
				for (int j = 0; j < 128; j++) {
					if (mapss[i][j] != null) {
						list.add(mapss[i][j]);
					}
				}
				result.add(list);
			}
		}
		return result;
	}
	
	public static Map<String, Object> mapping(Map<String, Object> updateMap, Map<String, String> mapping) {
		if (CollectionUtils.isEmpty(mapping)) {
			return updateMap;
		}
		Map<String, Object> map = null;
		if (!CollectionUtils.isEmpty(updateMap)) {
			map = new HashMap<>(updateMap.size());
			for (Entry<String, Object> entry : updateMap.entrySet()) {
				String name = entry.getKey();
				String mappingName = null;
				if (mapping != null && StringUtils.isNotBlank(mappingName = mapping.get(name))) {
					name = mappingName;
				}
				map.put(name, entry.getValue());
			}
		}
		return map;
	}
	
	public static Map<String, String> reverse(Map<String, String> mapping) {
		Map<String, String> reverse = new HashMap<>(mapping.size());
		for (Entry<String, String> entry : mapping.entrySet()) {
			if (entry.getValue() != null) {
				reverse.put(entry.getValue(), entry.getKey());
			}
		}
		return reverse;
	}
	
}
