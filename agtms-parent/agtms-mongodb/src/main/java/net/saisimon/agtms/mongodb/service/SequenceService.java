package net.saisimon.agtms.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.mongodb.domain.Sequence;
import net.saisimon.agtms.mongodb.repository.SequenceRepository;

@Service
public class SequenceService {
	
	@Autowired
	private SequenceRepository sequenceRepository;
	
	public Long nextId(String collection) {
		Sequence sequence = sequenceRepository.findByCollection(collection);
		if (sequence == null) {
			sequence = new Sequence();
			sequence.setCollection(collection);
			sequence.setIndex(1L);
		} else {
			sequence.setIndex(sequence.getIndex() + 1);
		}
		sequenceRepository.save(sequence);
		return sequence.getIndex();
	}
	
}
