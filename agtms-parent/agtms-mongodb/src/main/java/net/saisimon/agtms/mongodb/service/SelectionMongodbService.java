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
import net.saisimon.agtms.core.util.StringUtils;
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

	@Override
	public BaseRepository<Selection, Long> getRepository() {
		return selectionMongodbRepository;
	}
	
	@Override
	public List<SelectionOption> getSelectionOptions(Long selectionId) {
		return selectionOptionMongodbRepository.findBySelectionId(selectionId, null);
	}
	
	@Override
	public List<SelectionOption> getSelectionOptions(Long selectionId, Set<String> values, Set<String> texts) {
		if (values != null) {
			return selectionOptionMongodbRepository.findBySelectionIdAndValueIn(selectionId, values);
		} else if (texts != null) {
			return selectionOptionMongodbRepository.findBySelectionIdAndTextIn(selectionId, texts);
		}
		return null;
	}
	
	@Override
	public List<SelectionOption> searchSelectionOptions(Long selectionId, String keyword, Integer size) {
		Pageable pageable = PageRequest.of(0, size);
		if (StringUtils.isBlank(keyword)) {
			return selectionOptionMongodbRepository.findBySelectionId(selectionId, pageable);
		} else {
			return selectionOptionMongodbRepository.findBySelectionIdAndTextContaining(selectionId, keyword, pageable);
		}
	}

	@Override
	public void removeSelectionOptions(Long selectionId) {
		selectionOptionMongodbRepository.deleteBySelectionId(selectionId);
	}
	
	@Override
	public void saveSelectionOptions(List<SelectionOption> options) {
		if (CollectionUtils.isEmpty(options)) {
			return;
		}
		for (SelectionOption option : options) {
			selectionOptionMongodbRepository.save(option);
		}
	}
	
	@Override
	public SelectionTemplate getSelectionTemplate(Long selectionId) {
		return selectionTemplateMongodbRepository.findBySelectionId(selectionId);
	}

	@Override
	public void removeSelectionTemplate(Long selectionId) {
		selectionTemplateMongodbRepository.deleteBySelectionId(selectionId);
	}

	@Override
	public void saveSelectionTemplate(SelectionTemplate template) {
		if (template == null) {
			return;
		}
		selectionTemplateMongodbRepository.save(template);
	}

}