package net.saisimon.agtms.mongodb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.SelectionOption;
import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.service.SelectionService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.mongodb.MongodbTestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongodbTestApplication.class, properties = {"logging.level.net.saisimon=DEBUG", "logging.level.org.springframework.data.mongodb=DEBUG"})
@DataMongoTest
public class ServiceTest {
	
	@Autowired
	private TemplateService templateService;
	@Autowired
	private UserService userService;
	@Autowired
	private SelectionService selectionService;
	
	@Before
	public void before() {
		templateService.delete(FilterRequest.build());
		userService.delete(FilterRequest.build());
		selectionService.delete(FilterRequest.build());
	}
	
	@Test
	public void baseServiceTest() {
		// count
		Long count = templateService.count(FilterRequest.build());
		Assert.assertEquals(0L, count.longValue());
		
		// exists
		Assert.assertFalse(templateService.exists(FilterRequest.build()));
		
		// findList
		List<Template> templates = templateService.findList(FilterRequest.build());
		Assert.assertNotNull(templates);
		Assert.assertEquals(0, templates.size());
		
		// findPage
		Page<Template> page = templateService.findPage(FilterRequest.build(), null);
		Assert.assertNotNull(page);
		Assert.assertNotNull(page.getContent());
		Assert.assertEquals(0, page.getContent().size());
		Assert.assertEquals(0, page.getTotalElements());
		
		// findOne
		Optional<Template> optional = templateService.findOne(FilterRequest.build());
		Assert.assertFalse(optional.isPresent());
		
		// findById
		optional = templateService.findById(1L);
		Assert.assertFalse(optional.isPresent());
		
		// saveOrUpdate
		String title = "xxx";
		int columnSize = 2;
		Template template = buildTestTemplate(title, columnSize);
		templateService.saveOrUpdate(template);
		Assert.assertNotNull(template.getId());
		
		// count
		count = templateService.count(FilterRequest.build());
		Assert.assertEquals(1L, count.longValue());
		
		// findById
		optional = templateService.findById(template.getId());
		Assert.assertTrue(optional.isPresent());
		template = optional.get();
		Assert.assertEquals(columnSize, template.getColumns().size());
		Assert.assertEquals(title, template.getTitle());
		
		// saveOrUpdate
		title = "aaa";
		int addColumnSize = 3;
		updateTemplate(template, title, addColumnSize);
		templateService.saveOrUpdate(template);
		
		// saveOrUpdate
		Template newTemplate = buildTestTemplate("bbb", 1);
		templateService.saveOrUpdate(newTemplate);
		
		// count
		count = templateService.count(FilterRequest.build());
		Assert.assertEquals(2L, count.longValue());
		
		// findList
		templates = templateService.findList(FilterRequest.build(), FilterSort.build("title", Direction.DESC));
		Assert.assertEquals("bbb", templates.get(0).getTitle());
		Assert.assertEquals("aaa", templates.get(1).getTitle());
		
		// update
		Map<String, Object> updateMap = new HashMap<>();
		updateMap.put("functions", 63);
		updateMap.put("title", "ccc");
		templateService.update(newTemplate.getId(), updateMap);
		
		// findById
		optional = templateService.findById(newTemplate.getId());
		Assert.assertTrue(optional.isPresent());
		newTemplate = optional.get();
		Assert.assertEquals(63, newTemplate.getFunctions().intValue());
		Assert.assertEquals("ccc", newTemplate.getTitle());
		
		// saveOrUpdate
		int addSize = 30;
		for (int i = 0; i < addSize; i++) {
			templateService.saveOrUpdate(buildTestTemplate(title + i, columnSize));
		}
		
		// findPage
		page = templateService.findPage(FilterRequest.build(), null);
		Assert.assertNotNull(page);
		Assert.assertNotNull(page.getContent());
		Assert.assertEquals(10, page.getContent().size());
		Assert.assertEquals(addSize + 2, page.getTotalElements());
		Assert.assertEquals(title, page.getContent().get(0).getTitle());
		
		// findPage
		FilterPageable pageable = new FilterPageable(1, 10, null);
		page = templateService.findPage(FilterRequest.build(), pageable);
		Assert.assertNotNull(page);
		Assert.assertNotNull(page.getContent());
		Assert.assertEquals(10, page.getContent().size());
		Assert.assertEquals(addSize + 2, page.getTotalElements());
		Assert.assertEquals(title + 8, page.getContent().get(0).getTitle());
		
		// delete
		templateService.delete(template);
		optional = templateService.findById(template.getId());
		Assert.assertFalse(optional.isPresent());
		
		// delete
		count = templateService.delete(FilterRequest.build());
		Assert.assertEquals(addSize + 1, count.intValue());
		// count
		count = templateService.count(FilterRequest.build());
		Assert.assertEquals(0L, count.longValue());
	}
	
