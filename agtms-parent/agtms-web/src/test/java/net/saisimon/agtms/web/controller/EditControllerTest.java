package net.saisimon.agtms.web.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.dto.PageResult;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.FileTypes;
import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.dto.req.ExportParam;
import net.saisimon.agtms.web.dto.req.NavigationParam;
import net.saisimon.agtms.web.dto.req.SelectionParam;
import net.saisimon.agtms.web.dto.req.SelectionParam.SelectionOptionParam;
import net.saisimon.agtms.web.dto.req.SelectionParam.SelectionTemplateParam;
import net.saisimon.agtms.web.dto.req.UserParam;
import net.saisimon.agtms.web.dto.resp.TaskInfo;
import net.saisimon.agtms.web.dto.resp.TemplateInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.main.bannerMode=OFF", "logging.level.root=ERROR", "logging.level.net.saisimon=DEBUG"})
@AutoConfigureMockMvc
public class EditControllerTest extends AbstractControllerTest {
	
	@Autowired
	private AgtmsProperties agtmsProperties;
	
	/* UserEditController Start */
	@Test
	public void testUserEditGrid() throws Exception {
		UserToken testToken = login("test", "test");
		Long testUserId = testToken.getUserId();
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		sendPost("/user/edit/grid", null, adminToken);
		
		Map<String, String> param = new HashMap<>();
		param.put("id", "10");
		sendPost("/user/edit/grid", param, adminToken);
		
		param = new HashMap<>();
		param.put("id", testUserId.toString());
		sendPost("/user/edit/grid", param, adminToken);
	}
	
	@Test
	public void testUserEditSave() throws Exception {
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		UserParam body = new UserParam();
		sendPost("/user/edit/save", body, adminToken);
		
		body = new UserParam();
		body.setLoginName(buildString(33));
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setAdmin(0);
		sendPost("/user/edit/save", body, adminToken);
		
		body = new UserParam();
		body.setPassword(buildString(17));
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setAdmin(0);
		sendPost("/user/edit/save", body, adminToken);
		
		body = new UserParam();
		body.setNickname(buildString(33));
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setAdmin(0);
		sendPost("/user/edit/save", body, adminToken);
		
		body = new UserParam();
		body.setCellphone(buildString(33));
		body.setLoginName("saisimon");
		body.setEmail("saisimon@saisimon.net");
		body.setAdmin(0);
		sendPost("/user/edit/save", body, adminToken);
		
		body = new UserParam();
		body.setEmail(buildString(257));
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setAdmin(0);
		sendPost("/user/edit/save", body, adminToken);
		
		body = new UserParam();
		body.setAvatar(buildString(65));
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setAdmin(0);
		sendPost("/user/edit/save", body, adminToken);
		
		body = new UserParam();
		body.setRemark(buildString(513));
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setAdmin(0);
		sendPost("/user/edit/save", body, adminToken);
		
		body = new UserParam();
		body.setLoginName("test");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setAdmin(0);
		sendPost("/user/edit/save", body, adminToken);
		
		body = new UserParam();
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setNickname("Saisimon");
		body.setPassword("123456");
		body.setRemark("-");
		body.setAdmin(0);
		sendPost("/user/edit/save", body, adminToken);
		
		body = new UserParam();
		body.setId(10L);
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setNickname("Saisimon");
		body.setPassword("123456");
		body.setRemark("-");
		body.setAdmin(0);
		sendPost("/user/edit/save", body, adminToken);
		
		UserToken saisimonToken = login("saisimon", "123456");
		body = new UserParam();
		body.setId(saisimonToken.getUserId());
		body.setLoginName("test");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setNickname("Saisimon");
		body.setPassword("123456");
		body.setAdmin(0);
		sendPost("/user/edit/save", body, adminToken);
		
		body = new UserParam();
		body.setId(saisimonToken.getUserId());
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setNickname("Sai");
		body.setPassword("123456");
		body.setRemark("SAISIMON");
		body.setAvatar("/");
		body.setAdmin(0);
		sendPost("/user/edit/save", body, adminToken);
	}
	/* UserEditController End */
	
