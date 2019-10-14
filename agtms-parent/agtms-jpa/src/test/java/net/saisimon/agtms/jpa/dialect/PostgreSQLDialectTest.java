package net.saisimon.agtms.jpa.dialect;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PostgreSQLDialectTest extends AbstractDialectTest {
	
	private Dialect dialect = new PostgreSQLDialect();
	
	@Test
	public void buildCreateSQLTest() {
		Assert.assertNull(dialect.buildCreateSQL(null, null));
		Assert.assertEquals("CREATE TABLE test (id BIGSERIAL NOT NULL, operatorId INT8 NOT NULL, createTime TIMESTAMP, updateTime TIMESTAMP, PRIMARY KEY (id))", dialect.buildCreateSQL(null, tableName()));
		Assert.assertEquals("CREATE TABLE test (id BIGSERIAL NOT NULL, column0field0 VARCHAR(512), operatorId INT8 NOT NULL, createTime TIMESTAMP, updateTime TIMESTAMP, PRIMARY KEY (id))", dialect.buildCreateSQL(fieldInfoMap(), tableName()));
	}
	
	@Test
	public void buildDropSQLTest() {
		Assert.assertNull(dialect.buildDropSQL(null));
		Assert.assertEquals("DROP TABLE test", dialect.buildDropSQL(tableName()));
	}
	
	@Test
	public void buildAlterAddSQLTest() {
		Assert.assertNull(dialect.buildAlterAddSQL(null, null, null));
		Assert.assertEquals("ALTER TABLE test ADD COLUMN column0field0 VARCHAR(512)", dialect.buildAlterAddSQL(field(), tableName(), columnName()));
	}
	
	@Test
	public void buildAlterModifySQLTest() {
		Assert.assertNull(dialect.buildAlterModifySQL(null, null, null));
		Assert.assertEquals("ALTER TABLE test ALTER COLUMN column0field0 TYPE VARCHAR(512)", dialect.buildAlterModifySQL(field(), tableName(), columnName()));
	}
	
	@Test
	public void buildAlterDropSQLTest() {
		Assert.assertNull(dialect.buildAlterDropSQL(null, null));
		Assert.assertEquals("ALTER TABLE test DROP COLUMN column0field0", dialect.buildAlterDropSQL(tableName(), columnName()));
	}
	
	@Test
	public void buildCreateIndexSQLTest() {
		Assert.assertNull(dialect.buildCreateIndexSQL(null, null, null, false));
		Assert.assertEquals("CREATE INDEX IF NOT EXISTS test_column0field0_idx ON test (column0field0)", dialect.buildCreateIndexSQL(tableName(), columnName(), indexName(), false));
		Assert.assertEquals("CREATE UNIQUE INDEX IF NOT EXISTS test_column0field0_idx ON test (column0field0)", dialect.buildCreateIndexSQL(tableName(), columnName(), indexName(), true));
	}
	
	@Test
	public void buildDropIndexSQLTest() {
		Assert.assertNull(dialect.buildDropIndexSQL(null, null));
		Assert.assertEquals("DROP INDEX IF EXISTS test_column0field0_idx", dialect.buildDropIndexSQL(tableName(), indexName()));
	}
	
	@Test
	public void wrapPageSQLTest() {
		StringBuilder sql = null;
		dialect.wrapPageSQL(sql, null);
		Assert.assertNull(sql);
		
		sql = new StringBuilder("SELECT * FROM test");
		Pageable pageable = PageRequest.of(0, 10);
		dialect.wrapPageSQL(sql, pageable);
		Assert.assertEquals("SELECT * FROM test LIMIT 10 OFFSET 0", sql.toString());
	}
	
}