	@Test
	public void templateServiceTest() {
		String title = "xxx";
		int columnSize = 2;
		Template template = buildTestTemplate(title, columnSize);
		template = templateService.saveOrUpdate(template);
		title = "aaa";
		int addColumnSize = 3;
		updateTemplate(template, title, addColumnSize);
		templateService.saveOrUpdate(template);
		Template newTemplate = buildTestTemplate("bbb", 1);
		templateService.saveOrUpdate(newTemplate);
		
		// exists
		Assert.assertTrue(templateService.exists(title, 1L));
		
		// getTemplates
		List<Template> templates = templateService.getTemplates(1L);
		Assert.assertNotNull(templates);
		Assert.assertEquals(2, templates.size());
		
		// createTable
		boolean result = templateService.createTable(null);
		Assert.assertFalse(result);
		
		// alterTable
		result = templateService.alterTable(null, null);
		Assert.assertFalse(result);
		
		// dropTable
		result = templateService.dropTable(null);
		Assert.assertFalse(result);
		
		// createTable
		result = templateService.createTable(template);
		Assert.assertTrue(result);
		
		// alterTable
		newTemplate = buildTestTemplate(title, 1);
		newTemplate.setId(template.getId());
		result = templateService.alterTable(newTemplate, template);
		Assert.assertTrue(result);
		
		// dropTable
		result = templateService.dropTable(template);
		Assert.assertTrue(result);
		
		templateService.delete(FilterRequest.build());
	}
	
	@Test
	public void userServiceTest() {
		// getUserByLoginNameOrEmail
		User user = userService.getUserByLoginNameOrEmail(null, null);
		Assert.assertNull(user);
		String username = "admin";
		String password = "123456";
		user = userService.getUserByLoginNameOrEmail(username, null);
		Assert.assertNull(user);
		user = userService.getUserByLoginNameOrEmail(null, username);
		Assert.assertNull(user);
		user = userService.getUserByLoginNameOrEmail(username, username);
		Assert.assertNull(user);
		
		// auth
		user = userService.auth(null, null);
		Assert.assertNull(user);
		user = userService.auth(username, password);
		Assert.assertNull(user);
		
		// register
		user = userService.register(null, null, null);
		Assert.assertNull(user);
		String email = "saisimon@test.com";
		user = userService.register(username, email, password);
		Assert.assertNotNull(user);
		Assert.assertNotNull(user.getId());
		Long uid = user.getId();
		user = userService.register(username, email + "a", password);
		Assert.assertNull(user);
		user = userService.register(username + "a", email, password);
		Assert.assertNull(user);
		
		// auth
		user = userService.auth(username + "aaa", password);
		Assert.assertNull(user);
		user = userService.auth(username, password);
		Assert.assertNotNull(user);
		Assert.assertEquals(uid, user.getId());
		user = userService.auth(email, password);
		Assert.assertNotNull(user);
		Assert.assertEquals(uid, user.getId());
	}
	
