package net.saisimon.agtms.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.dto.PageResult;
import net.saisimon.agtms.core.enums.FileTypes;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.dto.req.ExportParam;
import net.saisimon.agtms.web.dto.req.NavigationParam;
import net.saisimon.agtms.web.dto.req.SelectionParam;
import net.saisimon.agtms.web.dto.req.SelectionParam.SelectionOptionParam;
import net.saisimon.agtms.web.dto.req.SelectionParam.SelectionTemplateParam;
import net.saisimon.agtms.web.dto.req.UserParam;
import net.saisimon.agtms.web.dto.resp.TemplateInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.main.banner-mode=OFF", "logging.level.net.saisimon=DEBUG", "eureka.client.enabled=false"})
@AutoConfigureMockMvc
public class EditControllerTest extends AbstractControllerTest {
	
	@Value("${extra.admin.username:admin}")
	private String username;
	@Value("${extra.admin.password:123456}")
	private String password;
	
	/* UserEditController Start */
	@Test
	public void testUserEditGrid() throws Exception {
		UserToken testToken = login("test", "test");
		Long testUserId = testToken.getUserId();
		UserToken adminToken = login(username, password);
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
		UserToken adminToken = login(username, password);
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
			for (TemplateInfo ti : it) {
				templateInfo = ti;
				break;
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
			selectionTemplateParam.setId(templateInfo.getId());
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
	public void testManagementEditSave() throws Exception {
		UserToken testToken = login("test", "test");
		sendPost("/management/edit/1/grid", null, testToken);
		
		Map<String, String> param = new HashMap<>();
		param.put("id", "10");
		sendPost("/management/edit/1/grid", param, testToken);
		
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
			for (TemplateInfo ti : it) {
				templateInfo = ti;
				break;
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
			body.put("column1field0", "column1field0");
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
			body.put("column0field0", "column0field0-1");
			body.put("column1field0", "column1field0-1");
			sendPost(editSaveUri, body, testToken);
			
			body = new HashMap<>();
			body.put("id", 1L);
			body.put("column0field0", "column0field0-1");
			body.put("column1field0", "column1field0-1");
			sendPost(editSaveUri, body, testToken);
			
			for (int i = 0; i < 5; i++) {
				body = new HashMap<>();
				body.put("column0field0", "Test-" + i);
				body.put("column1field0", "Test-" + i);
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
			sendPost(mainBatchSaveUri, null, body, testToken);
			
			ExportParam exportParam = new ExportParam();
			sendPost(mainBatchExportUri, exportParam, testToken);
			
			List<String> exportFields = Arrays.asList("column0field0", "column1field0");
			exportParam = new ExportParam();
			exportParam.setExportFields(exportFields);
			exportParam.setExportFileType(FileTypes.XLS.getType());
			sendPost(mainBatchExportUri, exportParam, testToken);
			
			param = new HashMap<>();
			param.put("id", "1");
			sendPost("/task/main/cancel", param, testToken);
			
			exportParam = new ExportParam();
			exportParam.setExportFields(exportFields);
			exportParam.setExportFileType(FileTypes.XLSX.getType());
			sendPost(mainBatchExportUri, exportParam, testToken);
			
			exportParam = new ExportParam();
			exportParam.setExportFields(exportFields);
			exportParam.setExportFileType(FileTypes.CSV.getType());
			sendPost(mainBatchExportUri, exportParam, testToken);
			
			param = new HashMap<>();
			param.put("index", "0");
			param.put("size", "10");
			body = new HashMap<>();
			sendPost("/task/main/list", param, body, testToken);
			
			param = new HashMap<>();
			param.put("id", "100");
			sendPost("/task/main/grid", param, testToken);
			
			param = new HashMap<>();
			param.put("id", "1");
			sendPost("/task/main/grid", param, testToken);
			
			param = new HashMap<>();
			param.put("id", "10");
			returnBinary("/task/main/download", HttpMethod.GET, param, null, testToken);
			
			param = new HashMap<>();
			param.put("id", "1");
			sendPost("/task/main/remove", param, testToken);
			
			List<Long> ids = new ArrayList<>();
			sendPost("/task/main/batch/remove", ids, testToken);
			
			ids = new ArrayList<>();
			ids.add(1L);
			ids.add(2L);
			ids.add(3L);
			sendPost("/task/main/batch/remove", ids, testToken);
			
			Map<String, Object> importParam = new HashMap<>();
			importParam.put("importFileType", FileTypes.CSV.getType());
			importParam.put("importFields", Arrays.asList("column0field0", "column1field0"));
			ClassPathResource classPathResource = new ClassPathResource("test.csv");
			MockMultipartFile file = new MockMultipartFile("importFile", classPathResource.getInputStream());
			sendMultipart(mainBatchImportUri, importParam, testToken, file);
			
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
		template.setSource("jpa");
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
				templateField.setFieldType("string");
				templateField.setFilter(true);
				templateField.setHidden(false);
				templateField.setOrdered(j);
				templateField.setRequired(false);
				templateField.setSorted(false);
				templateField.setUniqued(false);
				templateField.setViews("text");
				templateField.setDefaultValue("");
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
