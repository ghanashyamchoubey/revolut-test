package com.test.task.revolut;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.main.task.revolut.util.SQLQueryUtil;


public class SQLQueryUtilTest {

	private ResultSet rs;
	
	@Before
	public void init() {
		rs = Mockito.mock(ResultSet.class);
	}
	
	@Test
	public void testExtractMethod() {
		try {
			assertNotNull(SQLQueryUtil.extractAccountDetailsFromResultSet(rs));
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}
	
	
}
