package net.saisimon.agtms.jpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "test")
public class TestLong {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(length=32)
	private String name;

	@Column(length=32)
	private String idCard;

	@Column
	private Integer age;

	@Column(length=128)
	private String remark;

}
