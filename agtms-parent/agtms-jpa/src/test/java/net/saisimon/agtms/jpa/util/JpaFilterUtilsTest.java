package net.saisimon.agtms.jpa.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Sort.Direction;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.jpa.domain.Statement;

public class JpaFilterUtilsTest {

	@Test
	public void whereTest() {
		Assert.assertNull(JpaFilterUtils.where(null));
		Statement statement = JpaFilterUtils.where(FilterRequest.build().and("column0", "column0").and("column1", "column1").or(FilterRequest.build().and("column0", "column1").and("column1", "column0")));
		Assert.assertTrue(statement.isNotEmpty());
		Assert.assertEquals(4, statement.getArgs().size());
	}

	@Test
	public void orderbyTest() {
		Assert.assertEquals(Constant.ID, JpaFilterUtils.orderby(null));
		Assert.assertEquals("column0 ASC, column1 DESC", JpaFilterUtils.orderby(FilterSort.build("column0", Direction.ASC).desc("column1")));
	}

}