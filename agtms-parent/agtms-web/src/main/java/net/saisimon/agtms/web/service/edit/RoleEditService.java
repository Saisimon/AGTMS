package net.saisimon.agtms.web.service.edit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.domain.entity.Role;
import net.saisimon.agtms.core.domain.entity.RoleResource;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.EditGrid.FieldGroup;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleServiceFactory;
import net.saisimon.agtms.core.property.BasicProperties;
import net.saisimon.agtms.core.service.ResourceService;
import net.saisimon.agtms.core.service.RoleService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.config.runner.InitRunner;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.RoleParam;
import net.saisimon.agtms.web.selection.ResourceSelection;
import net.saisimon.agtms.web.selection.RoleSelection;
import net.saisimon.agtms.web.service.base.AbstractEditService;
import net.saisimon.agtms.web.service.common.PremissionService;
import net.saisimon.agtms.web.service.main.RoleMainService;

/**
 * 用户编辑服务
 * 
 * @author saisimon
 *
 */
@Service
public class RoleEditService extends AbstractEditService<Role> {
	
	@Autowired
	private RoleMainService roleMainService;
	@Autowired
	private RoleSelection roleSelection;
	@Autowired
	private ResourceSelection resourceSelection;
	@Autowired
	private PremissionService premissionService;
	@Autowired
	private BasicProperties basicProperties;
	
