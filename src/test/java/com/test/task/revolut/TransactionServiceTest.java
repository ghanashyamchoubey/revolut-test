package com.test.task.revolut;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.main.task.revolut.db.H2Datasource;
import com.main.task.revolut.dto.Account;
import com.main.task.revolut.dto.Transaction;
import com.main.task.revolut.exception.TransferException;
import com.main.task.revolut.exception.ValidationException;
import com.main.task.revolut.serviceImpl.TransactionServiceImpl;
import com.main.task.revolut.util.SQLQueryUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ H2Datasource.class, SQLQueryUtil.class })
public class TransactionServiceTest {

	private Connection connection;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	private TransactionServiceImpl transactionServiceImpl;

	@Before()
	public void setup() throws SQLException {
		connection = Mockito.mock(Connection.class);
		preparedStatement = Mockito.mock(PreparedStatement.class);
		resultSet = Mockito.mock(ResultSet.class);

		PowerMockito.mockStatic(H2Datasource.class);
		PowerMockito.mockStatic(SQLQueryUtil.class);

		transactionServiceImpl = new TransactionServiceImpl();

		Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
		Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
		Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);
		Mockito.when(resultSet.next()).thenReturn(true, false, true, false, true, false, true, false, true, false, true,
				false);
	}

	@Test
	public void testTransferMoney() throws TransferException, SQLException {
		Account sender = new Account();
		Account receiver = new Account();

		sender.setAccountId("12345");
		sender.setBalance(BigDecimal.valueOf(1000L));

		receiver.setAccountId("98765");
		receiver.setBalance(BigDecimal.valueOf(2000L));

		Transaction transactionDTO = new Transaction();
		transactionDTO.setAmount(BigDecimal.valueOf(500L));
		transactionDTO.setSourceAccountId("12345");
		transactionDTO.setTargetAccountId("98765");

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(sender, receiver, sender,
				sender, receiver);

		transactionServiceImpl.transact(transactionDTO);
	}

	@Test(expected = TransferException.class)
	public void testTransferMoneyNullPointer() throws TransferException, SQLException {
		Account sender = new Account();
		Account receiver = new Account();

		sender.setAccountId("12345");

		receiver.setAccountId("98765");
		receiver.setBalance(BigDecimal.valueOf(2000L));

		Transaction transactionDTO = new Transaction();
		transactionDTO.setAmount(BigDecimal.valueOf(500L));
		transactionDTO.setSourceAccountId("12345");
		transactionDTO.setTargetAccountId("98765");

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(sender, receiver, sender,
				sender, receiver);

		transactionServiceImpl.transact(transactionDTO);
	}

	@Test(expected = TransferException.class)
	public void testTransferMoneyUpdateRowsSQLException() throws TransferException, SQLException {
		Account sender = new Account();
		Account receiver = new Account();

		sender.setAccountId("12345");
		sender.setBalance(BigDecimal.valueOf(1000L));

		receiver.setAccountId("98765");
		receiver.setBalance(BigDecimal.valueOf(2000L));

		Transaction transactionDTO = new Transaction();
		transactionDTO.setAmount(BigDecimal.valueOf(500L));
		transactionDTO.setSourceAccountId("12345");
		transactionDTO.setTargetAccountId("98765");
		Mockito.when(preparedStatement.executeUpdate()).thenReturn(2, 2);
		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(sender, receiver, sender,
				sender, receiver);

		transactionServiceImpl.transact(transactionDTO);
	}
	@Test(expected = TransferException.class)
	public void testTransferMoneyUpdateRowsTargetSQLException() throws TransferException, SQLException {
		Account sender = new Account();
		Account receiver = new Account();

		sender.setAccountId("12345");
		sender.setBalance(BigDecimal.valueOf(1000L));

		receiver.setAccountId("98765");
		receiver.setBalance(BigDecimal.valueOf(2000L));

		Transaction transactionDTO = new Transaction();
		transactionDTO.setAmount(BigDecimal.valueOf(500L));
		transactionDTO.setSourceAccountId("12345");
		transactionDTO.setTargetAccountId("98765");
		Mockito.when(preparedStatement.executeUpdate()).thenReturn(1, 2);
		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(sender, receiver, sender,
				sender, receiver);

		transactionServiceImpl.transact(transactionDTO);
	}

	@Test(expected = TransferException.class)
	public void testTransferMoneyBetweenSameAccounts() throws TransferException, SQLException {
		Account sender = new Account();

		sender.setAccountId("12345");
		sender.setBalance(BigDecimal.valueOf(1000L));

		Transaction transactionDTO = new Transaction();
		transactionDTO.setAmount(BigDecimal.valueOf(500L));
		transactionDTO.setSourceAccountId("12345");
		transactionDTO.setTargetAccountId("12345");

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(sender, sender, sender,
				sender, sender);

		transactionServiceImpl.transact(transactionDTO);
	}

	@Test(expected = TransferException.class)
	public void testTransferMoneyBetweenInvalidAccountNumbers() throws TransferException, SQLException {
		Account sender = new Account();
		Account receiver = new Account();

		sender.setAccountId("11111");
		sender.setBalance(BigDecimal.valueOf(1000L));

		receiver.setAccountId("");
		receiver.setBalance(BigDecimal.valueOf(1000L));

		Transaction transactionDTO = new Transaction();
		transactionDTO.setAmount(BigDecimal.valueOf(500L));
		transactionDTO.setSourceAccountId("12345");
		transactionDTO.setTargetAccountId("");

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(sender, receiver, sender,
				sender, receiver);

		transactionServiceImpl.transact(transactionDTO);
	}

	@Test(expected = TransferException.class)
	public void testTransferMoneyFailConnectionFailure() throws TransferException, SQLException {
		Account sender = new Account();
		Account receiver = new Account();

		sender.setAccountId("12345");
		sender.setBalance(BigDecimal.valueOf(1000L));

		receiver.setAccountId("98765");
		receiver.setBalance(BigDecimal.valueOf(2000L));

		Transaction transactionDTO = new Transaction();
		transactionDTO.setAmount(BigDecimal.valueOf(500L));
		transactionDTO.setSourceAccountId("12345");
		transactionDTO.setTargetAccountId("98765");

		Mockito.when(H2Datasource.getConnection()).thenThrow(SQLException.class);
		transactionServiceImpl.transact(transactionDTO);
	}
	

	@Test(expected = TransferException.class)
	public void testTransferMoneyBetweenLowAccountBalance() throws TransferException, SQLException {
		Account sender = new Account();
		Account receiver = new Account();

		sender.setAccountId("12345");
		sender.setBalance(BigDecimal.valueOf(0L));

		receiver.setAccountId("98765");
		receiver.setBalance(BigDecimal.valueOf(2000L));

		Transaction transactionDTO = new Transaction();
		transactionDTO.setAmount(BigDecimal.valueOf(500L));
		transactionDTO.setSourceAccountId("12345");
		transactionDTO.setTargetAccountId("98765");

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(sender, receiver, sender,
				sender, receiver);

		transactionServiceImpl.transact(transactionDTO);
	}

	@Test(expected = TransferException.class)
	public void testTransferMoneyErrorWhileGettingAccount() throws TransferException, SQLException {
		Account sender = new Account();
		Account receiver = new Account();

		sender.setAccountId("12345");
		sender.setBalance(BigDecimal.valueOf(1000L));

		receiver.setAccountId("98765");
		receiver.setBalance(BigDecimal.valueOf(2000L));

		Transaction transactionDTO = new Transaction();
		transactionDTO.setAmount(BigDecimal.valueOf(500L));
		transactionDTO.setSourceAccountId("12345");
		transactionDTO.setTargetAccountId("98765");

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(sender)
				.thenThrow(SQLException.class);

		transactionServiceImpl.transact(transactionDTO);
	}

	@Test(expected = TransferException.class)
	public void testTransferMoneyNullAccount() throws TransferException, SQLException {
		Account sender = new Account();
		Account receiver = new Account();

		sender.setAccountId("12345");
		sender.setBalance(BigDecimal.valueOf(1000L));

		receiver.setAccountId("98765");
		receiver.setBalance(BigDecimal.valueOf(2000L));

		Transaction transactionDTO = new Transaction();
		transactionDTO.setAmount(BigDecimal.valueOf(500L));
		transactionDTO.setSourceAccountId("12345");
		transactionDTO.setTargetAccountId("98765");

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(null, null, null);

		transactionServiceImpl.transact(transactionDTO);
	}

	@Test(expected = TransferException.class)
	public void testTransferMoneyErrorGettingConnection() throws SQLException, TransferException {
		Account sender = new Account();
		Account receiver = new Account();

		sender.setAccountId("12345");
		sender.setBalance(BigDecimal.valueOf(1000L));

		receiver.setAccountId("98765");
		receiver.setBalance(BigDecimal.valueOf(2000L));

		Transaction transactionDTO = new Transaction();
		transactionDTO.setAmount(BigDecimal.valueOf(500L));
		transactionDTO.setSourceAccountId("12345");
		transactionDTO.setTargetAccountId("98765");

		Mockito.when(H2Datasource.getConnection()).thenThrow(ValidationException.class);
		transactionServiceImpl.transact(transactionDTO);
	}
}