	/* NavigationEditController Start */
	@Test
	public void testNavigationEditSave() throws Exception {
		UserToken testToken = login("test", "test");
		NavigationParam body = new NavigationParam();
		sendPost("/navigation/edit/save", body, testToken);
		
		body = new NavigationParam();
		body.setIcon(buildString(65));
		body.setTitle("Test");
		body.setParentId(-1L);
		sendPost("/navigation/edit/save", body, testToken);
		
		body = new NavigationParam();
		body.setIcon("list");
		body.setTitle(buildString(33));
		body.setParentId(-1L);
		sendPost("/navigation/edit/save", body, testToken);
		
		body = new NavigationParam();
		body.setIcon("list");
		body.setTitle("Test");
		body.setPriority(0L);
		body.setParentId(1L);
		sendPost("/navigation/edit/save", body, testToken);
		
		body = new NavigationParam();
		body.setIcon("list");
		body.setTitle("Test");
		body.setPriority(0L);
		body.setParentId(-1L);
		sendPost("/navigation/edit/save", body, testToken);
		
		sendPost("/navigation/edit/save", body, testToken);
		
		for (int i = 0; i < 10; i++) {
			body = new NavigationParam();
			body.setIcon("list");
			body.setTitle("Test-" + i);
			body.setPriority((long)i);
			body.setParentId(-1L);
			sendPost("/navigation/edit/save", body, testToken);
		}
		
		body = new NavigationParam();
		body.setId(10L);
		body.setIcon("list");
		body.setTitle("Test");
		body.setPriority(0L);
		body.setParentId(-1L);
		sendPost("/navigation/edit/save", body, testToken);
		
		body = new NavigationParam();
		body.setId(1L);
		body.setIcon("list");
		body.setTitle("Test");
		body.setPriority(0L);
		body.setParentId(1L);
		sendPost("/navigation/edit/save", body, testToken);
		
		body = new NavigationParam();
		body.setId(1L);
		body.setIcon("cogs");
		body.setTitle("Test");
		body.setPriority(10L);
		body.setParentId(-1L);
		sendPost("/navigation/edit/save", body, testToken);
		
		sendPost("/navigation/edit/grid", null, testToken);
		
		Map<String, String> param = new HashMap<>();
		param.put("id", "10");
		sendPost("/navigation/edit/grid", param, testToken);
		
		param = new HashMap<>();
		param.put("id", "1");
		sendPost("/navigation/edit/grid", param, testToken);
		
		Map<String, String> paramRequest = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> bodyRequest = new HashMap<>();
		sendPost("/navigation/main/list", paramRequest, bodyRequest, testToken);
		
		param = new HashMap<>();
		param.put("id", "-10");
		sendPost("/navigation/main/remove", param, testToken);
		
		param = new HashMap<>();
		param.put("id", "100");
		sendPost("/navigation/main/remove", param, testToken);
		
		param = new HashMap<>();
		param.put("id", "1");
		sendPost("/navigation/main/remove", param, testToken);
		
		bodyRequest = new HashMap<>();
		sendPost("/navigation/main/batch/save", null, bodyRequest, testToken);
		
		bodyRequest = new HashMap<>();
		bodyRequest.put("ids", Arrays.asList(1L, 2L, 3L, 4L, 100L));
		bodyRequest.put("icon", "users");
		bodyRequest.put("priority", 8);
		sendPost("/navigation/main/batch/save", null, bodyRequest, testToken);
		
		List<Long> ids = new ArrayList<>();
		sendPost("/navigation/main/batch/remove", ids, testToken);
		
		ids = new ArrayList<>();
		ids.add(1L);
		ids.add(2L);
		ids.add(3L);
		sendPost("/navigation/main/batch/remove", ids, testToken);
	}
	/* NavigationEditController End */
	
