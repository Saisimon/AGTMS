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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.dto.PageResult;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.FileTypes;
import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.OperationServiceFactory;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleServiceFactory;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.factory.TaskServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.factory.UserRoleServiceFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.config.runner.InitRunner;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.ExportParam;
import net.saisimon.agtms.web.dto.req.NavigationParam;
import net.saisimon.agtms.web.dto.req.SelectionParam;
import net.saisimon.agtms.web.dto.req.SelectionParam.SelectionOptionParam;
import net.saisimon.agtms.web.dto.req.SelectionParam.SelectionTemplateParam;
import net.saisimon.agtms.web.dto.req.UserParam;
import net.saisimon.agtms.web.dto.resp.TaskInfo;
import net.saisimon.agtms.web.dto.resp.TemplateInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EditControllerTest.TestMain.class, properties = {"spring.main.bannerMode=OFF", "logging.level.root=ERROR"})
@AutoConfigureMockMvc
public class EditControllerTest extends AbstractControllerTest {
	
	@Autowired
	private AgtmsProperties agtmsProperties;
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
	
	/* UserEditController Start */
	@Test
	public void testUserEditGrid() throws Exception {
		UserToken testToken = login("editor", "editor");
		Long testUserId = testToken.getUserId();
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		sendPost("/user/edit/grid", null, adminToken);
		
		Map<String, String> param = new HashMap<>();
		param.put("id", "10");
		sendPost("/user/edit/grid", param, adminToken, ErrorMessage.User.ACCOUNT_NOT_EXIST.getCode());
		
		param = new HashMap<>();
		param.put("id", testUserId.toString());
		sendPost("/user/edit/grid", param, adminToken);
	}
	
