package net.saisimon.agtms.web.controller.edit;

import java.util.Date;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.date.DateUtil;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.enums.DataSources;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.TokenUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.resp.common.Result;
import net.saisimon.agtms.web.util.ResultUtils;

@RestController
@RequestMapping("/template/edit")
public class TemplateEditController {
	
	@PostMapping("/info")
	public Result info(@RequestParam("id") Long id) {
		long userId = TokenUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(id, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		return ResultUtils.success(template);
	}
	
	@PostMapping("/save")
	public Result save(@RequestParam(name = "id", required = false) Long id, @RequestBody Template template) throws GenerateException {
		if (!checkRequired(template)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		sort(template);
		long userId = TokenUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		if (id != null) {
			Template oldTemplate = templateService.getTemplate(id, userId);
			if (oldTemplate == null) {
				return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
			}
			String time = DateUtil.formatDateTime(new Date());
			template.setCreateTime(oldTemplate.getCreateTime());
			template.setUserId(oldTemplate.getUserId());
			template.setId(oldTemplate.getId());
			template.setUpdateTime(time);
			templateService.alterTable(template, oldTemplate);
			templateService.saveOrUpdate(template);
		} else {
			if (templateService.exists(template.getTitle(), userId)) {
				return ErrorMessage.Template.TEMPLATE_ALREADY_EXISTS;
			}
			String time = DateUtil.formatDateTime(new Date());
			template.setCreateTime(time);
			template.setUserId(userId);
			template.setUpdateTime(time);
			templateService.createTable(template);
			templateService.saveOrUpdate(template);
		}
		return ResultUtils.success();
	}
	
	private boolean checkRequired(Template template) {
		if (StringUtils.isBlank(template.getTitle())) {
			return false;
		}
		if (CollectionUtils.isEmpty(template.getColumns())) {
			return false;
		}
		if (DataSources.RPC.getSource().equals(template.getSource())) {
			if (StringUtils.isBlank(template.getSourceUrl())) {
				return false;
			}
		} else {
			template.setSourceUrl("");
		}
		return true;
	}
	
	private void sort(Template template) {
		if (!CollectionUtils.isEmpty(template.getColumns())) {
			template.getColumns().stream().sorted((pc1, pc2) -> {
				if (pc1.getColumnId() > pc2.getColumnId()) {
					return 1;
				} else if (pc1.getColumnId() < pc2.getColumnId()) {
					return -1;
				} else {
					return 0;
				}
			}).forEach(pageColumn -> {
				if (!CollectionUtils.isEmpty(pageColumn.getFields())) {
					pageColumn.getFields().stream().sorted((pf1, pf2) -> {
						if (pf1.getFieldId() > pf2.getFieldId()) {
							return 1;
						} else if (pf1.getFieldId() < pf2.getFieldId()) {
							return -1;
						} else {
							return 0;
						}
					});
				}
			});
		}
	}
	
}
