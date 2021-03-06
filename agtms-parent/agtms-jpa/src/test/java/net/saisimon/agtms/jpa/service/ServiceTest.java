package net.saisimon.agtms.jpa.service;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.SelectionOption;
import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.core.domain.entity.Task;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterParam;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.service.SelectionService;
import net.saisimon.agtms.core.service.TaskService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.jpa.JpaTestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JpaTestApplication.class, properties = { "spring.main.bannerMode=OFF", "logging.level.root=ERROR" })
@DataJpaTest(showSql=false)
@JdbcTest
public class ServiceTest {
	
	@Autowired
	private TemplateService templateService;
	@Autowired
	private UserService userService;
	@Autowired
	private SelectionService selectionService;
	@Autowired
	private TaskService taskService;
	
	@Before
	public void before() {
		templateService.delete(FilterRequest.build());
		userService.delete(FilterRequest.build());
		selectionService.delete(FilterRequest.build());
		taskService.delete(FilterRequest.build());
	}
	
	@Test
	public void baseServiceTest() {
		// count
		Assert.assertEquals(0L, templateService.count(null).longValue());
		Long count = templateService.count(FilterRequest.build());
		Assert.assertEquals(0L, count.longValue());
		
		// exists
		Assert.assertFalse(templateService.exists(FilterRequest.build()));
		
		// findList
		List<Template> templates = templateService.findList(FilterRequest.build(), "title");
		Assert.assertNotNull(templates);
		Assert.assertEquals(0, templates.size());
		templates = templateService.findList(FilterRequest.build(), FilterSort.build(null), "title");
		Assert.assertNotNull(templates);
		Assert.assertEquals(0, templates.size());
		templates = templateService.findList(FilterRequest.build(), FilterPageable.build(null), "title");
		Assert.assertNotNull(templates);
		Assert.assertEquals(0, templates.size());
		
		// findList
		List<Template> list = templateService.findList(FilterRequest.build(), FilterPageable.build(null), "title");
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
		
		// findOne
		Optional<Template> optional = templateService.findOne(FilterRequest.build());
		Assert.assertFalse(optional.isPresent());
		
		// findById
		optional = templateService.findById(1L);
		Assert.assertFalse(optional.isPresent());
		
		// saveOrUpdate
		Assert.assertNull(templateService.saveOrUpdate(null));
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
		templates = templateService.findList(FilterRequest.build(), FilterSort.build("title", Direction.DESC), "title");
		Assert.assertEquals(2, templates.size());
		Assert.assertEquals("bbb", templates.get(0).getTitle());
		Assert.assertEquals("aaa", templates.get(1).getTitle());
		templates = templateService.findList(FilterRequest.build(), FilterSort.build("title", Direction.ASC), "title");
		Assert.assertEquals(2, templates.size());
		Assert.assertEquals("aaa", templates.get(0).getTitle());
		Assert.assertEquals("bbb", templates.get(1).getTitle());
		templates = templateService.findList(FilterRequest.build(), new FilterPageable(null, 10, FilterSort.build("title", Direction.DESC)), "title");
		Assert.assertEquals(2, templates.size());
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
		Assert.assertEquals("ccc", newTemplate.getTitle());
		
		// saveOrUpdate
		int addSize = 30;
		for (int i = 0; i < addSize; i++) {
			templateService.saveOrUpdate(buildTestTemplate(title + i, columnSize));
		}
		
		// count
		count = templateService.count(FilterRequest.build());
		Assert.assertEquals(32L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", title));
		Assert.assertEquals(1L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", title, Constant.Operator.NE));
		Assert.assertEquals(31L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", title, Constant.Operator.REGEX));
		Assert.assertEquals(31L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", title, Constant.Operator.LREGEX));
		Assert.assertEquals(1L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", title, Constant.Operator.RREGEX));
		Assert.assertEquals(31L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", title, Constant.Operator.NREGEX));
		Assert.assertEquals(1L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", title, Constant.Operator.LNREGEX));
		Assert.assertEquals(31L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", title, Constant.Operator.RNREGEX));
		Assert.assertEquals(1L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", new String[] {title + "0", title + "2"}, Constant.Operator.IN));
		Assert.assertEquals(2L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", Arrays.asList(title + "0", title + "2"), Constant.Operator.IN));
		Assert.assertEquals(2L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", title + "0", Constant.Operator.IN));
		Assert.assertEquals(1L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", new String[] {title + "0", title + "2"}, Constant.Operator.NIN));
		Assert.assertEquals(30L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", Arrays.asList(title + "0", title + "2"), Constant.Operator.NIN));
		Assert.assertEquals(30L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", title + "0", Constant.Operator.NIN));
		Assert.assertEquals(31L, count.longValue());
		count = templateService.count(FilterRequest.build().and(Constant.CREATETIME, new Date(), Constant.Operator.LTE));
		Assert.assertEquals(32L, count.longValue());
		count = templateService.count(FilterRequest.build().and(Constant.CREATETIME, new Date(), Constant.Operator.LT));
		Assert.assertEquals(32L, count.longValue());
		count = templateService.count(FilterRequest.build().and(Constant.CREATETIME, new Date(), Constant.Operator.GTE));
		Assert.assertEquals(0L, count.longValue());
		count = templateService.count(FilterRequest.build().and(Constant.CREATETIME, new Date(), Constant.Operator.GT));
		Assert.assertEquals(0L, count.longValue());
		count = templateService.count(FilterRequest.build().and("title", true, Constant.Operator.EXISTS));
		Assert.assertEquals(32L, count.longValue());
		count = templateService.count(FilterRequest.build().or(FilterRequest.build().and("title", title + "0")).or(FilterRequest.build().and("title", title + "40")));
		Assert.assertEquals(1L, count.longValue());
		
		// findList
		list = templateService.findList(FilterRequest.build(), (FilterPageable) null);
		Assert.assertNotNull(list);
		Assert.assertEquals(10, list.size());
		Assert.assertEquals(title + (addSize - 1), list.get(0).getTitle());
		
		// findList
		FilterPageable pageable = new FilterPageable(FilterParam.build(Constant.ID, list.get(list.size() - 1).getId(), Operator.LT), 10, null);
		list = templateService.findList(FilterRequest.build(), pageable);
		Assert.assertNotNull(list);
		Assert.assertEquals(10, list.size());
		Assert.assertEquals(title + (addSize - 11), list.get(0).getTitle());
		
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
		// exists
		Assert.assertFalse(templateService.exists(title, Arrays.asList(1L)));
		
		// getTemplate
		Assert.assertNull(TemplateUtils.getTemplate(1L));
		
		// saveOrUpdate
		int columnSize = 2;
		Template template = buildTestTemplate(title, columnSize);
		templateService.saveOrUpdate(template);
		title = "aaa";
		int addColumnSize = 3;
		updateTemplate(template, title, addColumnSize);
		templateService.saveOrUpdate(template);
		
		// getTemplate
		Assert.assertNotNull(TemplateUtils.getTemplate(template.getId()));
		
		// saveOrUpdate
		Template newTemplate = buildTestTemplate("bbb", 1);
		templateService.saveOrUpdate(newTemplate);
		
		// exists
		Assert.assertTrue(templateService.exists(title, Arrays.asList(1L)));
		
		// getTemplates
		List<Template> templates = templateService.getTemplates(Arrays.asList(1L));
		Assert.assertNotNull(templates);
		Assert.assertEquals(2, templates.size());
		
		// createTable
		boolean result = GenerateServiceFactory.build(template).createTable();
		Assert.assertTrue(result);
		
		// alterTable
		newTemplate = buildTestTemplate(title, 1);
		newTemplate.setId(template.getId());
		result = GenerateServiceFactory.build(newTemplate).alterTable(null);
		Assert.assertFalse(result);
		
		result = GenerateServiceFactory.build(newTemplate).alterTable(template);
		Assert.assertTrue(result);
		
		// dropTable
		result = GenerateServiceFactory.build(newTemplate).dropTable();
		Assert.assertTrue(result);
		
		templateService.delete(FilterRequest.build());
	}
	
	@Test
	public void userServiceTest() {
		// getUserByLoginNameOrEmail
		User user = userService.getUserByLoginNameOrEmail(null, null);
		Assert.assertNull(user);
		String username = "test";
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
		
		user = buildTestUser(username, password);
		userService.saveOrUpdate(user);
		Long uid = user.getId();
		
		// auth
		user = userService.auth(username + "aaa", password);
		Assert.assertNull(user);
		user = userService.auth(username, password);
		Assert.assertNotNull(user);
		Assert.assertEquals(uid, user.getId());
	}
	
	@Test
	public void selectionServiceTest() {
		// exists
		Assert.assertFalse(selectionService.exists("option", 1L));
		
		// getSelection
		Assert.assertNull(SelectionUtils.getSelection(1L));
		
		// saveOrUpdate
		Selection optionSelection = buildTestSelection("option", SelectTypes.OPTION);
		selectionService.saveOrUpdate(optionSelection);
		Assert.assertNotNull(optionSelection.getId());
		
		// exists
		Assert.assertTrue(selectionService.exists("option", 1L));
		
		// getSelection
		Assert.assertNotNull(SelectionUtils.getSelection(optionSelection.getId()));
		
		// saveSelectionOptions
		selectionService.saveSelectionOptions(null, optionSelection);
		List<SelectionOption> options = buildTestSelectionOptions(optionSelection.getId());
		selectionService.saveSelectionOptions(options, optionSelection);
		
		// getSelectionOptions
		options = selectionService.getSelectionOptions(optionSelection);
		Assert.assertEquals(2, options.size());
		
		// saveOrUpdate
		Selection templateSelection = buildTestSelection("template", SelectTypes.TEMPLATE);
		selectionService.saveOrUpdate(templateSelection);
		Assert.assertNotNull(templateSelection.getId());
		
		// saveSelectionTemplate
		selectionService.saveSelectionTemplate(null, optionSelection);
		SelectionTemplate template = buildTestSelectionTemplate(templateSelection.getId());
		selectionService.saveSelectionTemplate(template, optionSelection);
		Assert.assertNotNull(template.getId());
		
		// getSelectionTemplate
		template = selectionService.getSelectionTemplate(templateSelection);
		Assert.assertNotNull(template);
		Assert.assertEquals("column0field0", template.getTextFieldName());
		Assert.assertEquals("id", template.getValueFieldName());
	}
	
	@Test
	public void taskServiceTest() {
		// getTask
		Assert.assertNull(taskService.findById(1L).orElse(null));
		
		// saveOrUpdate
		Task task = buildTestTask();
		taskService.saveOrUpdate(task);
		Assert.assertNotNull(task.getId());
		
		// getTask
		Assert.assertNotNull(taskService.findById(task.getId()).orElse(null));
	}
	
	@Test
	public void generateServiceTest() throws GenerateException {
		String title = "xxx";
		int columnSize = 2;
		Template template = buildTestTemplate(title, columnSize);
		templateService.saveOrUpdate(template);
		GenerateService generateService = GenerateServiceFactory.build(template);
		generateService.createTable();
		
		// checkExist
		Domain domain = generateService.newGenerate();
		domain.setField("column0field0", "aaa", String.class);
		domain.setField("column1field0", "bbb", String.class);
		Assert.assertFalse(generateService.checkExist(domain, Arrays.asList(1L)));
		
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
		newDomain.setField("column0field0", "cdc", String.class);
		newDomain.setField("column1field0", "ddd", String.class);
		generateService.updateDomain(newDomain, domain, 1L);
		Assert.assertNotNull(newDomain.getField(Constant.ID));
		Assert.assertEquals(domain.getField(Constant.ID), newDomain.getField(Constant.ID));
		Assert.assertEquals(domain.getField(Constant.CREATETIME), newDomain.getField(Constant.CREATETIME));
		Assert.assertNotEquals(domain.getField(Constant.UPDATETIME), newDomain.getField(Constant.UPDATETIME));
		Assert.assertEquals("cdc", newDomain.getField("column0field0"));
		Assert.assertEquals("ddd", newDomain.getField("column1field0"));
		
		// count
		Assert.assertEquals(1L, generateService.count(FilterRequest.build()).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and("column0field0", "cdc")).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and("column0field0", "d", Constant.Operator.REGEX)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and("column0field0", "d", Constant.Operator.LREGEX)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and("column0field0", "dc", Constant.Operator.LREGEX)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and("column0field0", "d", Constant.Operator.RREGEX)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and("column0field0", "cd", Constant.Operator.RREGEX)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and("column0field0", "d", Constant.Operator.NREGEX)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and("column0field0", "d", Constant.Operator.LNREGEX)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and("column0field0", "dc", Constant.Operator.LNREGEX)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and("column0field0", "d", Constant.Operator.RNREGEX)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and("column0field0", "cd", Constant.Operator.RNREGEX)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and("column0field0", "true", Constant.Operator.EXISTS)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and("column0field0", "cdc", Constant.Operator.NE)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and("column0field0", "cdc", Constant.Operator.IN)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and("column0field0", new String[] {"cdc"}, Constant.Operator.IN)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and("column0field0", Arrays.asList("cdc"), Constant.Operator.IN)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and("column0field0", "cdc", Constant.Operator.NIN)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and("column0field0", new String[] {"cdc"}, Constant.Operator.NIN)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and("column0field0", Arrays.asList("cdc"), Constant.Operator.NIN)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and("column0field0", "c", Constant.Operator.GTE)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and("column0field0", "c", Constant.Operator.GT)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and("column0field0", "c", Constant.Operator.LTE)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and("column0field0", "c", Constant.Operator.LT)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and(Constant.CREATETIME, new Date(), Constant.Operator.GTE)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and(Constant.CREATETIME, new Date(), Constant.Operator.GT)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and(Constant.CREATETIME, new Date(), Constant.Operator.LTE)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and(Constant.CREATETIME, new Date(), Constant.Operator.LT)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and(Constant.OPERATORID, 2, Constant.Operator.GTE)).longValue());
		Assert.assertEquals(0L, generateService.count(FilterRequest.build().and(Constant.OPERATORID, 2, Constant.Operator.GT)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and(Constant.OPERATORID, 2, Constant.Operator.LTE)).longValue());
		Assert.assertEquals(1L, generateService.count(FilterRequest.build().and(Constant.OPERATORID, 2, Constant.Operator.LT)).longValue());
		
		// findList
		List<Domain> domains = generateService.findList(FilterRequest.build(), (FilterSort) null);
		Assert.assertNotNull(domains);
		Assert.assertEquals(1, domains.size());
		
		// findPage
		Page<Domain> page = generateService.findPage(FilterRequest.build(), null, true);
		Assert.assertNotNull(page);
		Assert.assertNotNull(page.getContent());
		Assert.assertEquals(1, page.getContent().size());
		Assert.assertEquals(1, page.getTotalElements());
		Assert.assertEquals("cdc", page.getContent().get(0).getField("column0field0"));
		
		// findList
		List<Domain> list = generateService.findList(FilterRequest.build(), (FilterPageable) null);
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertEquals("cdc", list.get(0).getField("column0field0"));
		
		// findOne
		FilterRequest filter = FilterRequest.build().and("column0field0", "cdc");
		Optional<Domain> optional = generateService.findOne(filter, null);
		Assert.assertTrue(optional.isPresent());
		Assert.assertEquals("cdc", optional.get().getField("column0field0"));
		
		// findById
		domain = generateService.findById(1L, Arrays.asList(2L));
		Assert.assertNull(domain);
		domain = generateService.findById(1L, Arrays.asList(1L));
		Assert.assertNotNull(domain);
		Assert.assertEquals("cdc", domain.getField("column0field0"));
		Assert.assertEquals("ddd", domain.getField("column1field0"));
		
		// updateDomain
		Map<String, Object> updateMap = new HashMap<>();
		updateMap.put("column1field0", "eee");
		generateService.updateDomain(1L, updateMap);
		domain = generateService.findById(1L, Arrays.asList(1L));
		Assert.assertNotNull(domain);
		Assert.assertEquals("eee", domain.getField("column1field0"));
		
		// delete
		generateService.delete(domain);
		Assert.assertEquals(0L, generateService.count(FilterRequest.build()).longValue());
		
		domain = generateService.newGenerate();
		domain.setField("column0field0", "bbb", String.class);
		domain.setField("column1field0", "cdc", String.class);
		generateService.saveDomain(domain, 1L);
		Assert.assertEquals(1L, generateService.count(FilterRequest.build()).longValue());
		// delete
		generateService.delete(FilterRequest.build().and("column0field0", "bbb"));
		Assert.assertEquals(0L, generateService.count(FilterRequest.build()).longValue());
		
		templateService.delete(template);
		generateService.dropTable();
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
		template.setPath("");
		template.setOperatorId(1L);
		template.setSource("jpa");
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
				field.setFieldType(Classes.STRING.getKey());
				field.setFilter(Boolean.FALSE);
				field.setHidden(Boolean.FALSE);
				field.setOrdered(j);
				field.setRequired(Boolean.FALSE);
				field.setSorted(Boolean.FALSE);
				field.setUniqued(Boolean.FALSE);
				field.setViews(Views.TEXT.getKey());
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
			field.setFieldType(Classes.STRING.getKey());
			field.setFilter(Boolean.FALSE);
			field.setHidden(Boolean.FALSE);
			field.setOrdered(0);
			field.setRequired(Boolean.FALSE);
			field.setSorted(Boolean.FALSE);
			field.setUniqued(Boolean.FALSE);
			field.setViews(Views.TEXT.getKey());
			column.addField(field);
			column.setFieldIndex(column.getFields().size());
			template.addColumn(column);
		}
		template.setColumnIndex(template.getColumns().size());
	}
	
	private Task buildTestTask() {
		Task task = new Task();
		Date time = new Date();
		task.setHandleResult("success");
		task.setHandleStatus(HandleStatuses.SUCCESS.getStatus());
		task.setOperatorId(1L);
		task.setTaskParam("{}");
		task.setHandleTime(time);
		task.setTaskTime(time);
		task.setTaskType("export");
		return task;
	}
	
	private User buildTestUser(String username, String password) {
		User user = new User();
		user.setLoginName(username);
		String salt = AuthUtils.createSalt();
		String hmacPwd = AuthUtils.hmac(password, salt);
		Date time = new Date();
		user.setNickname(username);
		user.setRemark(username);
		user.setCellphone(username);
		user.setEmail(username);
		user.setCreateTime(time);
		user.setUpdateTime(time);
		user.setSalt(salt);
		user.setPassword(hmacPwd);
		user.setStatus(UserStatuses.NORMAL.getStatus());
		return user;
	}
	
}
