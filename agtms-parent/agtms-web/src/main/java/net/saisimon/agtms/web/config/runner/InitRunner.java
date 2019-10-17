package net.saisimon.agtms.web.config.runner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.domain.entity.Role;
import net.saisimon.agtms.core.domain.entity.RoleResource;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.entity.UserRole;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleServiceFactory;
import net.saisimon.agtms.core.factory.UserRoleServiceFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.property.AccountProperties;
import net.saisimon.agtms.core.service.ResourceService;
import net.saisimon.agtms.core.service.RoleResourceService;
import net.saisimon.agtms.core.service.RoleService;
import net.saisimon.agtms.core.service.UserRoleService;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.service.main.ManagementMainService;
import net.saisimon.agtms.web.service.main.NavigationMainService;
import net.saisimon.agtms.web.service.main.OperationMainService;
import net.saisimon.agtms.web.service.main.RoleMainService;
import net.saisimon.agtms.web.service.main.SelectionMainService;
import net.saisimon.agtms.web.service.main.TaskMainService;
import net.saisimon.agtms.web.service.main.TemplateMainService;
import net.saisimon.agtms.web.service.main.UserMainService;

/**
 * 初始化操作
 * 
 * @author saisimon
 *
 */
@Component
public class InitRunner implements CommandLineRunner {
	
	public static final String ADMIN_ROLE_NAME = "system.admin";
	public static final String EDITOR_ROLE_NAME = "template.editor";
	
	public static final Map<String, List<Functions>> FUNCTION_MAP = new HashMap<>();
	static {
		FUNCTION_MAP.put("/user/main", UserMainService.SUPPORT_FUNCTIONS);
		FUNCTION_MAP.put("/role/main", RoleMainService.SUPPORT_FUNCTIONS);
		FUNCTION_MAP.put("/navigation/main", NavigationMainService.SUPPORT_FUNCTIONS);
		FUNCTION_MAP.put("/template/main", TemplateMainService.SUPPORT_FUNCTIONS);
		FUNCTION_MAP.put("/selection/main", SelectionMainService.SUPPORT_FUNCTIONS);
		FUNCTION_MAP.put("/task/main", TaskMainService.SUPPORT_FUNCTIONS);
		FUNCTION_MAP.put("/operation/main", OperationMainService.SUPPORT_FUNCTIONS);
		FUNCTION_MAP.put("/management/main", ManagementMainService.SUPPORT_FUNCTIONS);
	}
	
	@Autowired
	private AccountProperties accountProperties;

	@Override
	@Transactional(rollbackOn=Exception.class)
	public void run(String... args) throws Exception {
		Resource userModuleResource = buildResource("user.module", "users", null, null);
		Resource userManagementResource = buildResource("user.management", "link", "/user/main", userModuleResource);
		Resource roleManagementResource = buildResource("role.management", "link", "/role/main", userModuleResource);
		
		Resource systemModuleResource = buildResource("system.module", "cogs", null, null);
		Resource navigationManagementResource = buildResource("navigation.management", "link", "/navigation/main", systemModuleResource);
		Resource templateManagementResource = buildResource("template.management", "link", "/template/main", systemModuleResource);
		Resource selectionManagementResource = buildResource("selection.management", "link", "/selection/main", systemModuleResource);
		Resource taskManagementResource = buildResource("task.management", "link", "/task/main", systemModuleResource);
		Resource operationManagementResource = buildResource("operation.management", "link", "/operation/main", systemModuleResource);
		
		Role adminRole = buildRole(ADMIN_ROLE_NAME, null);
		buildRoleResource(adminRole, userModuleResource);
		buildRoleResource(adminRole, userManagementResource);
		buildRoleResource(adminRole, roleManagementResource);
		User adminUser = buildUser(accountProperties.getAdmin().getUsername(), accountProperties.getAdmin().getPassword());
		buildUserRole(adminUser, adminRole);
		
		Role editorRole = buildRole(EDITOR_ROLE_NAME, adminRole);
		buildRoleResource(editorRole, systemModuleResource);
		buildRoleResource(editorRole, navigationManagementResource);
		buildRoleResource(editorRole, templateManagementResource);
		buildRoleResource(editorRole, selectionManagementResource);
		buildRoleResource(editorRole, taskManagementResource);
		buildRoleResource(editorRole, operationManagementResource);
		User editorUser = buildUser(accountProperties.getEditor().getUsername(), accountProperties.getEditor().getPassword());
		buildUserRole(editorUser, editorRole);
	}
	