	@Test
	public void selectionServiceTest() {
		// exists
		Assert.assertNull(selectionService.exists(null, null));
		
		// saveOrUpdate
		Selection optionSelection = buildTestSelection("option", SelectTypes.OPTION);
		selectionService.saveOrUpdate(optionSelection);
		Assert.assertNotNull(optionSelection.getId());
		
		// saveSelectionOptions
		selectionService.saveSelectionOptions(null);
		List<SelectionOption> options = buildTestSelectionOptions(optionSelection.getId());
		selectionService.saveSelectionOptions(options);
		
		// getSelectionOptions
		options = selectionService.getSelectionOptions(optionSelection.getId());
		Assert.assertEquals(2, options.size());
		
		// saveOrUpdate
		Selection templateSelection = buildTestSelection("template", SelectTypes.TEMPLATE);
		selectionService.saveOrUpdate(templateSelection);
		Assert.assertNotNull(templateSelection.getId());
		
		// saveSelectionTemplate
		selectionService.saveSelectionTemplate(null);
		SelectionTemplate template = buildTestSelectionTemplate(templateSelection.getId());
		selectionService.saveSelectionTemplate(template);
		Assert.assertNotNull(template.getId());
		
		// getSelectionTemplate
		template = selectionService.getSelectionTemplate(templateSelection.getId());
		Assert.assertNotNull(template);
		Assert.assertEquals("column0field0", template.getTextFieldName());
		Assert.assertEquals("id", template.getValueFieldName());
	}
	
	@Test
	public void generateServiceTest() throws GenerateException {
		String title = "xxx";
		int columnSize = 2;
		Template template = buildTestTemplate(title, columnSize);
		templateService.saveOrUpdate(template);
		templateService.createTable(template);
		GenerateService generateService = GenerateServiceFactory.build(template);
		
		// checkExist
		Domain domain = generateService.newGenerate();
		domain.setField("column0field0", "aaa", String.class);
		domain.setField("column1field0", "bbb", String.class);
		Assert.assertFalse(generateService.checkExist(domain, 1L));
		
		// saveDomain
		generateService.saveDomain(domain, 1L);
		Assert.assertNotNull(domain.getField(Constant.ID));
		Assert.assertNotNull(domain.getField(Constant.CREATETIME));
		Assert.assertNotNull(domain.getField(Constant.UPDATETIME));
		Assert.assertNotNull(domain.getField(Constant.OPERATORID));
		Assert.assertEquals("aaa", domain.getField("column0field0"));
		Assert.assertEquals("bbb", domain.getField("column1field0"));
		
		// updateDomain
		Domain newDomain = generateService.newGenerate();
		newDomain.setField("column0field0", "ccc", String.class);
		newDomain.setField("column1field0", "ddd", String.class);
		generateService.updateDomain(newDomain, domain, 1L);
		Assert.assertNotNull(newDomain.getField(Constant.ID));
		Assert.assertEquals(domain.getField(Constant.ID), newDomain.getField(Constant.ID));
		Assert.assertEquals(domain.getField(Constant.CREATETIME), newDomain.getField(Constant.CREATETIME));
		Assert.assertNotEquals(domain.getField(Constant.UPDATETIME), newDomain.getField(Constant.UPDATETIME));
		Assert.assertEquals("ccc", newDomain.getField("column0field0"));
		Assert.assertEquals("ddd", newDomain.getField("column1field0"));
		
		// count
		Assert.assertEquals(1L, generateService.count(FilterRequest.build()).longValue());
		
		// findList
		List<Domain> domains = generateService.findList(FilterRequest.build(), (FilterSort) null);
		Assert.assertNotNull(domains);
		Assert.assertEquals(1, domains.size());
		
		// findPage
		Page<Domain> page = generateService.findPage(FilterRequest.build(), null);
		Assert.assertNotNull(page);
		Assert.assertNotNull(page.getContent());
		Assert.assertEquals(1, page.getContent().size());
		Assert.assertEquals(1, page.getTotalElements());
		Assert.assertEquals("ccc", page.getContent().get(0).getField("column0field0"));
		
		// findOne
		FilterRequest filter = FilterRequest.build().and("column0field0", "ccc");
		Optional<Domain> optional = generateService.findOne(filter, null);
		Assert.assertTrue(optional.isPresent());
		Assert.assertEquals("ccc", optional.get().getField("column0field0"));
		
		// findById
		domain = generateService.findById(1L, 2L);
		Assert.assertNull(domain);
		domain = generateService.findById(1L, 1L);
		Assert.assertNotNull(domain);
		Assert.assertEquals("ccc", domain.getField("column0field0"));
		Assert.assertEquals("ddd", domain.getField("column1field0"));
		
		// updateDomain
		Map<String, Object> updateMap = new HashMap<>();
		updateMap.put("column1field0", "eee");
		generateService.updateDomain(1L, updateMap);
		domain = generateService.findById(1L, 1L);
		Assert.assertNotNull(domain);
		Assert.assertEquals("eee", domain.getField("column1field0"));
		
		// delete
		generateService.delete(domain);
		Assert.assertEquals(0L, generateService.count(FilterRequest.build()).longValue());
		
		templateService.delete(template);
		templateService.dropTable(template);
	}
	