	public Result grid(Long id) {
		Role role = null;
		if (id != null) {
			RoleService roleService = RoleServiceFactory.get();
			Optional<Role> optional = roleService.findById(id);
			if (!optional.isPresent()) {
				return ErrorMessage.Role.ROLE_NOT_EXIST;
			}
			role = optional.get();
		}
		return ResultUtils.simpleSuccess(getEditGrid(role, RoleMainService.ROLE));
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result save(RoleParam body) {
		Result checkResult = checkOverflow(body);
		if (!ResultUtils.isSuccess(checkResult)) {
			return checkResult;
		}
		if (null != body.getId() && body.getId() > 0) {
			return updateRole(body);
		} else {
			return saveRole(body);
		}
	}
	
	@Override
	protected List<Breadcrumb> breadcrumbs(Role role, Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("user.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("role.management")).to("/role/main").build());
		if (role == null) {
			breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("create")).active(true).build());
		} else {
			breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("edit")).active(true).build());
		}
		return breadcrumbs;
	}
	
	@Override
	protected List<FieldGroup> groups(Role role, Object key) {
		Field<String> nameField = Field.<String>builder().name("name").text(messageService.getMessage("role.name")).type(Classes.STRING.getKey()).required(true).build();
		String path = role == null ? null : role.getPath() + "/" + role.getId();
		Field<Option<String>> pathField = Field.<Option<String>>builder().name("path").text(messageService.getMessage("parent.role"))
				.type("select").views(Views.SELECTION.getKey()).options(roleSelection.buildNestedOptions(path)).required(true).build();
		Field<Option<String>> resourcesField = Field.<Option<String>>builder().name("resources").text(messageService.getMessage("resource.name"))
				.type("select").views(Views.SELECTION.getKey()).options(resourceSelection.buildNestedOptions(null, null, true)).consists(Constant.Field.ALL_WITH_INDETERMINATE).multiple(true).build();
		Field<String> remarkField = Field.<String>builder().name("remark").text(messageService.getMessage("remark"))
				.type(Classes.STRING.getKey()).views(Views.TEXTAREA.getKey()).build();
		if (role != null) {
			nameField.setValue(role.getName());
			pathField.setValue(SystemUtils.isEmpty(role.getPath()) ? null : role.getPath());
			resourcesField.setValue(getRoleResourcePaths(role));
			remarkField.setValue(role.getRemark());
		} else {
			nameField.setValue("");
			remarkField.setValue("");
		}
		List<FieldGroup> groups = new ArrayList<>();
		groups.add(buildFieldGroup(messageService.getMessage("role.info"), 0, nameField, pathField, resourcesField, remarkField));
		return groups;
	}
	
	private List<String> getRoleResourcePaths(Role role) {
		List<RoleResource> roleResources = RoleResourceServiceFactory.get().getRoleResources(Arrays.asList(role.getId()), null);
		if (CollectionUtils.isEmpty(roleResources)) {
			return Collections.emptyList();
		}
		ResourceService resourceService = ResourceServiceFactory.get();
		List<String> roleResourcePaths = new ArrayList<>();
		for (RoleResource roleResource : roleResources) {
			Resource resource = resourceService.findById(roleResource.getResourceId()).orElse(null);
			if (resource == null) {
				continue;
			}
			List<Functions> functions = InitRunner.FUNCTION_MAP.get(resource.getLink());
			if (roleResource.getResourceFunctions().equals(0)) {
				roleResourcePaths.add(roleResource.getResourcePath() + "/" + roleResource.getResourceId());
			} else {
				for (Functions function : functions) {
					if (SystemUtils.hasFunction(roleResource.getResourceFunctions(), function)) {
						roleResourcePaths.add(roleResource.getResourcePath() + "/" + roleResource.getResourceId() + ":" + function.getCode());
					}
				}
			}
		}
		return roleResourcePaths;
	}
	
	private Result checkOverflow(RoleParam body) {
		if (body.getName().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("role.name"), 32);
		}
		if (body.getRemark() != null && body.getRemark().length() > 512) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("remark"), 512);
		}
		return ResultUtils.simpleSuccess();
	}
	
	private Result saveRole(RoleParam body) {
		RoleService roleService = RoleServiceFactory.get();
		Long userId = AuthUtils.getUid();
		Set<Long> userIds = premissionService.getUserIds(userId);
		if (roleService.exists(null, body.getName(), userIds)) {
			return ErrorMessage.Role.ROLE_ALREADY_EXISTS;
		}
		userIds.add(Constant.SYSTEM_OPERATORID);
		List<Role> roles = roleService.getRoles(null, userIds);
		if (!validatePath(body.getPath(), roles)) {
			return ErrorMessage.Common.PARAM_ERROR;
		}
		if (!checkDepth(body.getPath())) {
			Result result = ErrorMessage.Role.ROLE_MAX_DEPTH_LIMIT;
			result.setMessageArgs(new Object[]{ basicProperties.getMaxDepth() });
			return result;
		}
		Date time = new Date();
		Role role = new Role();
		role.setCreateTime(time);
		role.setName(body.getName());
		role.setOperatorId(userId);
		role.setPath(body.getPath());
		role.setRemark(body.getRemark());
		role.setUpdateTime(time);
		roleService.saveOrUpdate(role);
		roleMainService.grantResource(role.getId(), resourceSelection.select(), body.getResources());
		return ResultUtils.simpleSuccess();
	}

	private Result updateRole(RoleParam body) {
		RoleService roleService = RoleServiceFactory.get();
		Role oldRole = roleService.findById(body.getId()).orElse(null);
		if (oldRole == null) {
			return ErrorMessage.Role.ROLE_NOT_EXIST;
		}
		Long userId = AuthUtils.getUid();
		Set<Long> userIds = premissionService.getUserIds(userId);
		if (roleService.exists(body.getId(), body.getName(), userIds)) {
			return ErrorMessage.Role.ROLE_ALREADY_EXISTS;
		}
		if (body.getPath().equals(oldRole.getPath() + "/" + oldRole.getId())) {
			return ErrorMessage.Common.PARAM_ERROR;
		}
		userIds.add(Constant.SYSTEM_OPERATORID);
		List<Role> roles = roleService.getRoles(oldRole.getPath() + "/" + oldRole.getId(), userIds);
		if (!validatePath(body.getPath(), roles)) {
			return ErrorMessage.Common.PARAM_ERROR;
		}
		if (!checkDepth(body.getPath())) {
			Result result = ErrorMessage.Role.ROLE_MAX_DEPTH_LIMIT;
			result.setMessageArgs(new Object[]{ basicProperties.getMaxDepth() });
			return result;
		}
		oldRole.setName(body.getName());
		oldRole.setPath(body.getPath());
		oldRole.setRemark(body.getRemark());
		oldRole.setUpdateTime(new Date());
		roleService.saveOrUpdate(oldRole);
		roleMainService.grantResource(oldRole.getId(), resourceSelection.select(), body.getResources());
		return ResultUtils.simpleSuccess();
	}
	
	private boolean validatePath(String path, List<Role> roles) {
		if ("".equals(path)) {
			return true;
		}
		for (Role role : roles) {
			if (path.equals(role.getPath() + "/" + role.getId())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkDepth(String path) {
		if ("".equals(path)) {
			return true;
		}
		String[] strs = path.split("/");
		return strs.length <= basicProperties.getMaxDepth();
	}
	
}
