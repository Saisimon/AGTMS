package net.saisimon.agtms.web.service.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Role;
import net.saisimon.agtms.core.domain.entity.RoleResource;
import net.saisimon.agtms.core.domain.entity.UserRole;
import net.saisimon.agtms.core.factory.RoleResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleServiceFactory;
import net.saisimon.agtms.core.factory.UserRoleServiceFactory;
import net.saisimon.agtms.core.service.RoleService;

@Service
public class PremissionService {
	
	@Cacheable(cacheNames=Constant.Cache.RESOURCE_IDS_NAME, key="#p0")
	public Map<String, Integer> getRoleResourceMap(Long userId) {
		if (userId == null) {
			return Collections.emptyMap();
		}
		Set<Long> roleIds = getRoleIds(userId, true);
		if (CollectionUtils.isEmpty(roleIds)) {
			return Collections.emptyMap();
		}
		return RoleResourceServiceFactory.get()
				.getRoleResources(roleIds, null)
				.parallelStream()
				.collect(Collectors.toMap(r -> r.getResourceId().toString(), RoleResource::getResourceFunctions, (v1, v2) -> {
					return v1 | v2;
				}));
	}
	
	@Cacheable(cacheNames=Constant.Cache.USER_IDS_NAME, key="#p0")
	public Set<Long> getUserIds(Long userId) {
		if (userId == null) {
			return Collections.emptySet();
		}
		Set<Long> userIds = new HashSet<>();
		userIds.add(userId);
		Set<Long> roleIds = getRoleIds(userId, false);
		if (CollectionUtils.isEmpty(roleIds)) {
			return userIds;
		}
		userIds.addAll(UserRoleServiceFactory.get().getUserRoles(roleIds).parallelStream().map(UserRole::getUserId).collect(Collectors.toSet()));
		return userIds;
	}
	
	private Set<Long> getRoleIds(Long userId, boolean includeSelfRole) {
		if (userId == null) {
			return Collections.emptySet();
		}
		List<UserRole> userRoles = UserRoleServiceFactory.get().getUserRoles(userId);
		RoleService roleService = RoleServiceFactory.get();
		Set<Long> roleIds = new HashSet<>();
		for (UserRole userRole : userRoles) {
			if (includeSelfRole) {
				roleIds.add(userRole.getRoleId());
			}
			List<Role> childrenRoles = roleService.getAllChildrenRoles(userRole.getRoleId(), userRole.getRolePath());
			if (childrenRoles != null) {
				roleIds.addAll(childrenRoles.parallelStream().map(Role::getId).collect(Collectors.toSet()));
			}
		}
		return roleIds;
	}
	
}
