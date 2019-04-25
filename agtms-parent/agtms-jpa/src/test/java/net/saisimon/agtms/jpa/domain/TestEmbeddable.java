package net.saisimon.agtms.jpa.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "test_embeddable")
public class TestEmbeddable implements Serializable {

	private static final long serialVersionUID = 7494706366053234979L;

	@EmbeddedId
	private TestEmbeddablePK pk;

	@Column
	private Integer age;

	@Column(length=128)
	private String remark;

	@Data
	@Embeddable
	public static class TestEmbeddablePK implements Serializable {

		private static final long serialVersionUID = 2010711133604689566L;

		@Column(length=32)
		private String name;

		@Column(length=32)
		private String idCard;

	}

}
