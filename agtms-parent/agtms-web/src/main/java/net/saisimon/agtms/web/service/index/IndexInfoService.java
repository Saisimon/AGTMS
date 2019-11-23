package net.saisimon.agtms.web.service.index;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.factory.NotificationServiceFactory;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.factory.TaskServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.ResourceService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.dto.resp.StatisticsInfo;
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 首页信息服务
 * 
 * @author saisimon
 *
 */
@Service
public class IndexInfoService {
	
	@Autowired
	private PremissionService premissionService;
	
	public Result statistics() {
		Long userId = AuthUtils.getUid();
		Map<String, Integer> ownRoleResourceMap = premissionService.getRoleResourceMap(userId);
		if (CollectionUtils.isEmpty(ownRoleResourceMap)) {
			return ResultUtils.simpleSuccess();
		}
		Set<Long> userIds = premissionService.getUserIds(userId);
		StatisticsInfo info = new StatisticsInfo();
		ResourceService resourceService = ResourceServiceFactory.get();
		Resource userResource = resourceService.getResourceByLinkAndContentId("/user/main", null);
		if (userResource != null && ownRoleResourceMap.get(userResource.getId().toString()) != null) {
			info.setUserCount(UserServiceFactory.get().count(FilterRequest.build()));
		}
		Resource navigationResource = resourceService.getResourceByLinkAndContentId("/navigation/main", null);
		if (navigationResource != null && ownRoleResourceMap.get(navigationResource.getId().toString()) != null) {
			FilterRequest filter = FilterRequest.build();
			filter.and(Constant.OPERATORID, userIds, Constant.Operator.IN).and("contentType", Resource.ContentType.NAVIGATION.getValue());
			info.setNavigationCount(resourceService.count(filter));
		}
		Resource templateResource = resourceService.getResourceByLinkAndContentId("/template/main", null);
		if (templateResource != null && ownRoleResourceMap.get(templateResource.getId().toString()) != null) {
			FilterRequest filter = FilterRequest.build();
			filter.and(Constant.OPERATORID, userIds, Constant.Operator.IN);
			info.setTemplateCount(TemplateServiceFactory.get().count(filter));
		}
		Resource selectionResource = resourceService.getResourceByLinkAndContentId("/selection/main", null);
		if (selectionResource != null && ownRoleResourceMap.get(selectionResource.getId().toString()) != null) {
			FilterRequest filter = FilterRequest.build();
			filter.and(Constant.OPERATORID, userIds, Constant.Operator.IN);
			info.setSelectionCount(SelectionServiceFactory.get().count(filter));
		}
		Resource taskResource = resourceService.getResourceByLinkAndContentId("/task/main", null);
		if (taskResource != null && ownRoleResourceMap.get(taskResource.getId().toString()) != null) {
			FilterRequest filter = FilterRequest.build();
			filter.and(Constant.OPERATORID, userIds, Constant.Operator.IN);
			info.setTaskCount(TaskServiceFactory.get().count(filter));
		}
		FilterRequest filter = FilterRequest.build();
		filter.and(Constant.OPERATORID, userId);
		info.setNotificationCount(NotificationServiceFactory.get().count(filter));
		return ResultUtils.simpleSuccess(info);
	}
	
}
