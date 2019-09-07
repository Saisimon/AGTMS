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
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 资源权限拦截器
 * 
 * @author saisimon
 *
 */
public class ResourceInterceptor implements HandlerInterceptor {
	
	@Autowired
	private PremissionService premissionService;
	@Autowired
	private MessageService messageService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!(handler instanceof HandlerMethod) || hasPermission(request, (HandlerMethod) handler)) {
			return true;
		}
		noPermission(response);
		return false;
	}
	
	private boolean hasPermission(HttpServletRequest request, HandlerMethod handler) {
		ResourceInfo resourceInfo = handler.getMethodAnnotation(ResourceInfo.class);
		if (resourceInfo == null) {
			return true;
		}
		ControllerInfo controllerInfo = handler.getMethod().getDeclaringClass().getAnnotation(ControllerInfo.class);
		if (controllerInfo == null || SystemUtils.isBlank(controllerInfo.link())) {
			return true;
		}
		Set<Long> ownResourceIds = premissionService.getResourceIds(AuthUtils.getUid());
		if (CollectionUtils.isEmpty(ownResourceIds)) {
			return false;
		}
		Resource resource = getResource(controllerInfo.link(), parseUri(request, controllerInfo));
		if (resource == null || !ownResourceIds.contains(resource.getId())) {
			return false;
		}
		for (Functions func : resourceInfo.func()) {
			if (TemplateUtils.hasFunction(resource.getFunctions(), func)) {
				return true;
			}
		}
		return false;
	}

	private Long parseUri(HttpServletRequest request, ControllerInfo controllerInfo) {
		if (!"/management/main".equals(controllerInfo.link())) {
			return null;
		}
		String uri = request.getRequestURI();
		String[] strs = uri.split("/");
		if (strs.length < 4) {
			return null;
		}
		try {
			return Long.valueOf(strs[3]);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	private void noPermission(HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(403);
		String message = messageService.getMessage("no.permission");
		try (PrintWriter out = response.getWriter()) {
			out.append("{'code': 403, 'message': '" + message + "'}");
		}
	}
	
	private Resource getResource(String link, Long contentId) {
		FilterRequest filter = FilterRequest.build().and("link", link).and("contentId", contentId);
		return ResourceServiceFactory.get().findOne(filter).orElse(null);
	}
	
}
