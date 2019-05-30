package com.main.task.revolut.serviceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.main.task.revolut.db.H2Datasource;
import com.main.task.revolut.dto.Account;
import com.main.task.revolut.dto.Transaction;
import com.main.task.revolut.exception.TransferException;
import com.main.task.revolut.exception.ValidationException;
import com.main.task.revolut.service.TransactionService;
import com.main.task.revolut.util.SQLQueryUtil;
import com.main.task.revolut.util.ValidationUtil;

public class TransactionServiceImpl implements TransactionService {

	private static final Logger LOGGER = Logger.getLogger(TransactionServiceImpl.class);

	/**
	 * Concurrency Management : Optimistic locking is achieved when the DB has the control to update the
	 * rows. Updates are handled by DB it makes sure that the amount is calculated
	 * and updated based on the current account balance so in cases of concurrent requests
	 * the updates would always check for the existing balance of the source account.
	 */
	public void transact(Transaction transactionDetailsDTO) throws TransferException {
		try {
			LOGGER.info("Account Validation Started.");
			validAccountDetails(transactionDetailsDTO.getSourceAccountId(), transactionDetailsDTO.getTargetAccountId(),
					transactionDetailsDTO.getAmount());
			LOGGER.info("Transaction started.");
			Account sourceAccountDetails = getAccountDetails(transactionDetailsDTO.getSourceAccountId());
			Account targetAccountDetails = getAccountDetails(transactionDetailsDTO.getTargetAccountId());
			transferMoney(sourceAccountDetails, targetAccountDetails, transactionDetailsDTO.getAmount());
		} catch (ValidationException e) {
			LOGGER.error("Account Validation failure :: ", e);
			throw new TransferException("Account Validation failure :: " + e.getMessage(), Response.Status.BAD_REQUEST);
		}
	}