	@Test
	public void testUserEditSave() throws Exception {
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		UserParam body = new UserParam();
		sendPost("/user/edit/save", body, adminToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		body = new UserParam();
		body.setLoginName(buildString(33));
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setPassword("123456");
		sendPost("/user/edit/save", body, adminToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		body = new UserParam();
		body.setPassword(buildString(17));
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		sendPost("/user/edit/save", body, adminToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		body = new UserParam();
		body.setNickname(buildString(33));
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setPassword("123456");
		sendPost("/user/edit/save", body, adminToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		body = new UserParam();
		body.setCellphone(buildString(33));
		body.setLoginName("saisimon");
		body.setEmail("saisimon@saisimon.net");
		body.setPassword("123456");
		sendPost("/user/edit/save", body, adminToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		body = new UserParam();
		body.setEmail(buildString(257));
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setPassword("123456");
		sendPost("/user/edit/save", body, adminToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		body = new UserParam();
		body.setAvatar(buildString(65));
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setPassword("123456");
		sendPost("/user/edit/save", body, adminToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		body = new UserParam();
		body.setRemark(buildString(513));
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setPassword("123456");
		sendPost("/user/edit/save", body, adminToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		body = new UserParam();
		body.setLoginName("editor");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setPassword("123456");
		sendPost("/user/edit/save", body, adminToken, ErrorMessage.User.ACCOUNT_ALREADY_EXISTS.getCode());
		
		body = new UserParam();
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setNickname("Saisimon");
		body.setPassword("123456");
		body.setRemark("-");
		sendPost("/user/edit/save", body, adminToken);
		
		body = new UserParam();
		body.setId(10L);
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setNickname("Saisimon");
		body.setPassword("123456");
		body.setRemark("-");
		sendPost("/user/edit/save", body, adminToken, ErrorMessage.User.ACCOUNT_NOT_EXIST.getCode());
		
		UserToken saisimonToken = login("saisimon", "123456");
		body = new UserParam();
		body.setId(saisimonToken.getUserId());
		body.setLoginName("editor");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setNickname("Saisimon");
		body.setPassword("123456");
		sendPost("/user/edit/save", body, adminToken, ErrorMessage.User.ACCOUNT_ALREADY_EXISTS.getCode());
		
		body = new UserParam();
		body.setId(saisimonToken.getUserId());
		body.setLoginName("saisimon");
		body.setCellphone("13888888888");
		body.setEmail("saisimon@saisimon.net");
		body.setNickname("Sai");
		body.setPassword("123456");
		body.setRemark("SAISIMON");
		body.setAvatar("/");
		sendPost("/user/edit/save", body, adminToken);
	}
	/* UserEditController End */
	
	/* NavigationEditController Start */
	@Test
	public void testNavigationEditSave() throws Exception {
		UserToken testToken = login("editor", "editor");
		NavigationParam body = new NavigationParam();
		sendPost("/navigation/edit/save", body, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		body = new NavigationParam();
		body.setIcon(buildString(65));
		body.setName("Test");
		body.setPath("");
		sendPost("/navigation/edit/save", body, testToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		body = new NavigationParam();
		body.setIcon("list");
		body.setName(buildString(33));
		body.setPath("");
		sendPost("/navigation/edit/save", body, testToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		body = new NavigationParam();
		body.setIcon("list");
		body.setName("Test");
		body.setPath("/100");
		sendPost("/navigation/edit/save", body, testToken, ErrorMessage.Common.PARAM_ERROR.getCode());
		
		body = new NavigationParam();
		body.setIcon("list");
		body.setName("Test");
		body.setPath("");
		sendPost("/navigation/edit/save", body, testToken);
		Long newId = ResourceServiceFactory.get().findOne(FilterRequest.build(), FilterSort.build(Constant.ID, Direction.DESC), Constant.ID).get().getId();
		
		sendPost("/navigation/edit/save", body, testToken, ErrorMessage.Navigation.NAVIGATION_ALREADY_EXISTS.getCode());
		
		for (int i = 0; i < 10; i++) {
			body = new NavigationParam();
			body.setIcon("list");
			body.setName("Test-" + i);
			body.setPath("");
			sendPost("/navigation/edit/save", body, testToken);
		}
		
		body = new NavigationParam();
		body.setId(1000L);
		body.setIcon("list");
		body.setName("Test");
		body.setPath("");
		sendPost("/navigation/edit/save", body, testToken, ErrorMessage.Navigation.NAVIGATION_NOT_EXIST.getCode());
		
		body = new NavigationParam();
		body.setId(newId);
		body.setIcon("list");
		body.setName("Test");
		body.setPath("/" + newId);
		sendPost("/navigation/edit/save", body, testToken, ErrorMessage.Common.PARAM_ERROR.getCode());
		
		body = new NavigationParam();
		body.setId(newId);
		body.setIcon("list");
		body.setName("Test");
		body.setPath("/1000");
		sendPost("/navigation/edit/save", body, testToken, ErrorMessage.Common.PARAM_ERROR.getCode());
		
		body = new NavigationParam();
		body.setId(newId + 1);
		body.setIcon("cogs");
		body.setName("Test");
		body.setPath("/" + newId);
		sendPost("/navigation/edit/save", body, testToken, ErrorMessage.Navigation.NAVIGATION_ALREADY_EXISTS.getCode());
		
		body = new NavigationParam();
		body.setId(newId + 1);
		body.setIcon("cogs");
		body.setName("Test-0");
		body.setPath("/" + newId);
		sendPost("/navigation/edit/save", body, testToken);
		
		body = new NavigationParam();
		body.setId(newId + 2);
		body.setIcon("cogs");
		body.setName("Test-1");
		body.setPath("/" + newId + "/" + (newId + 1));
		sendPost("/navigation/edit/save", body, testToken);
		
		body = new NavigationParam();
		body.setId(newId + 3);
		body.setIcon("cogs");
		body.setName("Test-2");
		body.setPath("/" + newId + "/" + (newId + 1) + "/" + (newId + 2));
		sendPost("/navigation/edit/save", body, testToken, ErrorMessage.Navigation.NAVIGATION_MAX_DEPTH_LIMIT.getCode());
		
		body = new NavigationParam();
		body.setIcon("cogs");
		body.setName("Test-SUB");
		body.setPath("/" + newId + "/" + (newId + 1) + "/" + (newId + 2));
		sendPost("/navigation/edit/save", body, testToken, ErrorMessage.Navigation.NAVIGATION_MAX_DEPTH_LIMIT.getCode());
		
		body = new NavigationParam();
		body.setId(newId + 1);
		body.setIcon("cogs");
		body.setName("Test-0");
		body.setPath("/" + (newId + 3));
		sendPost("/navigation/edit/save", body, testToken);
		
		sendPost("/navigation/edit/grid", null, testToken);
		
		Map<String, String> param = new HashMap<>();
		param.put("id", "1000");
		sendPost("/navigation/edit/grid", param, testToken, ErrorMessage.Navigation.NAVIGATION_NOT_EXIST.getCode());
		
		param = new HashMap<>();
		param.put("id", (newId + 1) + "");
		sendPost("/navigation/edit/grid", param, testToken);
		
		Map<String, String> paramRequest = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> bodyRequest = new HashMap<>();
		sendPost("/navigation/main/list", paramRequest, bodyRequest, testToken);
		
		param = new HashMap<>();
		param.put("id", "-10");
		sendPost("/navigation/main/remove", param, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		param = new HashMap<>();
		param.put("id", "1000");
		sendPost("/navigation/main/remove", param, testToken, ErrorMessage.Navigation.NAVIGATION_NOT_EXIST.getCode());
		
		param = new HashMap<>();
		param.put("id", (newId + 1) + "");
		sendPost("/navigation/main/remove", param, testToken);
		
		bodyRequest = new HashMap<>();
		sendPost("/navigation/main/batch/save", null, bodyRequest, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		bodyRequest = new HashMap<>();
		bodyRequest.put("ids", Arrays.asList((newId + 1) + "", (newId + 2) + "", (newId + 3) + "", "1000"));
		bodyRequest.put("icon", "users");
		sendPost("/navigation/main/batch/save", null, bodyRequest, testToken);
		
		List<Long> ids = new ArrayList<>();
		sendPost("/navigation/main/batch/remove", ids, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		ids = new ArrayList<>();
		ids.add(newId + 1);
		ids.add(newId + 2);
		ids.add(newId + 3);
		ids.add(1000L);
		sendPost("/navigation/main/batch/remove", ids, testToken);
	}
	/* NavigationEditController End */
	
	/* TemplateEditController Start */
	@Test
	public void testTemplateEditSave() throws Exception {
		UserToken testToken = login("editor", "editor");
		sendPost("/template/edit/grid", null, testToken);
		
		Map<String, String> param = new HashMap<>();
		param.put("id", "1000");
		sendPost("/template/edit/grid", param, testToken, ErrorMessage.Template.TEMPLATE_NOT_EXIST.getCode());
		
		Template testTemplate = buildTestTemplate(testToken.getUserId(), null, 0, 0);
		sendPost("/template/edit/save", testTemplate, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 0, 0);
		sendPost("/template/edit/save", testTemplate, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 1, 11);
		sendPost("/template/edit/save", testTemplate, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 11, 1);
		sendPost("/template/edit/save", testTemplate, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		testTemplate = buildTestTemplate(testToken.getUserId(), buildString(33), 2, 1);
		sendPost("/template/edit/save", testTemplate, testToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 2, 1);
		testTemplate.setPath("/1000");
		sendPost("/template/edit/save", testTemplate, testToken, ErrorMessage.Common.PARAM_ERROR.getCode());
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 2, 1);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		Long newId = TemplateServiceFactory.get().findOne(FilterRequest.build(), FilterSort.build(Constant.ID, Direction.DESC), Constant.ID).get().getId();
		
		sendPost("/template/edit/save", testTemplate, testToken, ErrorMessage.Template.TEMPLATE_ALREADY_EXISTS.getCode());
		
		int size = 5;
		for (int i = 0; i < size; i++) {
			testTemplate = buildTestTemplate(testToken.getUserId(), "Test-" + i, 3, 2);
			sendPost("/template/edit/save", testTemplate, testToken);
		}
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 1, 2);
		testTemplate.setId(1000L);
		sendPost("/template/edit/save", testTemplate, testToken, ErrorMessage.Template.TEMPLATE_NOT_EXIST.getCode());
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test-1", 1, 2);
		testTemplate.setId(newId);
		sendPost("/template/edit/save", testTemplate, testToken, ErrorMessage.Template.TEMPLATE_ALREADY_EXISTS.getCode());
		
		testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 1, 2);
		testTemplate.setId(newId);
		sendPost("/template/edit/save", testTemplate, testToken);
		
		param = new HashMap<>();
		param.put("id", newId.toString());
		sendPost("/template/edit/grid", param, testToken);
		
		param = new HashMap<>();
		param.put("id", newId.toString());
		sendPost("/template/main/grid", param, testToken);
		
		param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/template/main/list", param, body, testToken);
		
		param = new HashMap<>();
		param.put("id", "1000");
		sendPost("/template/main/remove", param, testToken, ErrorMessage.Template.TEMPLATE_NOT_EXIST.getCode());
		
		param = new HashMap<>();
		param.put("id", newId.toString());
		sendPost("/template/main/remove", param, testToken);
		
		List<Long> ids = new ArrayList<>();
		sendPost("/template/main/batch/remove", ids, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		ids = new ArrayList<>();
		for (int i = 0; i <= size; i++) {
			ids.add(newId + i);
		}
		sendPost("/template/main/batch/remove", ids, testToken);
	}
	/* TemplateEditController End */
	
	/* SelectionEditController Start */
	@Test
	public void testSelectionEditSave() throws Exception {
		UserToken testToken = login("editor", "editor");
		sendPost("/selection/edit/grid", null, testToken);
		
		Map<String, String> param = new HashMap<>();
		param.put("id", "10");
		sendPost("/selection/edit/grid", param, testToken, ErrorMessage.Selection.SELECTION_NOT_EXIST.getCode());
		
		param = new HashMap<>();
		param.put("id", "10");
		sendPost("/selection/edit/template", param, testToken, ErrorMessage.Template.TEMPLATE_NOT_EXIST.getCode());
		
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
		sendPost("/selection/edit/save", selectionParam, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		selectionParam = new SelectionParam();
		selectionParam.setTitle(buildString(33));
		selectionParam.setType(0);
		sendPost("/selection/edit/save", selectionParam, testToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		selectionParam = new SelectionParam();
		selectionParam.setTitle("Test-Option");
		selectionParam.setType(10);
		sendPost("/selection/edit/save", selectionParam, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		selectionParam = new SelectionParam();
		selectionParam.setTitle("Test-Option");
		selectionParam.setType(0);
		sendPost("/selection/edit/save", selectionParam, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		selectionParam = new SelectionParam();
		selectionParam.setOptions(buildTestSelectionOptionParams(2));
		selectionParam.setTitle("Test-Option");
		selectionParam.setType(0);
		sendPost("/selection/edit/save", selectionParam, testToken);
		
		Map<String, String> valueTextMap = SelectionUtils.getSelectionValueTextMap("1", new HashSet<>(Arrays.asList("1")), Arrays.asList(testToken.getUserId()));
		Assert.assertEquals("Text-1", valueTextMap.get("1"));
		Map<String, String> textValueMap = SelectionUtils.getSelectionTextValueMap("1", new HashSet<>(Arrays.asList("Text-1")), Arrays.asList(testToken.getUserId()));
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
		sendPost("/selection/edit/save", selectionParam, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		selectionParam = new SelectionParam();
		SelectionTemplateParam selectionTemplateParam = new SelectionTemplateParam();
		selectionTemplateParam.setId(10L);
		selectionParam.setTemplate(selectionTemplateParam);
		selectionParam.setTitle("Test-Template");
		selectionParam.setType(1);
		sendPost("/selection/edit/save", selectionParam, testToken, ErrorMessage.Template.TEMPLATE_NOT_EXIST.getCode());
		
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
			
			param = new HashMap<>();
			param.put("id", templateInfo.getId());
			sendPost("/template/main/remove", param, testToken);
		}
		
		param = new HashMap<>();
		param.put("id", "100");
		sendPost("/selection/main/remove", param, testToken, ErrorMessage.Selection.SELECTION_NOT_EXIST.getCode());
		
		param = new HashMap<>();
		param.put("id", "1");
		sendPost("/selection/main/remove", param, testToken);
		
		List<Long> ids = new ArrayList<>();
		sendPost("/selection/main/batch/remove", ids, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
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
		UserToken testToken = login("editor", "editor");
		Template testTemplate = buildTestTemplate(testToken.getUserId(), "Test", 2, 7);
		sendPost("/template/edit/save", testTemplate, testToken);
		Long templateId = TemplateServiceFactory.get().findOne(FilterRequest.build(), FilterSort.build(Constant.ID, Direction.DESC), Constant.ID).get().getId();
		
		String editGridUri = "/management/edit/" + templateId + "/grid";
		String editSaveUri = "/management/edit/" + templateId + "/save";
		String mainGridUri = "/management/main/" + templateId + "/grid";
		String mainRemoveUri = "/management/main/" + templateId + "/remove";
		String mainBatchGridUri = "/management/main/" + templateId + "/batch/grid";
		String mainBatchRemoveUri = "/management/main/" + templateId + "/batch/remove";
		String mainBatchSaveUri = "/management/main/" + templateId + "/batch/save";
		String mainBatchExportUri = "/management/main/" + templateId + "/batch/export";
		String mainBatchImportUri = "/management/main/" + templateId + "/batch/import";
		String mainListUri = "/management/main/" + templateId + "/list";
		sendPost(editGridUri, null, testToken);
		
		sendPost(mainGridUri, null, testToken);
		
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost(mainListUri, param, body, testToken);
		
		body = new HashMap<>();
		body.put("column0field0", buildString(513));
		sendPost(editSaveUri, body, testToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
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
		sendPost(editGridUri, param, testToken, ErrorMessage.Domain.DOMAIN_NOT_EXIST.getCode());
		
		param = new HashMap<>();
		param.put("id", "1");
		sendPost(editGridUri, param, testToken);
		
		sendPost(mainBatchGridUri, null, testToken);
		
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
		sendPost(editSaveUri, body, testToken, ErrorMessage.Domain.DOMAIN_NOT_EXIST.getCode());
		
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
		sendPost(mainBatchSaveUri, null, body, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		body = new HashMap<>();
		body.put("ids", Arrays.asList("2", "3", "4", "100"));
		body.put("column0field0", buildString(513));
		sendPost(mainBatchSaveUri, null, body, testToken, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		body = new HashMap<>();
		body.put("ids", Arrays.asList("2", "3", "4", "100"));
		body.put("column0field0", "column0field0-test");
		body.put("column0field3", new Date());
		sendPost(mainBatchSaveUri, null, body, testToken);
		
		ExportParam exportParam = new ExportParam();
		sendPost(mainBatchExportUri, exportParam, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
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
		sendPost("/task/main/cancel", param, testToken, ErrorMessage.Task.TASK_CANCEL_FAILED.getCode());
		
		param = new HashMap<>();
		param.put("taskId", "100");
		sendPost("/api/cancel/task", param, testToken, -1);
		
		param = new HashMap<>();
		param.put("taskId", xlsExportTask.getId());
		sendPost("/api/cancel/task", param, testToken, -1);
		
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
		param.put("id", "-1");
		sendPost("/task/main/remove", param, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		param = new HashMap<>();
		param.put("id", xlsExportTask.getId());
		sendPost("/task/main/remove", param, testToken);
		
		List<Long> ids = new ArrayList<>();
		sendPost("/task/main/batch/remove", ids, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		ids = new ArrayList<>();
		ids.add(1L);
		ids.add(2L);
		ids.add(3L);
		sendPost("/task/main/batch/remove", ids, testToken);
		
		param = new HashMap<>();
		param.put("id", "100");
		sendPost(mainRemoveUri, param, testToken, ErrorMessage.Domain.DOMAIN_NOT_EXIST.getCode());
		
		param = new HashMap<>();
		param.put("id", "1");
		sendPost(mainRemoveUri, param, testToken);
		
		ids = new ArrayList<>();
		sendPost(mainBatchRemoveUri, ids, testToken, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		ids = new ArrayList<>();
		ids.add(1L);
		ids.add(2L);
		ids.add(3L);
		sendPost(mainBatchRemoveUri, ids, testToken);
	}
	/* ManagementEditController Start */
	
	private Template buildTestTemplate(Long userId, String title, int columnSize, int fieldSize) {
		Template template = new Template();
		template.setTitle(title);
		template.setPath("");
		template.setOperatorId(userId);
		template.setFunctions(255);
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
					templateField.setFieldType(Classes.LONG.getKey());
					templateField.setViews(Views.TEXT.getKey());
					templateField.setDefaultValue("1");
				} else if (j == 2) {
					templateField.setFieldType(Classes.DOUBLE.getKey());
					templateField.setViews(Views.TEXT.getKey());
					templateField.setDefaultValue("1.1");
				} else if (j == 3) {
					templateField.setFieldType(Classes.DATE.getKey());
					templateField.setViews(Views.TEXT.getKey());
					templateField.setDefaultValue("2019-07-25");
				} else {
					templateField.setFieldType(Classes.STRING.getKey());
					if (j == 4) {
						templateField.setViews(Views.PASSWORD.getKey());
						templateField.setDefaultValue("123456789");
					} else if (j == 5) {
						templateField.setViews(Views.EMAIL.getKey());
						templateField.setDefaultValue("88888888@test.org");
					} else if (j == 6) {
						templateField.setViews(Views.PHONE.getKey());
						templateField.setDefaultValue("13888888888");
					} else {
						templateField.setViews(Views.TEXT.getKey());
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
	
	@SpringBootApplication
	@ComponentScan(basePackages="net.saisimon.agtms")
	public static class TestMain {}
	
}
