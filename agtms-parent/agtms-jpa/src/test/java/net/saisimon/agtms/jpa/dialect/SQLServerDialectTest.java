package net.saisimon.agtms.jpa.dialect;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class SQLServerDialectTest extends DialectTest {

	private Dialect dialect = new SQLServerDialect();

	@Test
	public void buildCreateSQLTest() {
		Assert.assertNull(dialect.buildCreateSQL(null, null));
		Assert.assertEquals("CREATE TABLE test (id BIGINT IDENTITY NOT NULL, operatorId BIGINT NOT NULL, createTime DATETIME2, updateTime DATETIME2, PRIMARY KEY (id))", dialect.buildCreateSQL(null, tableName()));
		Assert.assertEquals("CREATE TABLE test (id BIGINT IDENTITY NOT NULL, column0field0 VARCHAR(512), operatorId BIGINT NOT NULL, createTime DATETIME2, updateTime DATETIME2, PRIMARY KEY (id))", dialect.buildCreateSQL(fieldInfoMap(), tableName()));
	}

	@Test
	public void buildDropSQLTest() {
		Assert.assertNull(dialect.buildDropSQL(null));
		Assert.assertEquals("DROP TABLE test", dialect.buildDropSQL(tableName()));
	}

	@Test
	public void buildAlterAddSQLTest() {
		Assert.assertNull(dialect.buildAlterAddSQL(null, null, null));
		Assert.assertEquals("ALTER TABLE test ADD column0field0 VARCHAR(512)", dialect.buildAlterAddSQL(field(), tableName(), columnName()));
	}

	@Test
	public void buildAlterModifySQLTest() {
		Assert.assertNull(dialect.buildAlterModifySQL(null, null, null));
		Assert.assertEquals("ALTER TABLE test ALTER COLUMN column0field0 VARCHAR(512)", dialect.buildAlterModifySQL(field(), tableName(), columnName()));
	}

	@Test
	public void buildAlterDropSQLTest() {
		Assert.assertNull(dialect.buildAlterDropSQL(null, null));
		Assert.assertEquals("ALTER TABLE test DROP COLUMN column0field0", dialect.buildAlterDropSQL(tableName(), columnName()));
	}

	@Test
	public void wrapPageSQLTest() {
		StringBuilder sql = null;
		dialect.wrapPageSQL(sql, null);
		Assert.assertNull(sql);

		sql = new StringBuilder("SELECT * FROM test");
		Pageable pageable = PageRequest.of(0, 10);
		dialect.wrapPageSQL(sql, pageable);
		Assert.assertEquals("SELECT * FROM test OFFSET 0 ROW FETCH NEXT 10 ROW ONLY", sql.toString());
	}

}