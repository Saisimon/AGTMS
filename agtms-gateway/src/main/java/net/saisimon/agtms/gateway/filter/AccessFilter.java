package net.saisimon.agtms.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import net.saisimon.agtms.api.UserInterface;
import net.saisimon.agtms.gateway.config.WhiteList;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AccessFilter implements GlobalFilter, Ordered {
	
	private static final String AUTHORIZE_TOKEN = "X-TOKEN";
	private static final String AUTHORIZE_UID = "X-UID";
	
	@Autowired
	private WhiteList whiteList;
	@Autowired
	private UserInterface userInterface;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		HttpHeaders headers = request.getHeaders();
		MultiValueMap<String, String> params = request.getQueryParams();
		String uri = request.getPath().value();
		if (whiteList != null && whiteList.getUrls().contains(uri)) {
			return chain.filter(exchange);
		}
		ServerHttpResponse response = exchange.getResponse();
		String token = headers.getFirst(AUTHORIZE_TOKEN);
		if (StringUtils.isEmpty(token)) {
			token = params.getFirst(AUTHORIZE_TOKEN);
		}
		if (StringUtils.isEmpty(token)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return response.setComplete();
		}
		String uid = headers.getFirst(AUTHORIZE_UID);
		if (StringUtils.isEmpty(uid)) {
			uid = params.getFirst(AUTHORIZE_UID);
		}
		if (StringUtils.isEmpty(uid)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return response.setComplete();
		}
		if (!userInterface.checkToken(uid, token)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return response.setComplete();
		}
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return 0;
	}
	
}
