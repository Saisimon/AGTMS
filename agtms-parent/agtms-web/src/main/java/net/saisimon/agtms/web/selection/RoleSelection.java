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
import net.saisimon.agtms.core.domain.entity.Role;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.factory.RoleServiceFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.service.RoleService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.web.service.common.MessageService;
import net.saisimon.agtms.web.service.common.PremissionService;

@Component
public class RoleSelection extends AbstractSelection<String> {
	
	@Autowired
	private PremissionService premissionService;
	@Autowired
	protected MessageService messageService;
	
	@Override
	public Map<String, String> select() {
		return select(null);
	}
	
	public Map<String, String> select(String excludePath) {
		List<Role> roles = getRoles(excludePath);
		Map<String, String> roleMap = MapUtil.newHashMap(roles.size(), true);
		for (Role role : roles) {
			String name = roleMap.getOrDefault(role.getPath(), "");
			if (!name.endsWith("/")) {
				name += "/";
			}
			roleMap.put(role.getPath() + "/" + role.getId(), name + getMessage(role.getName()));
		}
		if (excludePath != null) {
			roleMap.remove(excludePath);
		}
		return roleMap;
	}
	
	public List<Option<String>> buildNestedOptions(String excludePath) {
		List<Role> roles = getRoles(excludePath);
		return buildNestedOptions(roles, "");
	}
	
	private List<Option<String>> buildNestedOptions(List<Role> roles, String path) {
		if (CollectionUtils.isEmpty(roles)) {
			return null;
		}
		List<Role> currents = new ArrayList<>();
		List<Role> rests = new ArrayList<>();
		for (Role role : roles) {
			if (path.equals(role.getPath())) {
				currents.add(role);
			} else {
				rests.add(role);
			}
		}
		List<Option<String>> options = new ArrayList<>();
		for (Role current : currents) {
			String newPath = current.getPath() + "/" + current.getId();
			Option<String> option = new Option<>(newPath, messageService.getMessage(current.getName()), buildNestedOptions(rests, newPath));
			options.add(option);
		}
		return options;
	}
	
	private List<Role> getRoles(String excludePath) {
		RoleService roleService = RoleServiceFactory.get();
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		userIds.add(Constant.SYSTEM_OPERATORID);
		return roleService.getRoles(excludePath, userIds);
	}
	
}
