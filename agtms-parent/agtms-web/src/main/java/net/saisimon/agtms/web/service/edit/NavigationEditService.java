package net.saisimon.agtms.web.service.edit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.domain.entity.RoleResource;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.UserRole;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.EditGrid.FieldGroup;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleResourceServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.factory.UserRoleServiceFactory;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.service.ResourceService;
import net.saisimon.agtms.core.service.RoleResourceService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.NavigationParam;
import net.saisimon.agtms.web.selection.ResourceSelection;
import net.saisimon.agtms.web.service.base.AbstractEditService;
import net.saisimon.agtms.web.service.common.PremissionService;
import net.saisimon.agtms.web.service.main.NavigationMainService;

/**
 * 导航编辑服务
 * 
 * @author saisimon
 *
 */
@Service
public class NavigationEditService extends AbstractEditService<Resource> {
	
	@Autowired
	private ResourceSelection resourceSelection;
	@Autowired
	private PremissionService premissionService;
	@Autowired
	private AgtmsProperties agtmsProperties;
	
	public Result grid(Long id) {
		Resource resource = null;
		if (id != null) {
			ResourceService resourceService = ResourceServiceFactory.get();
			resource = resourceService.findById(id).orElse(null);
			if (resource == null) {
				return ErrorMessage.Navigation.NAVIGATION_NOT_EXIST;
			}
			Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
			if (!userIds.contains(resource.getOperatorId())) {
				return ErrorMessage.Navigation.NAVIGATION_NOT_EXIST;
			}
		}
		return ResultUtils.simpleSuccess(getEditGrid(resource, NavigationMainService.NAVIGATION));
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result save(NavigationParam body) {
		if (body.getName().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("name"), 32);
		}
		if (body.getIcon().length() > 64) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("icon"), 64);
		}
		if (body.getPath() == null) {
			body.setPath("");
		}
		if (null != body.getId() && body.getId() > 0) {
			return updateResource(body);
		} else {
			return saveResource(body);
		}
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Resource resource, Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("system.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("navigation.management")).to("/navigation/main").build());
		if (resource == null) {
			breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("create")).active(true).build());
		} else {
			breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("edit")).active(true).build());
		}
		return breadcrumbs;
	}
	
	@Override
	protected List<FieldGroup> groups(Resource resource, Object key) {
		String path = resource == null ? null : resource.getPath() + "/" + resource.getId();
		List<Option<String>> options = resourceSelection.buildNestedOptions(path, Resource.ContentType.NAVIGATION, false);
		Field<Option<String>> pathField = Field.<Option<String>>builder().name("path").text(messageService.getMessage("parent.navigation"))
				.type("select").views(Views.SELECTION.getKey()).options(options).build();
		Field<String> iconField = Field.<String>builder().name("icon").text(messageService.getMessage("icon"))
				.type(Classes.STRING.getKey()).required(true).views(Views.ICON.getKey()).build();
		Field<String> titleField = Field.<String>builder().name("name").text(messageService.getMessage("title"))
				.type(Classes.STRING.getKey()).required(true).build();
		if (resource != null) {
			pathField.setValue(SystemUtils.isEmpty(resource.getPath()) ? null : resource.getPath());
			iconField.setValue(resource.getIcon());
			titleField.setValue(resource.getName());
		} else {
			iconField.setValue(Resource.DEFAULT_ICON);
			titleField.setValue("");
		}
		List<FieldGroup> groups = new ArrayList<>();
		groups.add(buildFieldGroup(messageService.getMessage("navigation.info"), 0, pathField, iconField, titleField));
		return groups;
	}
	
	private Result updateResource(NavigationParam body) {
		ResourceService resourceService = ResourceServiceFactory.get();
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		Resource oldResource = resourceService.findById(body.getId()).orElse(null);
		if (oldResource == null) {
			return ErrorMessage.Navigation.NAVIGATION_NOT_EXIST;
		}
		if (!body.getName().equals(oldResource.getName()) && resourceService.exists(body.getName(), Resource.ContentType.NAVIGATION, userIds)) {
			return ErrorMessage.Navigation.NAVIGATION_ALREADY_EXISTS;
		}
		if (body.getPath().equals(oldResource.getPath() + "/" + oldResource.getId())) {
			return ErrorMessage.Common.PARAM_ERROR;
		}
		if (!validatePath(body.getPath(), oldResource.getPath() + "/" + oldResource.getId(), userIds)) {
			return ErrorMessage.Common.PARAM_ERROR;
		}
		if (!checkDepth(body.getPath())) {
			Result result = ErrorMessage.Navigation.NAVIGATION_MAX_DEPTH_LIMIT;
			result.setMessageArgs(new Object[]{ agtmsProperties.getMaxDepth() });
			return result;
		}
		Date time = new Date();
		List<Resource> childrens = resourceService.getAllChildrenResources(oldResource.getId(), oldResource.getPath());
		if (!CollectionUtils.isEmpty(childrens)) {
			for (Resource children : childrens) {
				String newPathPrefix = body.getPath() + "/" + body.getId();
				String oldPathPrefix = oldResource.getPath() + "/" + oldResource.getId();
				String oldPath = children.getPath();
				oldPath = oldPath.replaceFirst(oldPathPrefix, newPathPrefix);
				if (Resource.ContentType.NAVIGATION.getValue().equals(children.getContentType()) && !checkDepth(oldPath)) {
					Result result = ErrorMessage.Navigation.NAVIGATION_MAX_DEPTH_LIMIT;
					result.setMessageArgs(new Object[]{ agtmsProperties.getMaxDepth() });
					return result;
				}
				children.setPath(oldPath);
				children.setUpdateTime(time);
			}
			TemplateService templateService = TemplateServiceFactory.get();
			for (Resource children : childrens) {
				resourceService.saveOrUpdate(children);
				if (Resource.ContentType.TEMPLATE.getValue().equals(children.getContentType())) {
					Template template = templateService.findById(children.getContentId()).orElse(null);
					if (template != null) {
						Map<String, Object> updateMap = new HashMap<>();
						updateMap.put("path", children.getPath());
						updateMap.put(Constant.UPDATETIME, time);
						templateService.update(template.getId(), updateMap);
					}
				}
			}
		}
		oldResource.setPath(body.getPath());
		oldResource.setIcon(body.getIcon());
		oldResource.setName(body.getName());
		oldResource.setUpdateTime(time);
		resourceService.saveOrUpdate(oldResource);
		return ResultUtils.simpleSuccess();
	}
	
	private Result saveResource(NavigationParam body) {
		ResourceService resourceService = ResourceServiceFactory.get();
		Long userId = AuthUtils.getUid();
		Set<Long> userIds = premissionService.getUserIds(userId);
		if (resourceService.exists(body.getName(), Resource.ContentType.NAVIGATION, userIds)) {
			return ErrorMessage.Navigation.NAVIGATION_ALREADY_EXISTS;
		}
		if (!validatePath(body.getPath(), null, userIds)) {
			return ErrorMessage.Common.PARAM_ERROR;
		}
		if (!checkDepth(body.getPath())) {
			Result result = ErrorMessage.Navigation.NAVIGATION_MAX_DEPTH_LIMIT;
			result.setMessageArgs(new Object[]{ agtmsProperties.getMaxDepth() });
			return result;
		}
		Date time = new Date();
		Resource newResource = new Resource();
		newResource.setPath(body.getPath());
		newResource.setIcon(body.getIcon());
		newResource.setName(body.getName());
		newResource.setCreateTime(time);
		newResource.setOperatorId(userId);
		newResource.setUpdateTime(time);
		newResource.setContentType(Resource.ContentType.NAVIGATION.getValue());
		resourceService.saveOrUpdate(newResource);
		List<UserRole> useRoles = UserRoleServiceFactory.get().getUserRoles(userId);
		RoleResourceService roleResourceService = RoleResourceServiceFactory.get();
		for (UserRole userRole : useRoles) {
			RoleResource roleResource = new RoleResource();
			roleResource.setResourceId(newResource.getId());
			roleResource.setResourcePath(newResource.getPath());
			roleResource.setResourceFunctions(0);
			roleResource.setRoleId(userRole.getRoleId());
			roleResourceService.saveOrUpdate(roleResource);
		}
		return ResultUtils.simpleSuccess();
	}
	
	public boolean validatePath(String path, String excludePath, Set<Long> userIds) {
		if ("".equals(path)) {
			return true;
		}
		List<Resource> resources = ResourceServiceFactory.get().getResources(null, Resource.ContentType.NAVIGATION, userIds);
		for (Resource resource : resources) {
			if (path.equals(resource.getPath() + "/" + resource.getId())) {
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
		return strs.length <= agtmsProperties.getMaxDepth();
	}
	
}
