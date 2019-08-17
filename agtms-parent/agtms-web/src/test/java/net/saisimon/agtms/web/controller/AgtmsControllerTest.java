package net.saisimon.agtms.web.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.web.domain.SimpleEntity;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.main.bannerMode=OFF", "logging.level.root=ERROR", "logging.level.net.saisimon=DEBUG"})
@AutoConfigureMockMvc
public class AgtmsControllerTest extends AbstractControllerTest {
	
	@Test
	public void testTemplates() throws Exception {
		UserToken token = login("test", "test");
		sendPost("/agtms/templates", null, null, token);
	}
	
	@Test
	public void testTemplate() throws Exception {
		UserToken token = login("test", "test");
		sendPost("/agtms/entity/template", null, null, token);
		
		sendPost("/agtms/simpleEntity/template", null, null, token);
	}
	
	@Test
	public void testSelection() throws Exception {
		UserToken token = login("test", "test");
		Map<String, Object> body = new HashMap<>();
		sendPost("/agtms/GenderSelection/selection", body, token);
		
		body = new HashMap<>();
		body.put("values", Arrays.asList(0, 1));
		sendPost("/agtms/GenderSelection/selection", body, token);
		
		body = new HashMap<>();
		body.put("texts", Arrays.asList("女", "男"));
		sendPost("/agtms/GenderSelection/selection", body, token);
		
		body = new HashMap<>();
		body.put("text", "女");
		sendPost("/agtms/GenderSelection/selection", body, token);
	}
	
	@Test
	public void testCount() throws Exception {
		UserToken token = login("test", "test");
		Map<String, Object> body = new HashMap<>();
		sendPost("/agtms/simpleEntity/count", body, token);
	}
	
	@Test
	public void testFindListBySort() throws Exception {
		UserToken token = login("test", "test");
		Map<String, Object> body = new HashMap<>();
		sendPost("/agtms/simpleEntity/findList", body, token);
	}
	
	@Test
	public void testFindPage() throws Exception {
		UserToken token = login("test", "test");
		Map<String, Object> body = new HashMap<>();
		sendPost("/agtms/simpleEntity/findPage", body, token);
	}
	
	@Test
	public void testFindOne() throws Exception {
		UserToken token = login("test", "test");
		Map<String, Object> body = new HashMap<>();
		sendPost("/agtms/simpleEntity/findOne", body, token);
		
		SimpleEntity entity = new SimpleEntity();
		entity.setAge(11);
		entity.setBirthday(new Date());
		entity.setDesc("-");
		entity.setGender(1);
		entity.setHome("/");
		entity.setName("Test");
		body.put("body", entity);
		sendPost("/agtms/simpleEntity/saveOrUpdate", body, token);
		
		body = new HashMap<>();
		sendPost("/agtms/simpleEntity/findOne", body, token);
	}
	
	@Test
	public void testDelete() throws Exception {
		UserToken token = login("test", "test");
		Map<String, Object> body = new HashMap<>();
		sendPost("/agtms/simpleEntity/delete", body, token);
	}
	
	@Test
	public void testDeleteEntity() throws Exception {
		UserToken token = login("test", "test");
		Map<String, Object> param = new HashMap<>();
		SimpleEntity entity = new SimpleEntity();
		entity.setId(1);
		param.put("body", entity);
		sendPost("/agtms/simpleEntity/deleteEntity", param, token);
	}
	
	@Test
	public void testSaveOrUpdate() throws Exception {
		UserToken token = login("test", "test");
		Map<String, Object> body = new HashMap<>();
		SimpleEntity entity = new SimpleEntity();
		entity.setAge(11);
		entity.setBirthday(new Date());
		entity.setDesc("-");
		entity.setGender(1);
		entity.setHome("/");
		entity.setName("Test");
		body.put("body", entity);
		sendPost("/agtms/simpleEntity/saveOrUpdate", body, token);
		
		entity = new SimpleEntity();
		entity.setId(1);
		entity.setAge(18);
		entity.setGender(0);
		body.put("body", entity);
		sendPost("/agtms/simpleEntity/saveOrUpdate", body, token);
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
		UserToken token = login("test", "test");
		Map<String, Object> body = new HashMap<>();
		sendPost("/agtms/simpleEntity/batchUpdate", body, token);
		
		SimpleEntity entity = new SimpleEntity();
		entity.setAge(11);
		entity.setBirthday(new Date());
		entity.setDesc("-");
		entity.setGender(1);
		entity.setHome("/");
		entity.setName("Test");
		body.put("body", entity);
		sendPost("/agtms/simpleEntity/saveOrUpdate", body, token);
		sendPost("/agtms/simpleEntity/saveOrUpdate", body, token);
		
		body = new HashMap<>();
		sendPost("/agtms/simpleEntity/findList", body, token);
		
		body = new HashMap<>();
		Map<String, Object> updateMap = new HashMap<>();
		updateMap.put("name", "entity");
		body.put("update", updateMap);
		sendPost("/agtms/simpleEntity/batchUpdate", body, token);
	}
	
}
