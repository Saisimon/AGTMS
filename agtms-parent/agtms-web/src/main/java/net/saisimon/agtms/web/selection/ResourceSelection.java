package net.saisimon.agtms.web.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.service.ResourceService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.config.runner.InitRunner;
import net.saisimon.agtms.web.service.common.MessageService;
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 资源下拉列表
 * 
 * @author saisimon
 *
 */
@Component
public class ResourceSelection extends AbstractSelection<String> {
	
	@Autowired
	private PremissionService premissionService;
	@Autowired
	protected MessageService messageService;

	@Override
	public Map<String, String> select() {
		return select(null, null, true);
	}
	
	public Map<String, String> select(String excludePath, Resource.ContentType contentType, boolean includeFunctions) {
		List<Resource> resources = getResources(excludePath, contentType);
		Map<String, String> resourceMap = MapUtil.newHashMap(resources.size() + 1, true);
		resourceMap.put("", "/");
		for (Resource resource : resources) {
			String name = resourceMap.getOrDefault(resource.getPath(), "");
			if (!name.endsWith("/")) {
				name += "/";
			}
			name += resource.getName();
			String path = resource.getPath() + "/" + resource.getId();
			if (path.equals(excludePath)) {
				continue;
			}
			resourceMap.put(path, name);
			if (includeFunctions) {
				List<Functions> functions = InitRunner.FUNCTION_MAP.get(resource.getLink());
				if (!CollectionUtils.isEmpty(functions)) {
					for (Functions function : functions) {
						resourceMap.put(path + ":" + function.getCode(), name + ":" + messageService.getMessage(SystemUtils.humpToCode(function.getFunction(), ".")));
					}
				}
			}
		}
		return resourceMap;
	}
	
	public List<Option<String>> buildNestedOptions(String excludePath, Resource.ContentType contentType, boolean includeFunctions) {
		List<Resource> resources = getResources(excludePath, contentType);
		return buildNestedOptions(resources, "", excludePath, includeFunctions);
	}
	
	private List<Option<String>> buildNestedOptions(List<Resource> resources, String path, String excludePath, boolean includeFunctions) {
		if (CollectionUtils.isEmpty(resources)) {
			return null;
		}
		List<Resource> currents = new ArrayList<>();
		List<Resource> rests = new ArrayList<>();
		for (Resource resource : resources) {
			if (path.equals(resource.getPath())) {
				currents.add(resource);
			} else {
				rests.add(resource);
			}
		}
		if (!CollectionUtils.isEmpty(currents)) {
			List<Option<String>> options = new ArrayList<>();
			for (Resource current : currents) {
				String newPath = current.getPath() + "/" + current.getId();
				if (newPath.equals(excludePath)) {
					continue;
				}
				Option<String> option = new Option<>(newPath, messageService.getMessage(current.getName()), buildNestedOptions(rests, newPath, excludePath, includeFunctions));
				if (includeFunctions && option.getChildren() == null) {
					option.setChildren(buildFunctionOptions(current, newPath));
				}
				options.add(option);
			}
			return options;
		}
		return null;
	}
	
	private List<Option<String>> buildFunctionOptions(Resource current, String path) {
		List<Functions> functions = InitRunner.FUNCTION_MAP.get(current.getLink());
		if (CollectionUtils.isEmpty(functions)) {
			return null;
		}
		List<Option<String>> options = new ArrayList<>(functions.size());
		for (Functions function : functions) {
			Option<String> option = new Option<>(path + ":" + function.getCode(), messageService.getMessage(SystemUtils.humpToCode(function.getFunction(), ".")), null);
			options.add(option);
		}
		return options;
	}
	
	private List<Resource> getResources(String excludePath, Resource.ContentType contentType) {
		ResourceService resourceService = ResourceServiceFactory.get();
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		userIds.add(Constant.SYSTEM_OPERATORID);
		return resourceService.getResources(excludePath, contentType, userIds);
	}

}
