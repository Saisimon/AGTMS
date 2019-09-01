package net.saisimon.agtms.web.selection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.service.ResourceService;
import net.saisimon.agtms.core.util.AuthUtils;
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

	@Override
	public Map<String, String> select() {
		return selectWithParent(null, null);
	}
	
	public Map<String, String> selectWithParent(String excludePath, Resource.ContentType contentType) {
		ResourceService resourceService = ResourceServiceFactory.get();
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		List<Resource> resources = resourceService.getResources(excludePath, contentType, userIds);
		Map<String, String> resourceMap = MapUtil.newHashMap(resources.size() + 1, true);
		resourceMap.put("", "/");
		for (Resource resource : resources) {
			String name = resourceMap.getOrDefault(resource.getPath(), "");
			if (!name.endsWith("/")) {
				name += "/";
			}
			resourceMap.put(resource.getPath() + "/" + resource.getId(), name + resource.getName());
		}
		if (excludePath != null) {
			resourceMap.remove(excludePath);
		}
		return resourceMap;
	}

}
