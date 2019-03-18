package net.saisimon.agtms.jpa.service;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

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
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.SelectionJpaRepository;
import net.saisimon.agtms.jpa.repository.SelectionOptionJpaRepository;
import net.saisimon.agtms.jpa.repository.SelectionTemplateJpaRepository;

@Service
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
	public List<SelectionOption> getSelectionOptions(Long selectionId) {
		return selectionOptionJpaRepository.findBySelectionId(selectionId, null);
	}
	
	@Override
	public List<SelectionOption> getSelectionOptions(Long selectionId, Set<String> values, boolean isValue) {
		if (isValue) {
			return selectionOptionJpaRepository.findBySelectionIdAndValueIn(selectionId, values);
		} else {
			return selectionOptionJpaRepository.findBySelectionIdAndTextIn(selectionId, values);
		}
	}
	
	@Override
	public List<SelectionOption> searchSelectionOptions(Long selectionId, String keyword, Integer size) {
		Pageable pageable = PageRequest.of(0, size);
		if (StringUtils.isBlank(keyword)) {
			return selectionOptionJpaRepository.findBySelectionId(selectionId, pageable);
		} else {
			return selectionOptionJpaRepository.findBySelectionIdAndTextContaining(selectionId, keyword, pageable);
		}
	}

	@Transactional
	@Override
	public void removeSelectionOptions(Long selectionId) {
		selectionOptionJpaRepository.deleteBySelectionId(selectionId);
	}
	
	@Transactional
	@Override
	public void saveSelectionOptions(List<SelectionOption> options) {
		if (CollectionUtils.isEmpty(options)) {
			return;
		}
		for (SelectionOption option : options) {
			selectionOptionJpaRepository.save(option);
		}
	}
	
	@Override
	public SelectionTemplate getSelectionTemplate(Long selectionId) {
		return selectionTemplateJpaRepository.findBySelectionId(selectionId);
	}

	@Transactional
	@Override
	public void removeSelectionTemplate(Long selectionId) {
		selectionTemplateJpaRepository.deleteBySelectionId(selectionId);
	}

	@Transactional
	@Override
	public void saveSelectionTemplate(SelectionTemplate template) {
		if (template == null) {
			return;
		}
		selectionTemplateJpaRepository.save(template);
	}

}
