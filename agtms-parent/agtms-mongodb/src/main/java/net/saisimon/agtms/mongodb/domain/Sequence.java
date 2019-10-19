package net.saisimon.agtms.mongodb.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

/**
 * 序列对象
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
@Document(collection="agtms_sequence")
public class Sequence {
	
	private String id;
	
	private String collection;
	
	private Long index;
	
}
