package net.saisimon.agtms.web.service.edit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.SelectionOption;
import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.domain.grid.SelectionGrid;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.domain.tag.Select;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.dto.SimpleResult;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.service.SelectionService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.SelectionParam;
import net.saisimon.agtms.web.dto.req.SelectionParam.SelectionOptionParam;
import net.saisimon.agtms.web.dto.req.SelectionParam.SelectionTemplateParam;
import net.saisimon.agtms.web.selection.SelectTypeSelection;
import net.saisimon.agtms.web.selection.TemplateSelection;
import net.saisimon.agtms.web.service.common.MessageService;
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 下拉列表编辑服务
 * 
 * @author saisimon
 *
 */
@Service
public class SelectionEditService {
	
	@Autowired
	private SelectTypeSelection selectTypeSelection;
	@Autowired
	private TemplateSelection templateSelection;
	@Autowired
	private MessageService messageService;
	@Autowired
	private PremissionService premissionService;
	
	
	public Result grid(Long id) {
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		Selection selection = null;
		if (id != null) {
			selection = SelectionUtils.getSelection(id, userIds);
			if (selection == null) {
				return ErrorMessage.Selection.SELECTION_NOT_EXIST;
			}
		}
		SelectionGrid grid = new SelectionGrid();
		grid.setBreadcrumbs(breadcrumbs(id));
		Field<String> titleField = Field.<String>builder().name("title").text(messageService.getMessage("title")).type(Classes.STRING.getName()).required(true).build();
		List<Option<Integer>> selectTypeOptions = Select.buildOptions(selectTypeSelection.select());
		Field<Option<Integer>> typeField = Field.<Option<Integer>>builder().name("type").text(messageService.getMessage("type")).required(true).type("select").options(selectTypeOptions).build();
		List<Option<String>> templateOptions = Select.buildOptions(templateSelection.select());
		Field<Option<String>> templateField = Field.<Option<String>>builder().name("template").text(messageService.getMessage("template")).required(true).type("select").options(templateOptions).build();
		Field<Option<String>> templateValue = Field.<Option<String>>builder().type("select").build();
		Field<Option<String>> templateText = Field.<Option<String>>builder().type("select").build();
		List<SelectionGrid.OptionField> options = new ArrayList<>();
		if (selection != null) {
			typeField.setDisabled(true);
			typeField.setValue(Select.getOption(selectTypeOptions, selection.getType()));
			titleField.setValue(selection.getTitle());
			SelectionService selectionService = SelectionServiceFactory.get();
			if (SelectTypes.OPTION.getType().equals(selection.getType())) {
				List<SelectionOption> selectionOptions = selectionService.getSelectionOptions(selection);
				for (SelectionOption selectionOption : selectionOptions) {
					SelectionGrid.OptionField option = new SelectionGrid.OptionField();
					option.setValue(Field.builder().type(Classes.STRING.getName()).value(selectionOption.getValue()).build());
					option.setText(Field.builder().type(Classes.STRING.getName()).value(selectionOption.getText()).build());
					options.add(option);
				}
				templateField.setValue(templateOptions.get(0));
				templateValue.setOptions(new ArrayList<>());
				templateText.setOptions(new ArrayList<>());
			} else if (SelectTypes.TEMPLATE.getType().equals(selection.getType())) {
				SelectionTemplate selectionTemplate = selectionService.getSelectionTemplate(selection);
				Template template = TemplateUtils.getTemplate(selectionTemplate.getTemplateId(), userIds);
				if (template == null) {
					return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
				}
				templateField.setValue(Select.getOption(templateOptions, selectionTemplate.getTemplateId().toString()));
				List<Option<String>> domainFieldOptions = buildSelectionOptions(template);
				templateValue.setOptions(domainFieldOptions);
				templateValue.setValue(Select.getOption(domainFieldOptions, selectionTemplate.getValueFieldName()));
				templateText.setOptions(domainFieldOptions);
				templateText.setValue(Select.getOption(domainFieldOptions, selectionTemplate.getTextFieldName()));
				SelectionGrid.OptionField option = new SelectionGrid.OptionField();
				option.setValue(Field.builder().type(Classes.STRING.getName()).build());
				option.setText(Field.builder().type(Classes.STRING.getName()).build());
				options.add(option);
			}
		} else {
			typeField.setValue(selectTypeOptions.get(0));
			templateField.setValue(templateOptions.get(0));
			templateValue.setOptions(new ArrayList<>());
			templateText.setOptions(new ArrayList<>());
			SelectionGrid.OptionField option = new SelectionGrid.OptionField();
			option.setValue(Field.builder().type(Classes.STRING.getName()).build());
			option.setText(Field.builder().type(Classes.STRING.getName()).build());
			options.add(option);
		}
		grid.setTitle(titleField);
		grid.setType(typeField);
		grid.setOptions(options);
		SelectionGrid.TemplateField template = new SelectionGrid.TemplateField();
		template.setTemplate(templateField);
		template.setValue(templateValue);
		template.setText(templateText);
		grid.setTemplate(template);
		return ResultUtils.simpleSuccess(grid);
	}
	
	public Result template(Long id) {
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		Template template = TemplateUtils.getTemplate(id, userIds);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		List<Option<String>> options = buildSelectionOptions(template);
		return ResultUtils.simpleSuccess(options);
	}
	
