package net.saisimon.agtms.zuul.filter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import net.saisimon.agtms.api.UserInterface;
import net.saisimon.agtms.zuul.config.WhiteList;

@RefreshScope
@Component
public class AccessFilter extends ZuulFilter {
	
	private static final String AUTHORIZE_TOKEN = "X-TOKEN";
	private static final String AUTHORIZE_UID = "X-UID";
	
	@Autowired
	private WhiteList whiteList;
	@Autowired
	private UserInterface userInterface;

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		String uri = request.getRequestURI();
		if (whiteList != null && whiteList.getUrls().contains(uri)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		String token = request.getHeader(AUTHORIZE_TOKEN);
		if (StringUtils.isEmpty(token)) {
			token = request.getParameter(AUTHORIZE_TOKEN);
		}
		String uid = request.getHeader(AUTHORIZE_UID);
		if (StringUtils.isEmpty(uid)) {
			uid = request.getParameter(AUTHORIZE_UID);
		}
		if (StringUtils.isEmpty(token) || StringUtils.isEmpty(uid)) {
			accessDenied(ctx);
			return null;
		}
		if (!userInterface.checkToken(uid, token)) {
			accessDenied(ctx);
			return null;
		}
		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 0;
	}
	
	private void accessDenied(RequestContext ctx) {
		ctx.setSendZuulResponse(false);
		ctx.setResponseBody("{'code': 401, 'message': 'Access Denied'}");
		ctx.setResponseStatusCode(401);
		ctx.getResponse().setContentType("application/json;charset=UTF-8");
	}
	
}