	private void buildRoleResource(Role role, Resource resource) {
		RoleResourceService roleResourceService = RoleResourceServiceFactory.get();
		if (roleResourceService.exists(role.getId(), resource.getId())) {
			return;
		}
		RoleResource roleResource = new RoleResource();
		roleResource.setResourceId(resource.getId());
		roleResource.setResourcePath(resource.getPath());
		roleResource.setResourceFunctions(SystemUtils.getFunctions(FUNCTION_MAP.get(resource.getLink())));
		roleResource.setRoleId(role.getId());
		roleResourceService.saveOrUpdate(roleResource);
	}
	
	private void buildUserRole(User user, Role... roles) {
		UserRoleService userRoleService = UserRoleServiceFactory.get();
		for (Role role : roles) {
			if (userRoleService.exists(user.getId(), role.getId())) {
				continue;
			}
			UserRole userRole = new UserRole();
			userRole.setRoleId(role.getId());
			userRole.setRolePath(role.getPath());
			userRole.setUserId(user.getId());
			userRoleService.saveOrUpdate(userRole);
		}
	}
	
	private Resource buildResource(String name, String icon, String link, Resource parentResource) {
		ResourceService resourceService = ResourceServiceFactory.get();
		Resource resource = resourceService.getResourceByNameAndOperatorId(name, Constant.SYSTEM_OPERATORID);
		if (resource != null) {
			return resource;
		}
		resource = new Resource();
		Date time = new Date();
		resource.setCreateTime(time);
		resource.setIcon(icon);
		resource.setLink(link);
		resource.setName(name);
		String path = "";
		if (parentResource != null) {
			path = parentResource.getPath() + "/" + parentResource.getId();
		}
		resource.setPath(path);
		resource.setOperatorId(Constant.SYSTEM_OPERATORID);
		resource.setUpdateTime(time);
		resourceService.saveOrUpdate(resource);
		return resource;
	}
	
	private Role buildRole(String name, Role parentRole) {
		RoleService roleService = RoleServiceFactory.get();
		Role role = roleService.getRole(name, Constant.SYSTEM_OPERATORID);
		if (role != null) {
			return role;
		}
		role = new Role();
		Date time = new Date();
		role.setCreateTime(time);
		role.setUpdateTime(time);
		role.setName(name);
		String path = "";
		if (parentRole != null) {
			path = parentRole.getPath() + "/" + parentRole.getId();
		}
		role.setPath(path);
		role.setOperatorId(Constant.SYSTEM_OPERATORID);
		role.setRemark(name);
		roleService.saveOrUpdate(role);
		return role;
	}
	
	private User buildUser(String name, String password) {
		UserService userService = UserServiceFactory.get();
		User user = userService.getUserByLoginNameOrEmail(name, null);
		if (user != null) {
			return user;
		}
		user = new User();
		user.setLoginName(name);
		String salt = AuthUtils.createSalt();
		String hmacPwd = AuthUtils.hmac(password, salt);
		Date time = new Date();
		user.setNickname(name);
		user.setCreateTime(time);
		user.setUpdateTime(time);
		user.setSalt(salt);
		user.setPassword(hmacPwd);
		user.setStatus(UserStatuses.NORMAL.getStatus());
		userService.saveOrUpdate(user);
		return user;
	}
	
}
