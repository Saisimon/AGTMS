package net.saisimon.agtms.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import net.saisimon.agtms.core.constant.FileConstant;
import net.saisimon.agtms.web.config.handler.BatchExportHandler;
import net.saisimon.agtms.web.config.handler.BatchImportHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(batchExportHandler(), "/manage/batch/export")
				.addInterceptors(new HttpSessionHandshakeInterceptor());
		registry.addHandler(batchImportHandler(), "/manage/batch/import")
				.addInterceptors(new HttpSessionHandshakeInterceptor());
	}

	@Bean
	public WebSocketHandler batchExportHandler() {
		return new BatchExportHandler();
	}

	@Bean
	public WebSocketHandler batchImportHandler() {
		return new BatchImportHandler();
	}

	@Bean
	public ServletServerContainerFactoryBean createWebSocketContainer() {
		final ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
		container.setMaxTextMessageBufferSize(8192);
		container.setMaxBinaryMessageBufferSize(FileConstant.MAX_IMPORT_SIZE);
		return container;
	}

}
