package net.saisimon.agtms.core.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.saisimon.agtms.core.domain.entity.Template;

/**
 * 远程服务接口
 * 
 * @author saisimon
 *
 */
public interface RemoteService {
	
	List<Template> templates(String serviceId);
	
	LinkedHashMap<?, String> selection(String serviceId, String key, Map<String, Object> body);
	
}