	private SelectionTemplate buildTestSelectionTemplate(Long selectionId) {
		SelectionTemplate template = new SelectionTemplate();
		template.setSelectionId(selectionId);
		template.setTemplateId(1L);
		template.setTextFieldName("column0field0");
		template.setValueFieldName("id");
		return template;
	}
	
	private List<SelectionOption> buildTestSelectionOptions(Long selectionId) {
		List<SelectionOption> options = new ArrayList<>();
		SelectionOption womanOption = new SelectionOption();
		womanOption.setSelectionId(selectionId);
		womanOption.setValue("0");
		womanOption.setText("女");
		options.add(womanOption);
		SelectionOption manOption = new SelectionOption();
		manOption.setSelectionId(selectionId);
		manOption.setValue("1");
		manOption.setText("男");
		options.add(manOption);
		return options;
	}
	
	private Selection buildTestSelection(String title, SelectTypes type) {
		Date time = new Date();
		Selection selection = new Selection();
		selection.setCreateTime(time);
		selection.setOperatorId(1L);
		selection.setTitle(title);
		selection.setType(type.getType());
		selection.setUpdateTime(time);
		return selection;
	}
	
	private Template buildTestTemplate(String title, int columnSize) {
		Date time = new Date();
		Template template = new Template();
		template.setCreateTime(time);
		template.setFunctions(127);
		template.setKey("test");
		template.setNavigationId(-1L);
		template.setOperatorId(1L);
		template.setSource("mongodb");
		template.setTitle(title);
		template.setUpdateTime(time);
		for (int i = 0; i < columnSize; i++) {
			TemplateColumn column = new TemplateColumn();
			column.setColumnName("column" + i);
			column.setOrdered(i);
			column.setTitle("列" + i);
			for (int j = 0; j < i + 1; j++) {
				TemplateField field = new TemplateField();
				field.setDefaultValue("");
				field.setFieldName("field" + j);
				field.setFieldTitle("属性" + j);
				field.setFieldType(Classes.STRING.getName());
				field.setFilter(Boolean.FALSE);
				field.setHidden(Boolean.FALSE);
				field.setOrdered(j);
				field.setRequired(Boolean.FALSE);
				field.setSorted(Boolean.FALSE);
				field.setUniqued(Boolean.FALSE);
				field.setViews(Views.TEXT.getView());
				column.addField(field);
			}
			column.setFieldIndex(column.getFields().size());
			template.addColumn(column);
		}
		template.setColumnIndex(template.getColumns().size());
		return template;
	}
	
	private void updateTemplate(Template template, String title, int addColumnSize) {
		template.setTitle(title);
		int idx = template.getColumnIndex();
		for (int i = idx; i < idx + addColumnSize; i++) {
			TemplateColumn column = new TemplateColumn();
			column.setColumnName("column" + i);
			column.setOrdered(i);
			column.setTitle("列" + i);
			TemplateField field = new TemplateField();
			field.setDefaultValue("");
			field.setFieldName("field0");
			field.setFieldTitle("属性0");
			field.setFieldType(Classes.STRING.getName());
			field.setFilter(Boolean.FALSE);
			field.setHidden(Boolean.FALSE);
			field.setOrdered(0);
			field.setRequired(Boolean.FALSE);
			field.setSorted(Boolean.FALSE);
			field.setUniqued(Boolean.FALSE);
			field.setViews(Views.TEXT.getView());
			column.addField(field);
			column.setFieldIndex(column.getFields().size());
			template.addColumn(column);
		}
		template.setColumnIndex(template.getColumns().size());
	}
	
}