	/* TemplateEditController Start */
	@Test
	public void testTemplateEditSave() throws Exception {
		UserToken testToken = login("test", "test");
		sendPost("/template/edit/grid", null, testToken);
		
		Map<String, String> param = new HashMap<>();
		param.put("id", "10");
		sendPost("/template/edit/grid", param, testToken);
		
		Template testTemplate = buildTestTemplate(testToken.getUserId(), null, 0, 0);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 0, 0);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 1, 11);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 11, 1);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		testTemplate = buildTestTemplate(testToken.getUserId(), buildString(33), 2, 1);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 2, 1);
		testTemplate.setNavigationId(100L);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 2, 1);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		sendPost("/template/edit/save", testTemplate, testToken);
		
		for (int i = 0; i < 5; i++) {
			testTemplate = buildTestTemplate(testToken.getUserId(), "Test-" + i, 3, 2);
			sendPost("/template/edit/save", testTemplate, testToken);
		}
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 1, 2);
		testTemplate.setId(10L);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test-1", 1, 2);
		testTemplate.setId(1L);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 1, 2);
		testTemplate.setId(1L);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		param = new HashMap<>();
		param.put("id", "1");
		sendPost("/template/edit/grid", param, testToken);
		
		param = new HashMap<>();
		param.put("id", "1");
		sendPost("/template/main/grid", param, testToken);
		
		param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/template/main/list", param, body, testToken);
		
		param = new HashMap<>();
		param.put("id", "100");
		sendPost("/template/main/remove", param, testToken);
		
		param = new HashMap<>();
		param.put("id", "1");
		sendPost("/template/main/remove", param, testToken);
		
		List<Long> ids = new ArrayList<>();
		sendPost("/template/main/batch/remove", ids, testToken);
		
		ids = new ArrayList<>();
		ids.add(1L);
		ids.add(2L);
		ids.add(3L);
		ids.add(4L);
		ids.add(5L);
		ids.add(6L);
		sendPost("/template/main/batch/remove", ids, testToken);
	}
	/* TemplateEditController End */
	
	/* SelectionEditController Start */
	@Test
	public void testSelectionEditSave() throws Exception {
		UserToken testToken = login("test", "test");
		sendPost("/selection/edit/grid", null, testToken);
		
		Map<String, String> param = new HashMap<>();
		param.put("id", "10");
		sendPost("/selection/edit/grid", param, testToken);
		
		param = new HashMap<>();
		param.put("id", "10");
		sendPost("/selection/edit/template", param, testToken);
		
		TemplateInfo templateInfo = null;
		Template testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 2, 1);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		String json = sendPost("/template/main/list", param, body, testToken);
		@SuppressWarnings("unchecked")
		PageResult<TemplateInfo> pageResult = SystemUtils.fromJson(json, PageResult.class, TemplateInfo.class);
		Iterable<TemplateInfo> it = pageResult.getRows();
		if (it != null) {
			Iterator<TemplateInfo> iterator = it.iterator();
			if (iterator.hasNext()) {
				templateInfo = iterator.next();
			}
		}
		if (templateInfo != null) {
			param = new HashMap<>();
			param.put("id", templateInfo.getId().toString());
			sendPost("/selection/edit/template", param, testToken);
		}
		
		SelectionParam selectionParam = new SelectionParam();
		sendPost("/selection/edit/save", selectionParam, testToken);
		
		selectionParam = new SelectionParam();
		selectionParam.setTitle(buildString(33));
		selectionParam.setType(0);
		sendPost("/selection/edit/save", selectionParam, testToken);
		
		selectionParam = new SelectionParam();
		selectionParam.setTitle("Test-Option");
		selectionParam.setType(10);
		sendPost("/selection/edit/save", selectionParam, testToken);
		
		selectionParam = new SelectionParam();
		selectionParam.setTitle("Test-Option");
		selectionParam.setType(0);
		sendPost("/selection/edit/save", selectionParam, testToken);
		
		selectionParam = new SelectionParam();
		selectionParam.setOptions(buildTestSelectionOptionParams(2));
		selectionParam.setTitle("Test-Option");
		selectionParam.setType(0);
		sendPost("/selection/edit/save", selectionParam, testToken);
		
		Map<String, String> valueTextMap = SelectionUtils.getSelectionValueTextMap("1", new HashSet<>(Arrays.asList("1")), testToken.getUserId());
		Assert.assertEquals("Text-1", valueTextMap.get("1"));
		Map<String, String> textValueMap = SelectionUtils.getSelectionTextValueMap("1", new HashSet<>(Arrays.asList("Text-1")), testToken.getUserId());
		Assert.assertEquals("1", textValueMap.get("Text-1"));
		
		param = new HashMap<>();
		param.put("sign", "1");
		sendPost("/selection/edit/search", param, testToken);
		
		param.put("id", "1");
		sendPost("/selection/edit/grid", param, testToken);
		
		selectionParam = new SelectionParam();
		selectionParam.setId(1L);
		selectionParam.setOptions(buildTestSelectionOptionParams(3));
		selectionParam.setTitle("Test-Option");
		selectionParam.setType(0);
		sendPost("/selection/edit/save", selectionParam, testToken);
		
		selectionParam = new SelectionParam();
		selectionParam.setTitle("Test-Template");
		selectionParam.setType(1);
		sendPost("/selection/edit/save", selectionParam, testToken);
		
		selectionParam = new SelectionParam();
		SelectionTemplateParam selectionTemplateParam = new SelectionTemplateParam();
		selectionTemplateParam.setId(10L);
		selectionParam.setTemplate(selectionTemplateParam);
		selectionParam.setTitle("Test-Template");
		selectionParam.setType(1);
		sendPost("/selection/edit/save", selectionParam, testToken);
		
		if (templateInfo != null) {
			selectionParam = new SelectionParam();
			selectionTemplateParam = new SelectionTemplateParam();
			selectionTemplateParam.setId(Long.valueOf(templateInfo.getId()));
			selectionTemplateParam.setValue("id");
			selectionTemplateParam.setText("column0field0");
			selectionParam.setTemplate(selectionTemplateParam);
			selectionParam.setTitle("Test-Template");
			selectionParam.setType(1);
			sendPost("/selection/edit/save", selectionParam, testToken);
			
			param = new HashMap<>();
			param.put("sign", "10");
			sendPost("/selection/edit/search", param, testToken);
			
			param = new HashMap<>();
			param.put("sign", "2");
			sendPost("/selection/edit/search", param, testToken);
			
			param.put("id", "2");
			sendPost("/selection/edit/grid", param, testToken);
		}
		
		param = new HashMap<>();
		param.put("id", "100");
		sendPost("/selection/main/remove", param, testToken);
		
		param = new HashMap<>();
		param.put("id", "1");
		sendPost("/selection/main/remove", param, testToken);
		
		List<Long> ids = new ArrayList<>();
		sendPost("/selection/main/batch/remove", ids, testToken);
		
		ids = new ArrayList<>();
		ids.add(1L);
		ids.add(2L);
		ids.add(3L);
		sendPost("/selection/main/batch/remove", ids, testToken);
	}
	/* SelectionEditController End */
	
	/* ManagementEditController Start */
	@Test
	@SuppressWarnings("unchecked")
	public void testManagementEditSave() throws Exception {
		UserToken testToken = login("test", "test");
		sendPost("/management/edit/1/grid", null, testToken);
		
		Map<String, String> param = new HashMap<>();
		param.put("id", "10");
		sendPost("/management/edit/1/grid", param, testToken);
		
		TemplateInfo templateInfo = null;
		Template testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 2, 7);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		String json = sendPost("/template/main/list", param, body, testToken);
		PageResult<TemplateInfo> templagePageResult = SystemUtils.fromJson(json, PageResult.class, TemplateInfo.class);
		Iterable<TemplateInfo> templateIterable = templagePageResult.getRows();
		if (templateIterable != null) {
			Iterator<TemplateInfo> templateIterator = templateIterable.iterator();
			if (templateIterator != null && templateIterator.hasNext()) {
				templateInfo = templateIterator.next();
			}
		}
		if (templateInfo != null) {
			String editGridUri = "/management/edit/" + templateInfo.getId() + "/grid";
			String editSaveUri = "/management/edit/" + templateInfo.getId() + "/save";
			String mainGridUri = "/management/main/" + templateInfo.getId() + "/grid";
			String mainRemoveUri = "/management/main/" + templateInfo.getId() + "/remove";
			String mainBatchGridUri = "/management/main/" + templateInfo.getId() + "/batch/grid";
			String mainBatchRemoveUri = "/management/main/" + templateInfo.getId() + "/batch/remove";
			String mainBatchSaveUri = "/management/main/" + templateInfo.getId() + "/batch/save";
			String mainBatchExportUri = "/management/main/" + templateInfo.getId() + "/batch/export";
			String mainBatchImportUri = "/management/main/" + templateInfo.getId() + "/batch/import";
			String mainListUri = "/management/main/" + templateInfo.getId() + "/list";
			sendPost(editGridUri, null, testToken);
			
			sendPost(mainGridUri, null, testToken);
			
			param = new HashMap<>();
			param.put("index", "0");
			param.put("size", "10");
			body = new HashMap<>();
			sendPost(mainListUri, param, body, testToken);
			
			body = new HashMap<>();
			body.put("column0field0", buildString(513));
			sendPost(editSaveUri, body, testToken);
			
			body = new HashMap<>();
			body.put("column0field0", "column0field0");
			body.put("column0field1", 99L);
			body.put("column0field2", 99.99D);
			body.put("column0field3", new Date());
			body.put("column1field0", "column1field0");
			body.put("column1field1", 99L);
			body.put("column1field2", 99.99D);
			body.put("column1field3", new Date());
			sendPost(editSaveUri, body, testToken);
			
			param = new HashMap<>();
			param.put("id", "10");
			sendPost(editGridUri, param, testToken);
			
			param = new HashMap<>();
			param.put("id", "1");
			sendPost(editGridUri, param, testToken);
			
			param = new HashMap<>();
			param.put("id", "10");
			sendPost(mainBatchGridUri, param, testToken);
			
			param = new HashMap<>();
			param.put("id", "1");
			sendPost(mainBatchGridUri, param, testToken);
			
			param = new HashMap<>();
			param.put("index", "0");
			param.put("size", "10");
			body = new HashMap<>();
			sendPost(mainListUri, param, body, testToken);
			
			body = new HashMap<>();
			body.put("id", 10L);
			body.put("column0field0", "column0field0");
			body.put("column0field1", 99L);
			body.put("column0field2", 99.99D);
			body.put("column0field3", new Date());
			body.put("column1field0", "column1field0");
			body.put("column1field1", 99L);
			body.put("column1field2", 99.99D);
			body.put("column1field3", new Date());
			sendPost(editSaveUri, body, testToken);
			
			body = new HashMap<>();
			body.put("id", 1L);
			body.put("column0field0", "column0field0");
			body.put("column0field1", 199L);
			body.put("column0field2", 199.99D);
			body.put("column0field3", new Date());
			body.put("column1field0", "column1field0");
			body.put("column1field1", 199L);
			body.put("column1field2", 199.99D);
			body.put("column1field3", new Date());
			sendPost(editSaveUri, body, testToken);
			
			for (int i = 0; i < 5; i++) {
				body = new HashMap<>();
				body.put("column0field0", "Test-" + i);
				body.put("column0field1", 199L);
				body.put("column0field2", 199.99D);
				body.put("column0field3", new Date());
				body.put("column1field0", "Test-" + i);
				body.put("column1field1", 199L);
				body.put("column1field2", 199.99D);
				body.put("column1field3", new Date());
				sendPost(editSaveUri, body, testToken);
			}
			
			body = new HashMap<>();
			sendPost(mainBatchSaveUri, null, body, testToken);
			
			body = new HashMap<>();
			body.put("ids", Arrays.asList(2L, 3L, 4L, 100L));
			body.put("column0field0", buildString(513));
			sendPost(mainBatchSaveUri, null, body, testToken);
			
			body = new HashMap<>();
			body.put("ids", Arrays.asList(2L, 3L, 4L, 100L));
			body.put("column0field0", "column0field0-test");
			body.put("column0field3", new Date());
			sendPost(mainBatchSaveUri, null, body, testToken);
			
			ExportParam exportParam = new ExportParam();
			sendPost(mainBatchExportUri, exportParam, testToken);
			
			List<String> exportFields = Arrays.asList("column0field0", "column0field1", "column0field2", "column0field3", "column1field0", "column1field1", "column1field2", "column1field3");
			exportParam = new ExportParam();
			exportParam.setExportFields(exportFields);
			exportParam.setExportFileType(FileTypes.XLS.getType());
			sendPost(mainBatchExportUri, exportParam, testToken);
			
			exportParam = new ExportParam();
			exportParam.setExportFields(exportFields);
			exportParam.setExportFileType(FileTypes.XLSX.getType());
			sendPost(mainBatchExportUri, exportParam, testToken);
			
			exportParam = new ExportParam();
			exportParam.setExportFields(exportFields);
			exportParam.setExportFileType(FileTypes.CSV.getType());
			sendPost(mainBatchExportUri, exportParam, testToken);
			
			exportParam = new ExportParam();
			exportParam.setExportFields(exportFields);
			exportParam.setExportFileType(FileTypes.PDF.getType());
			sendPost(mainBatchExportUri, exportParam, testToken);
			
			Map<String, Object> importParam = new HashMap<>();
			
			importParam.put("importFileType", FileTypes.XLS.getType());
			importParam.put("importFields", Arrays.asList("column0field0", "column1field0"));
			ClassPathResource classPathResource = new ClassPathResource("test.xls");
			MockMultipartFile file = new MockMultipartFile("importFiles", classPathResource.getInputStream());
			sendMultipart(mainBatchImportUri, importParam, testToken, file);
			
			importParam.put("importFileType", FileTypes.XLSX.getType());
			importParam.put("importFields", Arrays.asList("column0field0", "column1field0"));
			classPathResource = new ClassPathResource("test.xlsx");
			file = new MockMultipartFile("importFiles", classPathResource.getInputStream());
			sendMultipart(mainBatchImportUri, importParam, testToken, file);
			
			importParam.put("importFileType", FileTypes.CSV.getType());
			importParam.put("importFields", Arrays.asList("column0field0", "column1field0"));
			classPathResource = new ClassPathResource("test.csv");
			file = new MockMultipartFile("importFiles", classPathResource.getInputStream());
			sendMultipart(mainBatchImportUri, importParam, testToken, file);
			
			Thread.sleep(1000);
			
			MockMultipartFile[] files = new MockMultipartFile[agtmsProperties.getImportFileMaxSize() + 1];
			for (int i = 0; i < agtmsProperties.getImportFileMaxSize() + 1; i++) {
				files[i] = new MockMultipartFile("importFiles", classPathResource.getInputStream());
			}
			sendMultipart(mainBatchImportUri, importParam, testToken, files);
			
			param = new HashMap<>();
			param.put("index", "0");
			param.put("size", "10");
			body = new HashMap<>();
			String result = sendPost("/task/main/list", param, body, testToken);
			PageResult<TaskInfo> taskPageResult = SystemUtils.fromJson(result, PageResult.class, TaskInfo.class);
			Iterator<TaskInfo> taskIterator = taskPageResult.getRows().iterator();
			TaskInfo xlsExportTask = taskIterator.next();
			TaskInfo xlsxExportTask = taskIterator.next();
			TaskInfo csvExportTask = taskIterator.next();
			TaskInfo pdfExportTask = taskIterator.next();
			TaskInfo xlsImportTask = taskIterator.next();
			TaskInfo xlsxImportTask = taskIterator.next();
			TaskInfo csvImportTask = taskIterator.next();
			
			param = new HashMap<>();
			param.put("id", xlsExportTask.getId());
			sendPost("/task/main/cancel", param, testToken);
			
			param = new HashMap<>();
			param.put("taskId", "100");
			sendPost("/api/cancel/task", param, testToken);
			
			param = new HashMap<>();
			param.put("taskId", xlsExportTask.getId());
			sendPost("/api/cancel/task", param, testToken);
			
			param = new HashMap<>();
			sendPost("/task/main/grid", param, testToken);
			
			param = new HashMap<>();
			param.put("id", "100");
			param.put("uuid", "");
			returnBinary("/task/main/download", param, null, status().isNotFound());
			
			if (getMessage(HandleStatuses.SUCCESS.getName()).equals(xlsExportTask.getHandleStatus())) {
				param = new HashMap<>();
				param.put("id", xlsExportTask.getId());
				param.put("uuid", xlsExportTask.getUuid());
				returnBinary("/task/main/download", param, null);
			}
			if (getMessage(HandleStatuses.SUCCESS.getName()).equals(xlsxExportTask.getHandleStatus())) {
				param = new HashMap<>();
				param.put("id", xlsxExportTask.getId());
				param.put("uuid", xlsxExportTask.getUuid());
				returnBinary("/task/main/download", param, null);
			}
			if (getMessage(HandleStatuses.SUCCESS.getName()).equals(csvExportTask.getHandleStatus())) {
				param = new HashMap<>();
				param.put("id", csvExportTask.getId());
				param.put("uuid", csvExportTask.getUuid());
				returnBinary("/task/main/download", param, null);
			}
			if (getMessage(HandleStatuses.SUCCESS.getName()).equals(pdfExportTask.getHandleStatus())) {
				param = new HashMap<>();
				param.put("id", pdfExportTask.getId());
				param.put("uuid", pdfExportTask.getUuid());
				returnBinary("/task/main/download", param, null);
			}
			if (getMessage(HandleStatuses.SUCCESS.getName()).equals(xlsImportTask.getHandleStatus())) {
				param = new HashMap<>();
				param.put("id", xlsImportTask.getId());
				param.put("uuid", xlsImportTask.getUuid());
				returnBinary("/task/main/download", param, null);
			}
			if (getMessage(HandleStatuses.SUCCESS.getName()).equals(xlsxImportTask.getHandleStatus())) {
				param = new HashMap<>();
				param.put("id", xlsxImportTask.getId());
				param.put("uuid", xlsxImportTask.getUuid());
				returnBinary("/task/main/download", param, null);
			}
			if (getMessage(HandleStatuses.SUCCESS.getName()).equals(csvImportTask.getHandleStatus())) {
				param = new HashMap<>();
				param.put("id", csvImportTask.getId());
				param.put("uuid", csvImportTask.getUuid());
				returnBinary("/task/main/download", param, null);
			}
			
			param = new HashMap<>();
			param.put("id", xlsExportTask.getId());
			sendPost("/task/main/remove", param, testToken);
			
			List<Long> ids = new ArrayList<>();
			sendPost("/task/main/batch/remove", ids, testToken);
			
			ids = new ArrayList<>();
			ids.add(1L);
			ids.add(2L);
			ids.add(3L);
			sendPost("/task/main/batch/remove", ids, testToken);
			
			param = new HashMap<>();
			param.put("id", "100");
			sendPost(mainRemoveUri, param, testToken);
			
			param = new HashMap<>();
			param.put("id", "1");
			sendPost(mainRemoveUri, param, testToken);
			
			ids = new ArrayList<>();
			sendPost(mainBatchRemoveUri, ids, testToken);
			
			ids = new ArrayList<>();
			ids.add(1L);
			ids.add(2L);
			ids.add(3L);
			sendPost(mainBatchRemoveUri, ids, testToken);
		}
	}
	/* ManagementEditController Start */
	
	private Template buildTestTemplate(Long userId, String title, int columnSize, int fieldSize) {
		Template template = new Template();
		template.setTitle(title);
		template.setOperatorId(userId);
		template.setFunctions(127);
		template.setColumnIndex(columnSize);
		template.setSource(GenerateServiceFactory.getSigns().get(0).getName());
		Set<TemplateColumn> templateColumns = new HashSet<>();
		for (int i = 0; i < columnSize; i++) {
			TemplateColumn templateColumn = new TemplateColumn();
			templateColumn.setColumnName("column" + i);
			templateColumn.setFieldIndex(fieldSize);
			templateColumn.setOrdered(i);
			templateColumn.setTitle("TestColumn-" + i);
			Set<TemplateField> templateFields = new HashSet<>();
			for (int j = 0; j < fieldSize; j++) {
				TemplateField templateField = new TemplateField();
				templateField.setFieldName("field" + j);
				templateField.setFieldTitle("TestField-" + j);
				if (j == 1) {
					templateField.setFieldType(Classes.LONG.getName());
					templateField.setViews(Views.TEXT.getView());
					templateField.setDefaultValue("1");
				} else if (j == 2) {
					templateField.setFieldType(Classes.DOUBLE.getName());
					templateField.setViews(Views.TEXT.getView());
					templateField.setDefaultValue("1.1");
				} else if (j == 3) {
					templateField.setFieldType(Classes.DATE.getName());
					templateField.setViews(Views.TEXT.getView());
					templateField.setDefaultValue("2019-07-25");
				} else {
					templateField.setFieldType(Classes.STRING.getName());
					if (j == 4) {
						templateField.setViews(Views.PASSWORD.getView());
						templateField.setDefaultValue("123456789");
					} else if (j == 5) {
						templateField.setViews(Views.EMAIL.getView());
						templateField.setDefaultValue("88888888@test.org");
					} else if (j == 6) {
						templateField.setViews(Views.PHONE.getView());
						templateField.setDefaultValue("13888888888");
					} else {
						templateField.setViews(Views.TEXT.getView());
						templateField.setDefaultValue("");
					}
				}
				templateField.setFilter(true);
				templateField.setHidden(false);
				templateField.setOrdered(j);
				templateField.setRequired(false);
				templateField.setSorted(false);
				templateField.setUniqued(false);
				templateFields.add(templateField);
			}
			templateColumn.setFields(templateFields);
			templateColumns.add(templateColumn);
		}
		template.setColumns(templateColumns);
		return template;
	}
	
	private List<SelectionOptionParam> buildTestSelectionOptionParams(int size) {
		List<SelectionOptionParam> params = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			SelectionOptionParam param = new SelectionOptionParam();
			param.setText("Text-" + i);
			param.setValue("" + i);
			params.add(param);
		}
		return params;
	}
}
