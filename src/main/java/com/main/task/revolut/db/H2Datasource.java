package com.main.task.revolut.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class H2Datasource {

	
	private static BasicDataSource bs;

	private static final String H2_URL = "jdbc:h2:mem:revolutdb;INIT=runscript from 'classpath:app_db_schema/table_schema.sql'";

	static {

		bs = new BasicDataSource();
		bs.setUrl(H2_URL);
		bs.setDefaultAutoCommit(false);
		bs.setInitialSize(10);
		bs.setDefaultTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
	}

	private H2Datasource() {
	}

	public static Connection getConnection() throws SQLException {
		return bs.getConnection();
	}
	

}
