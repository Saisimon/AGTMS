package net.saisimon.agtms.web.config.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.ResourceInfo;
import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.service.common.MessageService;
import net.saisimon.agtms.web.service.user.UserInfoService;

/**
 * 资源权限拦截器
 * 
 * @author saisimon
 *
 */
public class ResourceInterceptor implements HandlerInterceptor {
	
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private MessageService messageService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		HandlerMethod h = (HandlerMethod) handler;
		ResourceInfo resourceInfo = h.getMethodAnnotation(ResourceInfo.class);
		if (resourceInfo == null) {
			return true;
		}
		ControllerInfo controllerInfo = h.getMethod().getDeclaringClass().getAnnotation(ControllerInfo.class);
		if (controllerInfo == null || SystemUtils.isBlank(controllerInfo.link())) {
			return true;
		}
		Set<Long> ownResourceIds = userInfoService.getResourceIds(AuthUtils.getUid());
		if (CollectionUtils.isEmpty(ownResourceIds)) {
			noPermission(request, response);
			return false;
		}
		String link = controllerInfo.link();
		Long contentId = null;
		if ("/management/main".equals(link)) {
			String uri = request.getRequestURI();
			String[] strs = uri.split("/");
			if (strs.length < 4) {
				noPermission(request, response);
				return false;
			}
			try {
				contentId = Long.valueOf(strs[3]);
			} catch (NumberFormatException e) {}
		}
		Resource resource = getResource(link, contentId);
		if (resource == null || !ownResourceIds.contains(resource.getId())) {
			noPermission(request, response);
			return false;
		}
		for (Functions func : resourceInfo.func()) {
			if (TemplateUtils.hasFunction(resource.getFunctions(), func)) {
				return true;
			}
		}
		noPermission(request, response);
		return false;
	}
	
	private void noPermission(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setContentType("application/json;charset=UTF-8");
		httpResponse.setStatus(403);
		String message = messageService.getMessage("no.permission");
		try (PrintWriter out = httpResponse.getWriter()) {
			out.append("{'code': 403, 'message': '" + message + "'}");
		}
	}
	
	private Resource getResource(String link, Long contentId) {
		FilterRequest filter = FilterRequest.build().and("link", link).and("contentId", contentId);
		return ResourceServiceFactory.get().findOne(filter).orElse(null);
	}
	
}
