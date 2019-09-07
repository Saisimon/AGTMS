package net.saisimon.agtms.web.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.dto.SimpleResult;
import net.saisimon.agtms.core.factory.OperationServiceFactory;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleServiceFactory;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.factory.TaskServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.factory.UserRoleServiceFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.config.runner.InitRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImageControllerTest.TestMain.class, properties = {"spring.main.bannerMode=OFF", "logging.level.root=ERROR"})
@AutoConfigureMockMvc
public class ImageControllerTest extends AbstractControllerTest {
	
	@Autowired
	private InitRunner initRunner;
	
	@Before
	public void setUp() throws Exception {
		initRunner.run(new String[0]);
	}
	
	@After
	public void tearsDown() throws Exception {
		FilterRequest filter = FilterRequest.build();
		UserServiceFactory.get().delete(filter);
		UserRoleServiceFactory.get().delete(filter);
		TemplateServiceFactory.get().delete(filter);
		TaskServiceFactory.get().delete(filter);
		SelectionServiceFactory.get().delete(filter);
		RoleServiceFactory.get().delete(filter);
		RoleResourceServiceFactory.get().delete(filter);
		ResourceServiceFactory.get().delete(filter);
		OperationServiceFactory.get().delete(filter);
	}

	/* ImageController Start */
	@Test
	public void testUpload() throws Exception {
		UserToken testToken = login("editor", "editor");
		ClassPathResource classPathResource = new ClassPathResource("test.png");
		MockMultipartFile file = new MockMultipartFile("image", classPathResource.getInputStream());
		sendMultipart("/image/upload", null, testToken, file);
	}
	
	@Test
	public void testRes() throws Exception {
		UserToken testToken = login("editor", "editor");
		ClassPathResource classPathResource = new ClassPathResource("test.png");
		MockMultipartFile file = new MockMultipartFile("image", classPathResource.getInputStream());
		String json = sendMultipart("/image/upload", null, testToken, file);
		@SuppressWarnings("unchecked")
		SimpleResult<String> simpleResult = SystemUtils.fromJson(json, SimpleResult.class, String.class);
		String uri = simpleResult.getData();
		returnBinary(uri, null, null);
	}
	/* ImageController End */
	
	@SpringBootApplication
	@ComponentScan(basePackages="net.saisimon.agtms")
	public static class TestMain {}
	
}
