package net.saisimon.agtms.jpa.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;
import net.saisimon.agtms.jpa.domain.TestIdClass.TestIdClassPK;

@Entity
@Data
@Table(name = "test_id_class")
@IdClass(TestIdClassPK.class)
public class TestIdClass implements Serializable {

	private static final long serialVersionUID = -5926882945564482313L;

	@Id
	@Column(length=32)
	private String name;

	@Id
	@Column(length=32)
	private String idCard;

	@Column
	private Integer age;

	@Column(length=128)
	private String remark;

	@Data
	public static class TestIdClassPK implements Serializable {

		private static final long serialVersionUID = 5055860511251396813L;

		private String name;

		private String idCard;

	}

}
