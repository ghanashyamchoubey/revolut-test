package com.main.task.revolut.serviceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.main.task.revolut.db.H2Datasource;
import com.main.task.revolut.dto.Account;
import com.main.task.revolut.exception.AccountDetailsException;
import com.main.task.revolut.service.AccountService;
import com.main.task.revolut.util.SQLQueryUtil;
import com.main.task.revolut.util.ValidationUtil;

public class AccountServiceImpl implements AccountService {

	private static final Logger LOGGER = Logger.getLogger(AccountServiceImpl.class);

	public void createAccount(Account account) throws AccountDetailsException {

		PreparedStatement preparedStatement = null;
		Connection conn = null;

		validateAccountNumber(account.getAccountId());
		validateAccountBalance(account.getBalance());

		if (checkIfAccountExists(account.getAccountId())) {
			LOGGER.info("Account already exists.");
			throw new AccountDetailsException("Account " + account.getAccountId() + " already exists !",
					Response.Status.BAD_REQUEST);
		}
		LOGGER.info("Account creation started");
		try {
			conn = H2Datasource.getConnection();

			insertIntoAccount(conn, preparedStatement, account);

			conn.commit();
		} catch (SQLException e) {
			LOGGER.error("Account creation failed", e);
			rollback(conn);
			throw new AccountDetailsException("Account creation failed ! Try again.",
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			closeConnection(conn);
			closePreparedStatement(preparedStatement);
		}
	}

	private void insertIntoAccount(Connection conn, PreparedStatement preparedStatement, Account account)
			throws SQLException {
		preparedStatement = conn.prepareStatement(SQLQueryUtil.INSERT_INTO_ACCOUNT_TABLE_QUERY);
		preparedStatement.setString(1, account.getAccountId());
		preparedStatement.setBigDecimal(2, account.getBalance());
		preparedStatement.execute();
		LOGGER.info("Prepared Statement for create account executed successfully!");
	}

	public Account getAccountByNumber(String accountNumber) throws AccountDetailsException {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		validateAccountNumber(accountNumber);
		LOGGER.info("Getting details for the entered account number.");
		Account detailsDTO = null;
		try {
			conn = H2Datasource.getConnection();

			resultSet = getAccountDetails(conn, preparedStatement, accountNumber);
			LOGGER.info("Prepared Statement for get account details executed successfully!");

			while (resultSet.next()) {
				detailsDTO = SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet);
			}
			LOGGER.info("Result list after fetching account details" + detailsDTO);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.error("Fetching account details failed", e);
			throw new AccountDetailsException("Get by account number failed ! Try again. ",
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			closeConnection(conn);
			closePreparedStatement(preparedStatement);
			closeResultSet(resultSet);
		}
		return detailsDTO;

	}

	private ResultSet getAccountDetails(Connection conn, PreparedStatement preparedStatement, String accountNumber)
			throws SQLException {
		preparedStatement = conn.prepareStatement(SQLQueryUtil.GET_ACCOUNT_DETAILS_QUERY);
		preparedStatement.setString(1, accountNumber);

		LOGGER.info("Prepared Statement for get account details executed successfully!");
		return preparedStatement.executeQuery();

	}

	private boolean checkIfAccountExists(String accountNumber) throws AccountDetailsException {
		Account detailsDTO = getAccountByNumber(accountNumber);
		if (null != detailsDTO) {
			return true;
		}
		return false;
	}

	private void validateAccountNumber(String accountNumber) throws AccountDetailsException {
		if (!ValidationUtil.isValidAccountNumber(accountNumber)) {
			throw new AccountDetailsException("Account number should contain 5 digits with no special characters.",
					Response.Status.BAD_REQUEST);
		}

	}

	private void validateAccountBalance(BigDecimal balance) throws AccountDetailsException {
		if (!ValidationUtil.isValidAccountBalance(balance)) {
			throw new AccountDetailsException("Account balance should be greater than 0", Response.Status.BAD_REQUEST);
		}
	}

	private void rollback(Connection conn) {
		if (null != conn) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				LOGGER.error("Exception while rolling back the transaction.", e);
			}
		}
	}

	private void closeConnection(Connection conn) {
		if (null != conn) {
			try {
				conn.close();
			} catch (SQLException e) {
				LOGGER.error("Unexpected exception while closing connection ", e);
			}
		}
	}

	private void closePreparedStatement(PreparedStatement preparedStatement) {
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				LOGGER.error("Unexpected exception while closing prepared statement ", e);
			}
		}

	}

	private void closeResultSet(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				LOGGER.error("Unexpected exception while closing result set ", e);
			}
		}
	}

}
