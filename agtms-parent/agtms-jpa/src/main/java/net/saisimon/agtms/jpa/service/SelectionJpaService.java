package net.saisimon.agtms.jpa.service;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
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
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.SelectionJpaRepository;
import net.saisimon.agtms.jpa.repository.SelectionOptionJpaRepository;
import net.saisimon.agtms.jpa.repository.SelectionTemplateJpaRepository;

@Service
@ConditionalOnMissingClass("org.apache.shardingsphere.api.hint.HintManager")
public class SelectionJpaService implements SelectionService, JpaOrder {
	
	@Autowired
	private SelectionJpaRepository selectionJpaRepository;
	@Autowired
	private SelectionTemplateJpaRepository selectionTemplateJpaRepository;
	@Autowired
	private SelectionOptionJpaRepository selectionOptionJpaRepository;

	@Override
	public BaseRepository<Selection, Long> getRepository() {
		return selectionJpaRepository;
	}
	
	@Override
	public List<SelectionOption> getSelectionOptions(Selection selection) {
		return selectionOptionJpaRepository.findBySelectionId(selection.getId(), null);
	}
	
	@Override
	public List<SelectionOption> getSelectionOptions(Selection selection, Set<String> values, boolean isValue) {
		if (isValue) {
			return selectionOptionJpaRepository.findBySelectionIdAndValueIn(selection.getId(), values);
		} else {
			return selectionOptionJpaRepository.findBySelectionIdAndTextIn(selection.getId(), values);
		}
	}
	
	@Override
	public List<SelectionOption> searchSelectionOptions(Selection selection, String keyword, Integer size) {
		Pageable pageable = PageRequest.of(0, size);
		if (SystemUtils.isBlank(keyword)) {
			return selectionOptionJpaRepository.findBySelectionId(selection.getId(), pageable);
		} else {
			return selectionOptionJpaRepository.findBySelectionIdAndTextContaining(selection.getId(), keyword, pageable);
		}
	}

	@Transactional(rollbackOn=Exception.class)
	@Override
	public void removeSelectionOptions(Selection selection) {
		selectionOptionJpaRepository.deleteBySelectionId(selection.getId());
	}
	
	@Transactional(rollbackOn=Exception.class)
	@Override
	public void saveSelectionOptions(List<SelectionOption> options, Selection selection) {
		if (CollectionUtils.isEmpty(options)) {
			return;
		}
		for (SelectionOption option : options) {
			selectionOptionJpaRepository.save(option);
		}
	}
	
	@Override
	public SelectionTemplate getSelectionTemplate(Selection selection) {
		return selectionTemplateJpaRepository.findBySelectionId(selection.getId());
	}

	@Transactional(rollbackOn=Exception.class)
	@Override
	public void removeSelectionTemplate(Selection selection) {
		selectionTemplateJpaRepository.deleteBySelectionId(selection.getId());
	}

	@Transactional(rollbackOn=Exception.class)
	@Override
	public void saveSelectionTemplate(SelectionTemplate template, Selection selection) {
		if (template == null) {
			return;
		}
		selectionTemplateJpaRepository.save(template);
	}

}
