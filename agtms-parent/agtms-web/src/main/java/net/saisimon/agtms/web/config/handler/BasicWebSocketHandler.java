package net.saisimon.agtms.web.config.handler;

import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.TokenUtils;

public abstract class BasicWebSocketHandler extends AbstractWebSocketHandler {
	
	protected Template getTemplate(Map<String, Object> request, WebSocketSession session) {
		Object midObj = request.get("mid");
		if (midObj == null) {
			return null;
		}
		UserInfo userInfo = getCurrentUser(session);
		if (userInfo == null) {
			return null;
		}
		TemplateService templateService = TemplateServiceFactory.get();
		return templateService.getTemplate(Long.valueOf(midObj.toString()), userInfo.getUserId());
	}
	
	protected UserInfo getCurrentUser(WebSocketSession session) {
		List<String> tokenHeaders = session.getHandshakeHeaders().get(TokenUtils.TOKEN_SIGN);
		if (CollectionUtils.isEmpty(tokenHeaders)) {
			return null;
		}
		return TokenUtils.getUserInfo(tokenHeaders.get(0));
	}
	
}
