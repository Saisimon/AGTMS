package net.saisimon.agtms.remote.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.saisimon.agtms.core.domain.entity.Template;

/**
 * 远程 API 接口
 * 
 * @author saisimon
 *
 */
public interface ApiService {
	
	@RequestMapping(method = RequestMethod.POST, value = "/agtms/templates")
	List<Template> templates();
	
	@RequestMapping(method = RequestMethod.POST, value = "/agtms/{key}/template")
	Template template(@PathVariable("key") String key);
	
	@RequestMapping(method = RequestMethod.POST, value = "/agtms/{key}/selection")
	LinkedHashMap<?, String> selection(@PathVariable("key") String key, @RequestBody Map<String, Object> body);
	
	@RequestMapping(method = RequestMethod.POST, value = "/agtms/{key}/count")
	Long count(@PathVariable("key") String key, @RequestBody(required = false) Map<String, Object> body);
	
	@RequestMapping(method = RequestMethod.POST, value = "/agtms/{key}/findList")
	List<Map<String, Object>> findList(@PathVariable("key") String key, @RequestBody Map<String, Object> body);
	
	@RequestMapping(method = RequestMethod.POST, value = "/agtms/{key}/findPage")
	public Map<String, Object> findPage(@PathVariable("key") String key, @RequestBody Map<String, Object> body);
	
	@RequestMapping(method = RequestMethod.POST, value = "/agtms/{key}/findOne")
	public Map<String, Object> findOne(@PathVariable("key") String key, @RequestBody Map<String, Object> body);
	
	@RequestMapping(method = RequestMethod.POST, value = "/agtms/{key}/findById")
	public Map<String, Object> findById(@PathVariable("key") String key, @RequestParam(name="id") String id);
	
	@RequestMapping(method = RequestMethod.POST, value = "/agtms/{key}/delete")
	public Long delete(@PathVariable("key") String key, @RequestBody(required = false) Map<String, Object> body);
	
	@RequestMapping(method = RequestMethod.POST, value = "/agtms/{key}/deleteEntity")
	public void deleteEntity(@PathVariable("key") String key, @RequestBody Map<String, Object> body);
	
	@RequestMapping(method = RequestMethod.POST, value = "/agtms/{key}/saveOrUpdate")
	public Map<String, Object> saveOrUpdate(@PathVariable("key") String key, @RequestBody Map<String, Object> body);
	
	@RequestMapping(method = RequestMethod.POST, value = "/agtms/{key}/batchUpdate")
	public void batchUpdate(@PathVariable("key") String key, @RequestBody Map<String, Object> body);
	
}
