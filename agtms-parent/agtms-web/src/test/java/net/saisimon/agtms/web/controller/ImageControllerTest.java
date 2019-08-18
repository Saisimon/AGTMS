package net.saisimon.agtms.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.dto.SimpleResult;
import net.saisimon.agtms.core.util.SystemUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.main.bannerMode=OFF", "logging.level.root=ERROR"})
@AutoConfigureMockMvc
public class ImageControllerTest extends AbstractControllerTest {

	/* ImageController Start */
	@Test
	public void testUpload() throws Exception {
		UserToken testToken = login("test", "test");
		ClassPathResource classPathResource = new ClassPathResource("test.png");
		MockMultipartFile file = new MockMultipartFile("image", classPathResource.getInputStream());
		sendMultipart("/image/upload", null, testToken, file);
	}
	
	@Test
	public void testRes() throws Exception {
		UserToken testToken = login("test", "test");
		ClassPathResource classPathResource = new ClassPathResource("test.png");
		MockMultipartFile file = new MockMultipartFile("image", classPathResource.getInputStream());
		String json = sendMultipart("/image/upload", null, testToken, file);
		@SuppressWarnings("unchecked")
		SimpleResult<String> simpleResult = SystemUtils.fromJson(json, SimpleResult.class, String.class);
		String uri = simpleResult.getData();
		returnBinary(uri, null, null);
	}
	/* ImageController End */
	
}
