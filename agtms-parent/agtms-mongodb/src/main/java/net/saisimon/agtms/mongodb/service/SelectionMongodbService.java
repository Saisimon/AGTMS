package net.saisimon.agtms.mongodb.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.SelectionOption;
import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.SelectionService;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.mongodb.order.MongodbOrder;
import net.saisimon.agtms.mongodb.repository.SelectionMongodbRepository;
import net.saisimon.agtms.mongodb.repository.SelectionOptionMongodbRepository;
import net.saisimon.agtms.mongodb.repository.SelectionTemplateMongodbRepository;

@Service
public class SelectionMongodbService implements SelectionService, MongodbOrder {
	
	@Autowired
	private SelectionMongodbRepository selectionMongodbRepository;
	@Autowired
	private SelectionTemplateMongodbRepository selectionTemplateMongodbRepository;
	@Autowired
	private SelectionOptionMongodbRepository selectionOptionMongodbRepository;
	@Autowired
	private SequenceService sequenceService;

	@Override
	public BaseRepository<Selection, Long> getRepository() {
		return selectionMongodbRepository;
	}
	
	@Override
	public List<SelectionOption> getSelectionOptions(Selection selection) {
		return selectionOptionMongodbRepository.findBySelectionId(selection.getId(), null);
	}
	
	@Override
	public List<SelectionOption> getSelectionOptions(Selection selection, Set<String> values, boolean isValue) {
		if (isValue) {
			return selectionOptionMongodbRepository.findBySelectionIdAndValueIn(selection.getId(), values);
		} else {
			return selectionOptionMongodbRepository.findBySelectionIdAndTextIn(selection.getId(), values);
		}
	}
	
	@Override
	public List<SelectionOption> searchSelectionOptions(Selection selection, String keyword, Integer size) {
		Pageable pageable = PageRequest.of(0, size);
		if (SystemUtils.isBlank(keyword)) {
			return selectionOptionMongodbRepository.findBySelectionId(selection.getId(), pageable);
		} else {
			return selectionOptionMongodbRepository.findBySelectionIdAndTextContaining(selection.getId(), keyword, pageable);
		}
	}

	@Override
	public void removeSelectionOptions(Selection selection) {
		selectionOptionMongodbRepository.deleteBySelectionId(selection.getId());
	}
	
	@Override
	public void saveSelectionOptions(List<SelectionOption> options, Selection selection) {
		if (CollectionUtils.isEmpty(options)) {
			return;
		}
		for (SelectionOption option : options) {
			if (option.getId() == null) {
				Long id = sequenceService.nextId(selectionOptionMongodbRepository.getCollectionName());
				option.setId(id);
			}
			selectionOptionMongodbRepository.save(option);
		}
	}
	
	@Override
	public SelectionTemplate getSelectionTemplate(Selection selection) {
		return selectionTemplateMongodbRepository.findBySelectionId(selection.getId());
	}

	@Override
	public void removeSelectionTemplate(Selection selection) {
		selectionTemplateMongodbRepository.deleteBySelectionId(selection.getId());
	}

	@Override
	public void saveSelectionTemplate(SelectionTemplate template, Selection selection) {
		if (template == null) {
			return;
		}
		if (template.getId() == null) {
			Long id = sequenceService.nextId(selectionTemplateMongodbRepository.getCollectionName());
			template.setId(id);
		}
		selectionTemplateMongodbRepository.save(template);
	}
	
	@Override
	public Selection saveOrUpdate(Selection entity) {
		if (entity == null) {
			return entity;
		}
		if (entity.getId() == null) {
			Long id = sequenceService.nextId(selectionMongodbRepository.getCollectionName());
			entity.setId(id);
		}
		return selectionMongodbRepository.saveOrUpdate(entity);
	}

}