	public void transferMoney(Account sourceAccountDetails, Account targetAccountDetails, BigDecimal transactionAmount)
			throws TransferException, ValidationException {
		Connection conn = null;
		try {
			conn = H2Datasource.getConnection();

			LOGGER.info("Updating the source account with updated details.");
			updateSourceAccount(conn, sourceAccountDetails, transactionAmount);
			LOGGER.info("Updating the target account with updated details.");
			updateTargetAccount(conn, targetAccountDetails, transactionAmount);
			LOGGER.info("Inserting the transaction details in transaction table.");
			insertIntoTransaction(conn, sourceAccountDetails.getAccountId(), targetAccountDetails.getAccountId(),
					transactionAmount);

			conn.commit();

		} catch (RuntimeException | SQLException e) {
			rollback(conn);
			throw new TransferException("Transaction Failed. Please try again!", Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			closeConnection(conn);
		}
	}

	private void insertIntoTransaction(Connection conn, String sourceAccountId, String targetAccountId,
			BigDecimal transactionAmount) throws TransferException {
		PreparedStatement preparedStatementInsertToTransaction = null;
		try {
			preparedStatementInsertToTransaction = conn
					.prepareStatement(SQLQueryUtil.INSERT_INTO_TRANSACTION_TABLE_QUERY);
			preparedStatementInsertToTransaction.setString(1, UUID.randomUUID().toString());
			preparedStatementInsertToTransaction.setString(2, sourceAccountId);
			preparedStatementInsertToTransaction.setString(3, targetAccountId);
			preparedStatementInsertToTransaction.setBigDecimal(4, transactionAmount);
			preparedStatementInsertToTransaction.executeUpdate();
			LOGGER.info("Entry made into transaction table.");
		} catch (SQLException e) {
			throw new TransferException("Transaction Failed. Please try again!", Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			closePreparedStatement(preparedStatementInsertToTransaction);
		}
	}

	private void updateSourceAccount(Connection conn, Account sourceAccountDetails, BigDecimal transactionAmount)
			throws TransferException {
		PreparedStatement preparedStatementUpdatesourceAccount = null;
		try {
			preparedStatementUpdatesourceAccount = conn.prepareStatement(SQLQueryUtil.UPDATE_DEBIT_ACCOUNT_TABLE_QUERY);
			preparedStatementUpdatesourceAccount.setBigDecimal(1, transactionAmount);
			preparedStatementUpdatesourceAccount.setString(2, sourceAccountDetails.getAccountId());
			preparedStatementUpdatesourceAccount.setBigDecimal(3, transactionAmount);

			if (preparedStatementUpdatesourceAccount.executeUpdate() != 1) {
				throw new TransferException("Transaction Failed. Please try again!",
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		} catch (SQLException e) {
			throw new TransferException("Transaction Failed. Please try again!", Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			closePreparedStatement(preparedStatementUpdatesourceAccount);
		}
	}

	private void updateTargetAccount(Connection conn, Account targetAccountDetails, BigDecimal transactionAmount)
			throws TransferException {
		PreparedStatement preparedStatementUpdateTargetAccount = null;
		try {
			preparedStatementUpdateTargetAccount = conn
					.prepareStatement(SQLQueryUtil.UPDATE_CREDIT_ACCOUNT_TABLE_QUERY);
			preparedStatementUpdateTargetAccount.setBigDecimal(1, transactionAmount);
			preparedStatementUpdateTargetAccount.setString(2, targetAccountDetails.getAccountId());

			if (preparedStatementUpdateTargetAccount.executeUpdate() != 1) {
				throw new TransferException("Transaction Failed. Please try again!",
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		} catch (SQLException e) {
			throw new TransferException("Transaction Failed. Please try again!", Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			closePreparedStatement(preparedStatementUpdateTargetAccount);
		}

	}

	private Account getAccountDetails(String targetAccountId) throws TransferException, ValidationException {
		Account detailsDTO = null;
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = H2Datasource.getConnection();
			preparedStatement = conn.prepareStatement(SQLQueryUtil.GET_BY_ID_QUERY_FOR_TRANSACTION);
			preparedStatement.setString(1, targetAccountId);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				detailsDTO = SQLQueryUtil.extractAccountDetailsFromResultSet(rs);
			}
			if (null == detailsDTO) {
				LOGGER.error("Account Validation failure :: ");
				throw new ValidationException("Account doesnt exist.", Response.Status.BAD_REQUEST);
			}
		} catch (SQLException e) {
			throw new TransferException("Fetching account details failed ! Try again.",
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			closeConnection(conn);
			closePreparedStatement(preparedStatement);

		}
		return detailsDTO;
	}

	private BigDecimal checkBalance(String accountId, BigDecimal amount) throws ValidationException, TransferException {
		BigDecimal accountBalance = BigDecimal.ZERO;
		try {
			accountBalance = getAccountDetails(accountId).getBalance();
			if (accountBalance.compareTo(BigDecimal.ZERO) < 0 || accountBalance.compareTo(amount) < 0) {
				throw new ValidationException("Low account balance. Cannot initiate transfer !",
						Response.Status.BAD_REQUEST);
			}
		} catch (NullPointerException e) {
			LOGGER.error("Unable to fetch account details :: ", e);
			throw new TransferException("Unable to fetch account details :: " + e.getMessage(),
					Response.Status.BAD_REQUEST);
		}
		return accountBalance;
	}

	private void validAccountDetails(String sourceAccountId, String targetAccountId, BigDecimal transactionAmount)
			throws ValidationException, TransferException {
		if (!ValidationUtil.isValidAccountNumber(targetAccountId)
				|| !ValidationUtil.isValidAccountNumber(sourceAccountId)) {
			throw new ValidationException("Account numbers entered for transaction are not valid",
					Response.Status.BAD_REQUEST);
		}
		if (targetAccountId.equals(sourceAccountId)) {
			throw new ValidationException("Account numbers of the source and target should be different",
					Response.Status.BAD_REQUEST);
		}
		checkBalance(sourceAccountId, transactionAmount);
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
				LOGGER.error("Exception while closing the connection.", e);
			}
		}
	}

	private void closePreparedStatement(PreparedStatement preparedStatement) {
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				LOGGER.error("Unexpected exception while closing prepared statement", e);
			}
		}
	}

}
