package net.saisimon.agtms.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import net.saisimon.agtms.zuul.filter.AccessFilter;

@SpringCloudApplication
@EnableZuulProxy
@Configuration
@EnableFeignClients("net.saisimon.agtms.api")
@ComponentScan(basePackages="net.saisimon.agtms")
public class ZuulApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ZuulApplication.class, args);
	}
	
	@Bean
	public AccessFilter accessFilter() {
		return new AccessFilter();
	}
	
	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true); // 允许cookies跨域
		config.addAllowedOrigin("*");// #允许向该服务器提交请求的URI，*表示全部允许，在SpringMVC中，如果设成*，会自动转成当前请求头中的Origin
		config.addAllowedHeader("*");// #允许访问的头信息,*表示全部
		config.addAllowedMethod("*");// 允许提交请求的方法，*表示全部允许
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
