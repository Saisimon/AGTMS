package net.saisimon.agtms.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import net.saisimon.agtms.mongodb.domain.Sequence;

@Repository
public interface SequenceRepository extends MongoRepository<Sequence, String> {
	
	Sequence findByCollection(String collection);
	
}
