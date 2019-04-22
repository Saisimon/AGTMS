package net.saisimon.agtms.web.config.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;

import cn.hutool.core.util.NumberUtil;
import lombok.Setter;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.config.property.WhiteList;
import net.saisimon.agtms.web.config.property.WhitePrefix;

public class AccessFilter implements Filter {
	
	public static final String AUTHORIZE_TOKEN = "X-TOKEN";
	public static final String AUTHORIZE_UID = "X-UID";
	
	@Setter
	private WhiteList whiteList;
	@Setter
	private WhitePrefix whitePrefix;
	@Setter
	private CorsConfiguration corsConfiguration;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			chain.doFilter(request, response);
			return;
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		if (httpRequest.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
			chain.doFilter(request, response);
			return;
		}
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		if (!needCheck(httpRequest)) {
			chain.doFilter(request, response);
			return;
		}
		String token = httpRequest.getHeader(AUTHORIZE_TOKEN);
		if (SystemUtils.isEmpty(token)) {
			token = httpRequest.getParameter(AUTHORIZE_TOKEN);
		}
		String uid = httpRequest.getHeader(AUTHORIZE_UID);
		if (SystemUtils.isEmpty(uid)) {
			uid = request.getParameter(AUTHORIZE_UID);
		}
		if (SystemUtils.isEmpty(token) || SystemUtils.isEmpty(uid)) {
			accessDenied(httpRequest, httpResponse);
			return;
		}
		if (!checkToken(uid, token)) {
			accessDenied(httpRequest, httpResponse);
			return;
		}
		chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() {}
	
	private boolean needCheck(HttpServletRequest httpRequest) {
		String uri = httpRequest.getRequestURI();
		if (whiteList != null && whiteList.getUrls().contains(uri)) {
			return false;
		} else if (whitePrefix != null) {
			for (String prefix : whitePrefix.getUrls()) {
				if (uri.startsWith(prefix)) {
					return false;
				}
			}
			return true;
		} else {
			return true;
		}
	}
	
	private void accessDenied(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setContentType("application/json;charset=UTF-8");
		httpResponse.setStatus(401);
		String requestOrigin = httpRequest.getHeader(HttpHeaders.ORIGIN);
		String allowOrigin = corsConfiguration.checkOrigin(requestOrigin);
		httpResponse.addHeader("Access-Control-Allow-Credentials", "true");
		httpResponse.addHeader("Access-Control-Allow-Origin", allowOrigin);
		try (PrintWriter out = httpResponse.getWriter()) {
			out.append("{'code': 401, 'message': 'Access Denied'}");
		}
	}
	
	private boolean checkToken(String uid, String token) {
		if (!NumberUtil.isLong(uid)) {
			return false;
		}
		UserToken userToken = TokenFactory.get().getToken(Long.valueOf(uid), true);
		if (userToken == null) {
			return false;
		}
		return token.equals(userToken.getToken());
	}
	
}
