package net.saisimon.agtms.web.config.runner;

import java.util.Date;

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
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleServiceFactory;
import net.saisimon.agtms.core.factory.UserRoleServiceFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.service.ResourceService;
import net.saisimon.agtms.core.service.RoleResourceService;
import net.saisimon.agtms.core.service.RoleService;
import net.saisimon.agtms.core.service.UserRoleService;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.service.main.NavigationMainService;
import net.saisimon.agtms.web.service.main.OperationMainService;
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
	
	private static final String EDITOR_USERNAME = "editor";
	private static final String EDITOR_PASSWORD = "editor";
	
	private static final String VIEWER_USERNAME = "viewer";
	private static final String VIEWER_PASSWORD = "viewer";
	
	private static final String ADMIN_ROLE_NAME = "ADMIN";
	private static final String EDITOR_ROLE_NAME = "EDITOR";
	private static final String VIEWER_ROLE_NAME = "VIEWER";
	
	@Autowired
	private AgtmsProperties agtmsProperties;

	@Override
	@Transactional(rollbackOn=Exception.class)
	public void run(String... args) throws Exception {
		Resource userModuleResource = buildResource("user.module", "users", null, null, 0);
		Resource userManagementResource = buildResource("user.management", "link", "/user/main", userModuleResource, TemplateUtils.getFunctions(UserMainService.SUPPORT_FUNCTIONS));
		
		Resource systemModuleResource = buildResource("system.module", "cogs", null, null, 0);
		Resource navigationManagementResource = buildResource("navigation.management", "link", "/navigation/main", systemModuleResource, TemplateUtils.getFunctions(NavigationMainService.SUPPORT_FUNCTIONS));
		Resource templateManagementResource = buildResource("template.management", "link", "/template/main", systemModuleResource, TemplateUtils.getFunctions(TemplateMainService.SUPPORT_FUNCTIONS));
		Resource selectionManagementResource = buildResource("selection.management", "link", "/selection/main", systemModuleResource, TemplateUtils.getFunctions(SelectionMainService.SUPPORT_FUNCTIONS));
		Resource taskManagementResource = buildResource("task.management", "link", "/task/main", systemModuleResource, TemplateUtils.getFunctions(TaskMainService.SUPPORT_FUNCTIONS));
		Resource operationManagementResource = buildResource("operation.management", "link", "/operation/main", systemModuleResource, TemplateUtils.getFunctions(OperationMainService.SUPPORT_FUNCTIONS));
		
		Role adminRole = buildRole(ADMIN_ROLE_NAME, null);
		buildRoleResource(adminRole, userModuleResource, userManagementResource);
		User adminUser = buildAdminUser();
		buildUserRole(adminUser, adminRole);
		
		Role editorRole = buildRole(EDITOR_ROLE_NAME, adminRole);
		buildRoleResource(editorRole, systemModuleResource, navigationManagementResource, templateManagementResource, selectionManagementResource);
		User editorUser = buildUser(EDITOR_USERNAME, EDITOR_PASSWORD);
		buildUserRole(editorUser, editorRole);
		
		Role viewerRole = buildRole(VIEWER_ROLE_NAME, editorRole);
		buildRoleResource(viewerRole, systemModuleResource, taskManagementResource, operationManagementResource);
		User viewerUser = buildUser(VIEWER_USERNAME, VIEWER_PASSWORD);
		buildUserRole(viewerUser, viewerRole);
	}
	
	private void buildRoleResource(Role role, Resource... resources) {
		RoleResourceService roleResourceService = RoleResourceServiceFactory.get();
		for (Resource resource : resources) {
			if (roleResourceService.exists(role.getId(), resource.getId())) {
				continue;
			}
			RoleResource roleResource = new RoleResource();
			roleResource.setResourceId(resource.getId());
			roleResource.setRoleId(role.getId());
			roleResourceService.saveOrUpdate(roleResource);
		}
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
	
	private Resource buildResource(String name, String icon, String link, Resource parentResource, Integer functions) {
		ResourceService resourceService = ResourceServiceFactory.get();
		Resource resource = resourceService.getResource(name, Constant.SYSTEM_OPERATORID);
		if (resource != null) {
			return resource;
		}
		resource = new Resource();
		Date time = new Date();
		resource.setCreateTime(time);
		resource.setFunctions(functions);
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
		role.setRemark(name.toLowerCase() + ".role");
		roleService.saveOrUpdate(role);
		return role;
	}
	
	private User buildAdminUser() {
		UserService userService = UserServiceFactory.get();
		User user = userService.getUserByLoginNameOrEmail(agtmsProperties.getAdminUsername(), null);
		if (user != null) {
			return user;
		}
		user = new User();
		user.setLoginName(agtmsProperties.getAdminUsername());
		String salt = AuthUtils.createSalt();
		String hmacPwd = AuthUtils.hmac(agtmsProperties.getAdminPassword(), salt);
		Date time = new Date();
		user.setNickname(agtmsProperties.getAdminUsername());
		user.setCreateTime(time);
		user.setUpdateTime(time);
		user.setSalt(salt);
		user.setPassword(hmacPwd);
		user.setStatus(UserStatuses.NORMAL.getStatus());
		userService.saveOrUpdate(user);
		return user;
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
