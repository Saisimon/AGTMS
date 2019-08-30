package net.saisimon.agtms.jpa.service.sharding;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.shardingsphere.api.hint.HintManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
@ConditionalOnClass(HintManager.class)
public class SelectionShardingJpaService implements SelectionService, JpaOrder {
	
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
		try (HintManager hintManager = HintManager.getInstance()) {
			hintManager.addDatabaseShardingValue(SelectionOption.TABLE_NAME, selection.getOperatorId());
			return selectionOptionJpaRepository.findBySelectionId(selection.getId(), null);
		}
	}
	
	@Override
	public List<SelectionOption> getSelectionOptions(Selection selection, Set<String> values, boolean isValue) {
		try (HintManager hintManager = HintManager.getInstance()) {
			hintManager.addDatabaseShardingValue(SelectionOption.TABLE_NAME, selection.getOperatorId());
			if (isValue) {
				return selectionOptionJpaRepository.findBySelectionIdAndValueIn(selection.getId(), values);
			} else {
				return selectionOptionJpaRepository.findBySelectionIdAndTextIn(selection.getId(), values);
			}
		}
	}
	
	@Override
	public List<SelectionOption> searchSelectionOptions(Selection selection, String keyword, Integer size) {
		try (HintManager hintManager = HintManager.getInstance()) {
			hintManager.addDatabaseShardingValue(SelectionOption.TABLE_NAME, selection.getOperatorId());
			Pageable pageable = PageRequest.of(0, size);
			if (SystemUtils.isBlank(keyword)) {
				return selectionOptionJpaRepository.findBySelectionId(selection.getId(), pageable);
			} else {
				return selectionOptionJpaRepository.findBySelectionIdAndTextContaining(selection.getId(), keyword, pageable);
			}
		}
	}

	@Transactional(rollbackOn=Exception.class)
	@Override
	public void removeSelectionOptions(Selection selection) {
		try (HintManager hintManager = HintManager.getInstance()) {
			hintManager.addDatabaseShardingValue(SelectionOption.TABLE_NAME, selection.getOperatorId());
			selectionOptionJpaRepository.deleteBySelectionId(selection.getId());
		}
	}
	
	@Transactional(rollbackOn=Exception.class)
	@Override
	public void saveSelectionOptions(List<SelectionOption> options, Selection selection) {
		if (CollectionUtils.isEmpty(options)) {
			return;
		}
		try (HintManager hintManager = HintManager.getInstance()) {
			hintManager.addDatabaseShardingValue(SelectionOption.TABLE_NAME, selection.getOperatorId());
			for (SelectionOption option : options) {
				selectionOptionJpaRepository.save(option);
			}
		}
	}
	
	@Override
	public SelectionTemplate getSelectionTemplate(Selection selection) {
		try (HintManager hintManager = HintManager.getInstance()) {
			hintManager.addDatabaseShardingValue(SelectionTemplate.TABLE_NAME, selection.getOperatorId());
			return selectionTemplateJpaRepository.findBySelectionId(selection.getId());
		}
	}

	@Transactional(rollbackOn=Exception.class)
	@Override
	public void removeSelectionTemplate(Selection selection) {
		try (HintManager hintManager = HintManager.getInstance()) {
			hintManager.addDatabaseShardingValue(SelectionTemplate.TABLE_NAME, selection.getOperatorId());
			selectionTemplateJpaRepository.deleteBySelectionId(selection.getId());
		}
	}

	@Transactional(rollbackOn=Exception.class)
	@Override
	public void saveSelectionTemplate(SelectionTemplate template, Selection selection) {
		if (template == null) {
			return;
		}
		try (HintManager hintManager = HintManager.getInstance()) {
			hintManager.addDatabaseShardingValue(SelectionTemplate.TABLE_NAME, selection.getOperatorId());
			selectionTemplateJpaRepository.save(template);
		}
	}

}
