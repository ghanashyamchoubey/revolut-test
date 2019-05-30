package com.test.task.revolut;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.main.task.revolut.db.H2Datasource;

public class H2DatasourceTest {
	private Connection connection;
	@Test
	public void testGetConnection() throws SQLException {
			connection = H2Datasource.getConnection();
			assertNotNull(connection);
	}
	
}
