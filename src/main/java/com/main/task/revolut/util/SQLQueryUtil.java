package com.main.task.revolut.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.main.task.revolut.dto.Account;

public class SQLQueryUtil {
	
	private SQLQueryUtil() {
	}

	public static final String INSERT_INTO_ACCOUNT_TABLE_QUERY = "INSERT INTO Account (accountId,balance) VALUES (?,?)";
	public static final String INSERT_INTO_TRANSACTION_TABLE_QUERY = "INSERT INTO Transaction (transactionId,sourceAccountId,targetAccountId,amount) VALUES (?,?,?,?)";
	public static final String GET_ACCOUNT_DETAILS_QUERY = "SELECT * FROM Account WHERE accountId = ?";
	public static final String GET_BY_ID_QUERY_FOR_TRANSACTION = "SELECT * FROM Account WHERE accountId = ?";

	public static final String UPDATE_DEBIT_ACCOUNT_TABLE_QUERY = "UPDATE Account SET balance = balance - ? WHERE accountId = ? and balance >= ?";
	public static final String UPDATE_CREDIT_ACCOUNT_TABLE_QUERY = "UPDATE Account SET balance = balance + ? WHERE accountId = ?";

	public static Account extractAccountDetailsFromResultSet(ResultSet rs) throws SQLException {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId(rs.getString("accountId"));
		detailsDTO.setBalance(rs.getBigDecimal("balance"));
		return detailsDTO;
	}

}
