package net.saisimon.agtms.gateway;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@SpringCloudApplication
@Configuration
@EnableFeignClients("net.saisimon.agtms.api")
@ComponentScan(basePackages = "net.saisimon.agtms")
public class GatewayApplication {

	private static final List<String> ALLOW_METHODS = Arrays.asList(HttpMethod.OPTIONS.name(), HttpMethod.GET.name(), HttpMethod.POST.name());
	private static final String ALL = "*";
	private static final String MAX_AGE = "18000L";

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public WebFilter corsFilter() {
		return (ServerWebExchange ctx, WebFilterChain chain) -> {
			ServerHttpRequest request = ctx.getRequest();
			if (!CorsUtils.isCorsRequest(request)) {
				return chain.filter(ctx);
			}
			HttpHeaders requestHeaders = request.getHeaders();
			ServerHttpResponse response = ctx.getResponse();
			HttpHeaders headers = response.getHeaders();
			headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestHeaders.getOrigin());
			headers.addAll(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders.getAccessControlRequestHeaders());
			headers.addAll(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALLOW_METHODS);
			headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
			headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
			headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
			if (request.getMethod() == HttpMethod.OPTIONS) {
				response.setStatusCode(HttpStatus.OK);
				return Mono.empty();
			}
			return chain.filter(ctx);
		};
	}

}
