package net.saisimon.agtms.web.controller.edit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
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
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.service.SelectionService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.BaseController;
import net.saisimon.agtms.web.dto.req.SelectionParam;
import net.saisimon.agtms.web.dto.req.SelectionParam.SelectionOptionParam;
import net.saisimon.agtms.web.selection.SelectTypeSelection;
import net.saisimon.agtms.web.selection.TemplateSelection;

/**
 * 下拉列表编辑控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/selection/edit")
@ControllerInfo("selection.management")
public class SelectionEditController extends BaseController {
	
	@Autowired
	private SelectTypeSelection selectTypeSelection;
	@Autowired
	private TemplateSelection templateSelection;
	
	@PostMapping("/grid")
	public Result grid(@RequestParam(name = "id", required = false) Long id) {
		Selection selection = null;
		Long userId = AuthUtils.getUid();
		if (id != null) {
			selection = SelectionUtils.getSelection(id, userId);
			if (selection == null) {
				return ErrorMessage.Selection.SELECTION_NOT_EXIST;
			}
		}
		SelectionGrid grid = new SelectionGrid();
		grid.setBreadcrumbs(breadcrumbs(id));
		Field<String> titleField = Field.<String>builder().name("title").text(getMessage("title")).type(Classes.STRING.getName()).required(true).build();
		List<Option<Integer>> selectTypeOptions = Select.buildOptions(selectTypeSelection.select());
		Field<Option<Integer>> typeField = Field.<Option<Integer>>builder().name("type").text(getMessage("type")).required(true).type("select").options(selectTypeOptions).build();
		List<Option<Long>> templateOptions = Select.buildOptions(templateSelection.select());
		Field<Option<Long>> templateField = Field.<Option<Long>>builder().name("template").text(getMessage("template")).required(true).type("select").options(templateOptions).build();
		Field<Option<String>> templateValue = Field.<Option<String>>builder().type("select").build();
		Field<Option<String>> templateText = Field.<Option<String>>builder().type("select").build();
		List<SelectionGrid.OptionField> options = new ArrayList<>();
		if (selection != null) {
			typeField.setDisabled(true);
			typeField.setValue(Select.getOption(selectTypeOptions, selection.getType()));
			titleField.setValue(selection.getTitle());
			SelectionService selectionService = SelectionServiceFactory.get();
			if (SelectTypes.OPTION.getType().equals(selection.getType())) {
				List<SelectionOption> selectionOptions = selectionService.getSelectionOptions(selection.getId());
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
				SelectionTemplate selectionTemplate = selectionService.getSelectionTemplate(selection.getId());
				Template template = TemplateUtils.getTemplate(selectionTemplate.getTemplateId(), userId);
				if (template == null) {
					return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
				}
				templateField.setValue(Select.getOption(templateOptions, selectionTemplate.getTemplateId()));
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
	
	@PostMapping("/template")
	public Result template(@RequestParam(name = "id") Long id) {
		Long userId = AuthUtils.getUid();
		Template template = TemplateUtils.getTemplate(id, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		List<Option<String>> options = buildSelectionOptions(template);
		return ResultUtils.simpleSuccess(options);
	}
	
	@PostMapping("/search")
	public Result search(@RequestParam(name = "sign") String sign, @RequestParam(name = "keyword", required = false) String keyword) {
		List<Option<Object>> options = SelectionUtils.getSelectionOptions(sign, keyword, AuthUtils.getUid());
		return ResultUtils.simpleSuccess(options);
	}
	
	@Operate(type=OperateTypes.EDIT)
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/save")
	public Result save(@Validated @RequestBody SelectionParam body, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		if (body.getTitle().length() > 50) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("title"), 50);
		}
		SelectionService selectionService = SelectionServiceFactory.get();
		Long userId = AuthUtils.getUid();
		Long id = body.getId();
		Date time = new Date();
		Selection selection = null;
		if (null != id && id > 0) {
			selection = SelectionUtils.getSelection(id, userId);
			if (selection == null) {
				return ErrorMessage.Selection.SELECTION_NOT_EXIST;
			}
			if (!body.getTitle().equals(selection.getTitle())) {
				if (selectionService.exists(body.getTitle(), userId)) {
					return ErrorMessage.Selection.SELECTION_ALREADY_EXISTS;
				}
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
		SelectTypes type = getSelectType(selection);
		if (type == null) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		if (type == SelectTypes.OPTION) {
			if (CollectionUtils.isEmpty(body.getOptions())) {
				return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
			}
			selectionService.saveOrUpdate(selection);
			selectionService.removeSelectionOptions(selection.getId());
			List<SelectionOption> selectionOptions = new ArrayList<>(body.getOptions().size());
			Set<String> values = new HashSet<>();
			Set<String> texts = new HashSet<>();
			for (SelectionOptionParam param : body.getOptions()) {
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
			selectionService.saveSelectionOptions(selectionOptions);
		} else if (type == SelectTypes.TEMPLATE) {
			if (body.getTemplate() == null) {
				return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
			}
			Template template = TemplateUtils.getTemplate(body.getTemplate().getId(), userId);
			if (template == null) {
				return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
			}
			selectionService.saveOrUpdate(selection);
			selectionService.removeSelectionTemplate(selection.getId());
			SelectionTemplate selectionTemplate = new SelectionTemplate();
			selectionTemplate.setTemplateId(body.getTemplate().getId());
			selectionTemplate.setTextFieldName(body.getTemplate().getText());
			selectionTemplate.setValueFieldName(body.getTemplate().getValue());
			selectionTemplate.setSelectionId(selection.getId());
			selectionService.saveSelectionTemplate(selectionTemplate);
		}
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
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("system.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("selection.management")).to("/selection/main").build());
		if (id == null) {
			breadcrumbs.add(Breadcrumb.builder().text(getMessage("create")).active(true).build());
		} else {
			breadcrumbs.add(Breadcrumb.builder().text(getMessage("edit")).active(true).build());
		}
		return breadcrumbs;
	}
	
	private List<Option<String>> buildSelectionOptions(Template template) {
		if (template == null || CollectionUtils.isEmpty(template.getColumns())) {
			return null;
		}
		List<Option<String>> options = new ArrayList<>();
		Option<String> idOption = new Option<String>(Constant.ID, getMessage("id"));
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