	public Result search(String sign, String keyword) {
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		List<Option<Object>> options = SelectionUtils.getSelectionOptions(sign, keyword, userIds);
		return ResultUtils.simpleSuccess(options);
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result save(SelectionParam body) {
		if (body.getTitle().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("title"), 32);
		}
		Long userId = AuthUtils.getUid();
		Result buildResult = buildSelection(body, userId);
		if (!ResultUtils.isSuccess(buildResult)) {
			return buildResult;
		}
		@SuppressWarnings("unchecked")
		Selection selection = ((SimpleResult<Selection>) buildResult).getData();
		SelectTypes type = getSelectType(selection);
		if (type == SelectTypes.OPTION) {
			return saveSelectionOptions(body.getOptions(), selection, userId);
		} else if (type == SelectTypes.TEMPLATE) {
			return saveSelectionTemplate(body.getTemplate(), selection, userId);
		} else {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
	}

	private Result buildSelection(SelectionParam body, Long userId) {
		SelectionService selectionService = SelectionServiceFactory.get();
		Date time = new Date();
		Selection selection = null;
		if (null != body.getId() && body.getId() > 0) {
			Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
			selection = SelectionUtils.getSelection(body.getId(), userIds);
			if (selection == null) {
				return ErrorMessage.Selection.SELECTION_NOT_EXIST;
			}
			if (!body.getTitle().equals(selection.getTitle()) && selectionService.exists(body.getTitle(), userId)) {
				return ErrorMessage.Selection.SELECTION_ALREADY_EXISTS;
			}
			selection.setUpdateTime(time);
			selection.setTitle(body.getTitle());
		} else {
			if (selectionService.exists(body.getTitle(), userId)) {
				return ErrorMessage.Selection.SELECTION_ALREADY_EXISTS;
			}
			selection = new Selection();
			selection.setOperatorId(userId);
			selection.setCreateTime(time);
			selection.setUpdateTime(time);
			selection.setTitle(body.getTitle());
			selection.setType(body.getType());
		}
		return ResultUtils.simpleSuccess(selection);
	}

	private Result saveSelectionOptions(List<SelectionOptionParam> body, Selection selection, Long userId) {
		if (CollectionUtils.isEmpty(body)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		SelectionService selectionService = SelectionServiceFactory.get();
		selectionService.saveOrUpdate(selection);
		selectionService.removeSelectionOptions(selection);
		List<SelectionOption> selectionOptions = new ArrayList<>(body.size());
		Set<String> values = new HashSet<>();
		Set<String> texts = new HashSet<>();
		for (SelectionOptionParam param : body) {
			if (values.contains(param.getValue()) || texts.contains(param.getText())) {
				continue;
			} else {
				values.add(param.getValue());
				texts.add(param.getText());
			}
			SelectionOption selectionOption = new SelectionOption();
			selectionOption.setText(param.getText());
			selectionOption.setValue(param.getValue());
			selectionOption.setSelectionId(selection.getId());
			selectionOptions.add(selectionOption);
		}
		selectionService.saveSelectionOptions(selectionOptions, selection);
		return ResultUtils.simpleSuccess();
	}

	private Result saveSelectionTemplate(SelectionTemplateParam body, Selection selection, Long userId) {
		if (body == null) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		Template template = TemplateUtils.getTemplate(body.getId(), userIds);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		SelectionService selectionService = SelectionServiceFactory.get();
		selectionService.saveOrUpdate(selection);
		selectionService.removeSelectionTemplate(selection);
		SelectionTemplate selectionTemplate = new SelectionTemplate();
		selectionTemplate.setTemplateId(body.getId());
		selectionTemplate.setTextFieldName(body.getText());
		selectionTemplate.setValueFieldName(body.getValue());
		selectionTemplate.setSelectionId(selection.getId());
		selectionService.saveSelectionTemplate(selectionTemplate, selection);
		return ResultUtils.simpleSuccess();
	}

	private SelectTypes getSelectType(Selection selection) {
		if (SelectTypes.OPTION.getType().equals(selection.getType())) {
			return SelectTypes.OPTION;
		} else if (SelectTypes.TEMPLATE.getType().equals(selection.getType())) {
			return SelectTypes.TEMPLATE;
		}
		return null;
	}
	
	private List<Breadcrumb> breadcrumbs(Long id) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("system.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("selection.management")).to("/selection/main").build());
		if (id == null) {
			breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("create")).active(true).build());
		} else {
			breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("edit")).active(true).build());
		}
		return breadcrumbs;
	}
	
	private List<Option<String>> buildSelectionOptions(Template template) {
		if (template == null || CollectionUtils.isEmpty(template.getColumns())) {
			return null;
		}
		List<Option<String>> options = new ArrayList<>();
		Option<String> idOption = new Option<String>(Constant.ID, messageService.getMessage("id"));
		options.add(idOption);
		for (Template.TemplateColumn column : template.getColumns()) {
			if (CollectionUtils.isEmpty(column.getFields())) {
				continue;
			}
			for (Template.TemplateField field : column.getFields()) {
				Option<String> option = new Option<String>(column.getColumnName() + field.getFieldName(), field.getFieldTitle(), !(field.getUniqued() && field.getRequired()));
				options.add(option);
			}
		}
		return options;
	}

}
